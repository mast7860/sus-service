package com.sus.logging;

import io.micronaut.core.convert.value.MutableConvertibleValues;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;

@Filter("/**")
@Slf4j
public class LogFilter implements HttpServerFilter {

    private final LogService logService;

    public LogFilter(LogService logService) {
        this.logService = logService;
    }

    private static String getStartKey(Class<? extends LogFilter> filterClass) {
        return filterClass.getSimpleName() + ".start";
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {

        final long now = System.currentTimeMillis();
        final String attributeKey = getStartKey(getClass());
        final MutableConvertibleValues<Object> attrs = request.getAttributes();

        final long start = attrs.get(attributeKey, Long.class, now);

        return logService.trace(request)
                .switchMap(aBoolean -> chain.proceed(request))
                .doOnNext(response -> logService.logResponse(request, response, start));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}

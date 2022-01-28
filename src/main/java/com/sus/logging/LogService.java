package com.sus.logging;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Singleton
@Slf4j
public class LogService {

    private static final List<String> SKIP_LOGGING_URIS = Arrays.asList(
            "/health",
            "/prometheus"
    );

    private static boolean shouldLog(HttpRequest<?> request) {
        return SKIP_LOGGING_URIS.stream().noneMatch(request.getUri().toString()::contains);
    }

    public Flowable<Boolean> trace(HttpRequest<?> request) {

        return Flowable.fromCallable(() -> {

            if (shouldLog(request)) {
                logRequest(request);
            }
            return Boolean.TRUE;
        }).subscribeOn(Schedulers.io());
    }

    private void logRequest(HttpRequest<?> request) {

        log.info("direction=REQUEST mtd={} uri={} body={}",
                request.getMethod(),
                request.getUri(),
                request.getBody().orElse(null));
    }

    public void logResponse(HttpRequest<?> request,
                            HttpResponse<?> response,
                            long start) {

        long duration = System.currentTimeMillis() - start;
        if (shouldLog(request)) {
            if (response.getStatus().getCode() >= HttpStatus.BAD_REQUEST.getCode()) {
                log.warn("direction=RESPONSE mtd={} uri={} status={} reason='{}' durationMs={} body={}",
                        request.getMethod(),
                        request.getUri(),
                        response.getStatus().getCode(),
                        response.getStatus().getReason(),
                        duration,
                        response.getBody().orElse(null));
            } else {
                log.warn("direction=RESPONSE mtd={} uri={} status={} reason='{}' durationMs={} body={}",
                        request.getMethod(),
                        request.getUri(),
                        response.getStatus().getCode(),
                        response.getStatus().getReason(),
                        duration,
                        response.getBody().orElse(null));
            }
        }
    }

}

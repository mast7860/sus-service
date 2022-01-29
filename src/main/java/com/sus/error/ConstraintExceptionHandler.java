package com.sus.error;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor;
import jakarta.inject.Singleton;

import javax.validation.ConstraintViolationException;

@Produces
@Replaces(io.micronaut.validation.exceptions.ConstraintExceptionHandler.class)
@Requires(classes = {ConstraintViolationException.class, ExceptionHandler.class})
@Singleton
public class ConstraintExceptionHandler extends io.micronaut.validation.exceptions.ConstraintExceptionHandler {

    public ConstraintExceptionHandler(ErrorResponseProcessor<?> responseProcessor) {
        super(responseProcessor);
    }

    @Override
    public HttpResponse<?> handle(HttpRequest request, ConstraintViolationException exception) {
        return HttpResponse
                .badRequest(ErrorMessage.builder().message(exception.getMessage()).code("CE001").build());
    }
}

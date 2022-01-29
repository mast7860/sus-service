package com.sus.error;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {SusException.class, ExceptionHandler.class})
public class SusExceptionHandler implements ExceptionHandler<SusException, HttpResponse<ErrorMessage>> {

    @Override
    public HttpResponse<ErrorMessage>
    handle(HttpRequest request, SusException exception) {

        return HttpResponse.serverError(exception.getErrorMessage()).
                status(HttpStatus.BAD_REQUEST);
    }
}

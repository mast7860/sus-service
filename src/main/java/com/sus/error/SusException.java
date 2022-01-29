package com.sus.error;

import lombok.Getter;

@Getter
public class SusException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public SusException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

}

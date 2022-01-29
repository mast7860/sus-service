package com.sus.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ErrorMessage {

    private String code;

    private String message;

}

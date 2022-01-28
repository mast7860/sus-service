package com.sus.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Introspected
@Validated
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class Token {

    UUID sessionId;

    LocalDateTime startDateTime;
}

package com.sus.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Introspected
@Validated
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UsabilityResponse {

    @Min(value = 1)
    @Max(value = 10)
    @Positive
    @NotEmpty
    int questionNumber;

    @Min(value = 1)
    @Max(value = 5)
    @Positive
    @NotEmpty
    int score;
}

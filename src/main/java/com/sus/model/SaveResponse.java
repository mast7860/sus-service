package com.sus.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Introspected
@Validated
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class SaveResponse {

    String grade;

    Double percentile;
}

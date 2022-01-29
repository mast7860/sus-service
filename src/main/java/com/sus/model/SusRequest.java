package com.sus.model;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.validation.Validated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Introspected
@Validated
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class SusRequest {

    @Size(min = 10, max = 10, message = "All 10 answers should be answered")
    @NotEmpty(message = "response cannot be empty")
    List<UsabilityResponse> usabilityResponses;
}

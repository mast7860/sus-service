package com.sus.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;

@Introspected
@Data
@AllArgsConstructor
@Builder
public class Stats {

    @Nullable
    Double avgScore;

    long count;
}

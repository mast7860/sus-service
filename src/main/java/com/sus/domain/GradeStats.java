package com.sus.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Introspected
@Data
@AllArgsConstructor
@Builder
public class GradeStats {

    String grade;
    long count;
    Double avgGradeScore;
}

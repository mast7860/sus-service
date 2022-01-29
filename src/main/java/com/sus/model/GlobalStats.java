package com.sus.model;

import com.sus.domain.GradeStats;
import com.sus.domain.ResponseTimes;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class GlobalStats {

    long totalCount;

    Double percentile;

    String grade;

    List<GradeStats> stats;

    ResponseTimes responseTimes;
}

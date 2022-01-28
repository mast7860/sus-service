package com.sus.model;

import com.sus.domain.GradeStat;
import com.sus.domain.ResponseTimes;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class GlobalStats {

    List<GradeStat> stats;

    Double percentile;

    ResponseTimes responseTimes;

    String grade;
}

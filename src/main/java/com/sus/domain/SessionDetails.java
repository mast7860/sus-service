package com.sus.domain;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.time.LocalDateTime;

@Introspected
@Data
@AllArgsConstructor
@Builder
public class SessionDetails {

    String sessionId;
    LocalDateTime startTime;
    @Nullable
    LocalDateTime endTime;
    @Nullable
    Integer timeSpentInSec;
}

package com.sus.service;

import com.sus.error.SusException;
import com.sus.model.GlobalStats;
import com.sus.model.SaveResponse;
import com.sus.model.SusRequest;
import com.sus.model.Token;
import com.sus.model.UsabilityResponse;
import com.sus.repository.SusRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class SusService {

    private final SusRepository susRepository;

    @Inject
    public SusService(SusRepository susRepository) {
        this.susRepository = susRepository;
    }

    public Token generateSessionId() {

        Token token = Token.builder()
                .startDateTime(LocalDateTime.now())
                .sessionId(UUID.randomUUID())
                .build();

        susRepository.createSession(token);
        return token;
    }

    public SaveResponse saveUserResponse(SusRequest request) {

        var sessionData = susRepository.getSession(request.getToken());
        if (sessionData.getTimeSpentInSec() != null) {
            throw new SusException("duplicate session");
        }
        var oddSum = request.getUsabilityResponses().stream().filter(scoreCard -> scoreCard.getQuestionNumber() % 2 != 0)
                .mapToInt(UsabilityResponse::getScore).sum();
        var evenSum = request.getUsabilityResponses().stream().filter(scoreCard -> scoreCard.getQuestionNumber() % 2 == 0)
                .mapToInt(UsabilityResponse::getScore).sum();

        var totalSum = ((oddSum - 5) + (25 - evenSum));
        log.debug("totalSum=" + totalSum);

        var percentile = totalSum * 2.5;
        log.debug("percentile=" + percentile);

        var answers = request.getUsabilityResponses().stream()
                .map(usabilityResponse -> usabilityResponse.getQuestionNumber() + "=" + usabilityResponse.getScore())
                .collect(Collectors.joining(","));

        log.debug("answers=" + answers);

        var grade = calculateGrade(percentile);
        log.debug("grade=" + grade);

        susRepository.updateSession(sessionData);
        susRepository.saveScores(request, percentile, grade, answers);

        return SaveResponse.builder().percentile(percentile).grade(grade).build();

    }

    public GlobalStats getResponseTimes() {
        var responseTimes = susRepository.getResponseTimes(LocalDate.now(), LocalDate.now());
        var averagePercentile = susRepository.getAveragePercentile(LocalDate.now(), LocalDate.now());
        var gradeStats = susRepository.getGradeCount(LocalDate.now(), LocalDate.now());

        return GlobalStats
                .builder()
                .percentile(averagePercentile)
                .grade(calculateGrade(averagePercentile))
                .responseTimes(responseTimes)
                .stats(gradeStats)
                .build();

    }

    private String calculateGrade(Double percentile) {

        if (percentile >= 80.3)
            return "A";
        else if (percentile > 68)
            return "B";
        else if (percentile == 68)
            return "C";
        else if (percentile >= 51)
            return "D";
        else if (percentile < 51)
            return "F";
        else
            return "Z";

    }
}

package com.sus.service;

import com.sus.error.ErrorMessage;
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

import static com.sus.utils.Utils.calculateGrade;

@Slf4j
@Singleton
@SuppressWarnings("ClassCanBeRecord")
public class SusService {

    private final SusRepository susRepository;

    @Inject
    public SusService(SusRepository susRepository) {
        this.susRepository = susRepository;
    }

    public Token generateSessionId() {

        Token token = Token.builder()
                .startDateTime(LocalDateTime.now())
                .sessionId(UUID.randomUUID().toString())
                .build();

        susRepository.createSession(token);
        return token;
    }

    public SaveResponse saveUserResponse(String sessionId, SusRequest request) {

        var sessionData = susRepository.getSession(sessionId);
        if (sessionData.getTimeSpentInSec() != null) {
            throw new SusException(ErrorMessage.builder().code("SUS002").message("duplicate session").build());
        }
        var oddSum = request.getUsabilityResponses().stream().filter(scoreCard -> scoreCard.getQuestionNumber() % 2 != 0)
                .mapToInt(UsabilityResponse::getScore).sum();
        log.debug("oddSum=" + oddSum);

        var evenSum = request.getUsabilityResponses().stream().filter(scoreCard -> scoreCard.getQuestionNumber() % 2 == 0)
                .mapToInt(UsabilityResponse::getScore).sum();
        log.debug("evenSum=" + evenSum);

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
        susRepository.saveScores(sessionId, percentile, grade, answers);

        return SaveResponse.builder().percentile(percentile).grade(grade).build();
    }

    public GlobalStats getGlobalStats(LocalDate fromDateTime,
                                      LocalDate toDateTime) {
        var responseTimes = susRepository.getResponseTimes(fromDateTime, toDateTime);
        var stats = susRepository.getAveragePercentile(fromDateTime, toDateTime);
        var gradeStats = susRepository.getGradeCount(fromDateTime, toDateTime);

        return GlobalStats
                .builder()
                .percentile(stats.getAvgScore())
                .grade(stats.getAvgScore()!=null ? calculateGrade(stats.getAvgScore()): "Z")
                .responseTimes(responseTimes)
                .stats(gradeStats)
                .totalCount(stats.getCount())
                .build();
    }
}

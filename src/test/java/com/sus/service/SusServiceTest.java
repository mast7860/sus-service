package com.sus.service;

import com.sus.error.SusException;
import com.sus.model.SusRequest;
import com.sus.model.UsabilityResponse;
import com.sus.repository.SusRepository;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class SusServiceTest {

    @Inject
    SusRepository susRepository;

    @Test
    void generateSessionId() {
        var service = new SusService(susRepository);

        var token = service.generateSessionId();

        assertNotNull(token);
    }

    @Test
    void getGlobalStats() {
        var service = new SusService(susRepository);

        var globalStats = service.getGlobalStats(LocalDate.of(2022, Month.JANUARY, 1), LocalDate.now());

        assertNotNull(globalStats);
    }

    @Test
    void saveResponses() {

        UsabilityResponse one = UsabilityResponse.builder().questionNumber(1).score(1).build();
        UsabilityResponse two = UsabilityResponse.builder().questionNumber(2).score(1).build();
        UsabilityResponse three = UsabilityResponse.builder().questionNumber(3).score(1).build();
        UsabilityResponse four = UsabilityResponse.builder().questionNumber(4).score(1).build();
        UsabilityResponse five = UsabilityResponse.builder().questionNumber(5).score(1).build();
        UsabilityResponse six = UsabilityResponse.builder().questionNumber(6).score(1).build();
        UsabilityResponse seven = UsabilityResponse.builder().questionNumber(7).score(1).build();
        UsabilityResponse eight = UsabilityResponse.builder().questionNumber(8).score(1).build();
        UsabilityResponse nine = UsabilityResponse.builder().questionNumber(9).score(1).build();
        UsabilityResponse ten = UsabilityResponse.builder().questionNumber(10).score(1).build();

        var request = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        var service = new SusService(susRepository);

        var token = service.generateSessionId();

        var saveResponse = service.saveUserResponse(token.getSessionId(), request);

        assertNotNull(saveResponse);

    }

    @Test
    void saveResponsesThrows_NotFound_Error() {

        UsabilityResponse one = UsabilityResponse.builder().questionNumber(1).score(1).build();
        UsabilityResponse two = UsabilityResponse.builder().questionNumber(2).score(1).build();
        UsabilityResponse three = UsabilityResponse.builder().questionNumber(3).score(1).build();
        UsabilityResponse four = UsabilityResponse.builder().questionNumber(4).score(1).build();
        UsabilityResponse five = UsabilityResponse.builder().questionNumber(5).score(1).build();
        UsabilityResponse six = UsabilityResponse.builder().questionNumber(6).score(1).build();
        UsabilityResponse seven = UsabilityResponse.builder().questionNumber(7).score(1).build();
        UsabilityResponse eight = UsabilityResponse.builder().questionNumber(8).score(1).build();
        UsabilityResponse nine = UsabilityResponse.builder().questionNumber(9).score(1).build();
        UsabilityResponse ten = UsabilityResponse.builder().questionNumber(10).score(1).build();

        var request = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        var service = new SusService(susRepository);

        SusException susException = Assertions.assertThrows(SusException.class, () -> service.saveUserResponse(UUID.randomUUID().toString(), request));

        assertEquals("SUS001", susException.getErrorMessage().getCode());

    }

    @Test
    void saveResponsesThrows_Distinct_Error() {

        UsabilityResponse one = UsabilityResponse.builder().questionNumber(1).score(1).build();
        UsabilityResponse two = UsabilityResponse.builder().questionNumber(2).score(1).build();
        UsabilityResponse three = UsabilityResponse.builder().questionNumber(3).score(1).build();
        UsabilityResponse four = UsabilityResponse.builder().questionNumber(4).score(1).build();
        UsabilityResponse five = UsabilityResponse.builder().questionNumber(5).score(1).build();
        UsabilityResponse six = UsabilityResponse.builder().questionNumber(6).score(1).build();
        UsabilityResponse seven = UsabilityResponse.builder().questionNumber(7).score(1).build();
        UsabilityResponse eight = UsabilityResponse.builder().questionNumber(8).score(1).build();
        UsabilityResponse nine = UsabilityResponse.builder().questionNumber(9).score(1).build();
        UsabilityResponse ten = UsabilityResponse.builder().questionNumber(10).score(1).build();

        var request = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        var service = new SusService(susRepository);

        SusException susException = Assertions.assertThrows(SusException.class, () -> service.saveUserResponse("df2deb5f-c9a8-4777-bb77-049bbd0c84eb", request));

        assertEquals("SUS002", susException.getErrorMessage().getCode());

    }

}
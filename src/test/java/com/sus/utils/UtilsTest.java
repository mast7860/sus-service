package com.sus.utils;

import com.sus.error.SusException;
import com.sus.model.SusRequest;
import com.sus.model.UsabilityResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @ParameterizedTest
    @CsvSource({
            "90.0, A",
            "80.0, B",
            "68.0, C",
            "65.0, D",
            "50.0, F",
            "101.0, Z",

    })
    void calculateGrade(Double percentile, String grade) {

        assertEquals(grade, Utils.calculateGrade(percentile));
    }

    @Test
    void validateRequest() {

        UsabilityResponse one = UsabilityResponse.builder().questionNumber(1).score(1).build();
        UsabilityResponse two = UsabilityResponse.builder().questionNumber(2).score(1).build();
        UsabilityResponse three = UsabilityResponse.builder().questionNumber(3).score(1).build();
        UsabilityResponse four = UsabilityResponse.builder().questionNumber(4).score(1).build();
        UsabilityResponse five = UsabilityResponse.builder().questionNumber(5).score(1).build();
        UsabilityResponse six = UsabilityResponse.builder().questionNumber(6).score(1).build();
        UsabilityResponse seven = UsabilityResponse.builder().questionNumber(7).score(1).build();
        UsabilityResponse eight = UsabilityResponse.builder().questionNumber(8).score(1).build();
        UsabilityResponse nine = UsabilityResponse.builder().questionNumber(9).score(1).build();
        UsabilityResponse ten = UsabilityResponse.builder().questionNumber(9).score(1).build();

        var request = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        SusException susException = Assertions.assertThrows(SusException.class, () -> Utils.validateRequest(request));

        assertEquals("SUS003", susException.getErrorMessage().getCode());

    }
}
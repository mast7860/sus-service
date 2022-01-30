package com.sus.resource;

import com.sus.error.SusException;
import com.sus.model.GlobalStats;
import com.sus.model.SaveResponse;
import com.sus.model.SusRequest;
import com.sus.model.Token;
import com.sus.model.UsabilityResponse;
import com.sus.service.SusService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.rxjava2.http.client.RxHttpClient;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class SusControllerTest {

    @Inject
    SusService susService;

    @Inject
    @Client("/")
    RxHttpClient client;
    private AutoCloseable closeable;

    @MockBean(SusService.class)
    SusService susService() {
        return mock(SusService.class);
    }

    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    void getTokenAndSaveResponses() {

        when(susService.generateSessionId()).thenReturn(Token.builder().sessionId(UUID.randomUUID().toString()).build());

        var tokenRequest = HttpRequest.GET("/token");

        var tokenResult = client.toBlocking().retrieve(tokenRequest, Token.class);

        var sessionId = tokenResult.getSessionId();

        assertNotNull(sessionId);

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

        var response = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        when(susService.saveUserResponse(any(), any())).thenReturn(SaveResponse.builder().grade("A").build());

        var saveRequest = HttpRequest.POST("/save", response)
                .header("sessionId", sessionId);

        var saveResult = client.toBlocking().retrieve(saveRequest, SaveResponse.class);

        assertNotNull(saveResult.getGrade());

        // constraint validation exception
        var responseThree = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine))
                .build();

        var saveRequestThree = HttpRequest.POST("/save", responseThree)
                .header("sessionId", UUID.randomUUID().toString());

        try {
            client.toBlocking().retrieve(saveRequestThree, SaveResponse.class);
        } catch (HttpClientResponseException httpClientResponseException) {
            assertEquals(HttpStatus.BAD_REQUEST.getCode(), httpClientResponseException.getStatus().getCode());
        }

    }

    @Test
    void saveResponses_Trigger_SusExceptionHandler() {

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

        var response = SusRequest
                .builder()
                .usabilityResponses(Arrays.asList(one, two, three, four, five, six, seven, eight, nine, ten))
                .build();

        var saveRequestTwo = HttpRequest.POST("/save", response)
                .header("sessionId", UUID.randomUUID().toString());

        when(susService.saveUserResponse(any(), any())).thenThrow(SusException.class);

        try {
            client.toBlocking().retrieve(saveRequestTwo, SaveResponse.class);
        } catch (HttpClientResponseException httpClientResponseException) {
            assertEquals(HttpStatus.BAD_REQUEST.getCode(), httpClientResponseException.getStatus().getCode());
        }
    }

    @Test
    void getGlobalStatus() {

        when(susService.getGlobalStats(any(),any())).thenReturn(GlobalStats.builder().grade("A").build());
        var statsRequest = HttpRequest.GET("/globalStats");

        var statsResponse = client.toBlocking().retrieve(statsRequest, GlobalStats.class);

        assertNotNull(statsResponse.getGrade());
    }

}
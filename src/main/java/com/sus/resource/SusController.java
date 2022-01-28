package com.sus.resource;

import com.sus.model.GlobalStats;
import com.sus.model.SaveResponse;
import com.sus.model.SusRequest;
import com.sus.model.Token;
import com.sus.service.SusService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Controller("/v1")
@Slf4j
public class SusController {

    private final SusService susService;

    @Inject
    public SusController(SusService susService) {
        this.susService = susService;
    }

    @Operation(summary = "get session token",
            description = "get id for the feedback session")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Tag(name = "Token")
    @Get(value = "/token", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Token>> getStatsID() {

        log.debug("getting session ID");

        return Single
                .just(susService.generateSessionId())
                .map(HttpResponse::ok);
    }

    @Operation(summary = "save responses",
            description = "save customer responses")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Tag(name = "Response")
    @Post(value = "/save", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<SaveResponse>> saveResponses(
            @Body SusRequest request) {

        log.debug("save user response");

        return Single
                .just(susService.saveUserResponse(request))
                .map(HttpResponse::ok);
    }

    @Operation(summary = "get global stats",
            description = "get global stats for sus")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Tag(name = "Stats")
    @Get(value = "/globalStats", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<GlobalStats>> getGlobalStatus() {

        log.info("getting global stats");

        return Single
                .just(susService.getResponseTimes())
                .map(HttpResponse::ok);
    }
}

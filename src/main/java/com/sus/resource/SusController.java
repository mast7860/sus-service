package com.sus.resource;

import com.sus.model.GlobalStats;
import com.sus.model.SaveResponse;
import com.sus.model.SusRequest;
import com.sus.model.Token;
import com.sus.service.SusService;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.reactivex.Single;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.sus.utils.Utils.validateRequest;

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
    public Single<HttpResponse<Token>> getToken() {

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
            @Header String sessionId,
            @Body @Valid SusRequest request) {

        log.debug("save user response");

        validateRequest(request);

        return Single
                .just(susService.saveUserResponse(sessionId, request))
                .map(HttpResponse::ok);
    }

    @Operation(summary = "get global stats",
            description = "get global stats for sus")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Tag(name = "Stats")
    @Get(value = "/globalStats", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<GlobalStats>> getGlobalStats(
            @QueryValue @Nullable @Format("yyyy-MM-dd") LocalDate fromDate,
            @QueryValue @Nullable @Format("yyyy-MM-dd") LocalDate toDate) {

        log.debug("getting global stats");

        var from = fromDate!=null ? fromDate : LocalDate.now().minusMonths(1);
        var to = toDate!=null ? toDate : LocalDate.now();

        return Single
                .just(susService.getGlobalStats(from,to))
                .map(HttpResponse::ok);
    }
}

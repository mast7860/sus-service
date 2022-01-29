package com.sus.job;

import com.sus.repository.SusRepository;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Singleton
@Slf4j
public class SessionCleanJob {

    private final SusRepository susRepository;

    public SessionCleanJob(SusRepository susRepository) {
        this.susRepository = susRepository;
    }

    @Scheduled(cron = "0 33 15 1/1 * ?")
    void cleanUp() {

        log.info("clean up job running");

        susRepository.deleteUnusedSessions();
    }
}

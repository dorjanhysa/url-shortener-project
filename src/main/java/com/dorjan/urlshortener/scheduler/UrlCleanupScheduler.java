package com.dorjan.urlshortener.scheduler;

import com.dorjan.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlCleanupScheduler {

    private final UrlService urlService;

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredUrls() {
        log.info("Cleaning up expired URLs...");
        urlService.deleteExpiredUrls();
        log.info("Expired URL cleanup completed!");
    }
}

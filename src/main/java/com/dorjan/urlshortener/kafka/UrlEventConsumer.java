package com.dorjan.urlshortener.kafka;

import com.dorjan.urlshortener.dto.UrlEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UrlEventConsumer {

    @KafkaListener(topics = "url-events", groupId = "analytics-group")
    public void handleAnalytics(UrlEvent event) {
        log.info("Analytics - {} | shortCode={} | clicks={}",
                event, event.getShortCode(), event.getClickCount());
    }

    @KafkaListener(topics = "url-events", groupId = "audit-group")
    public void handleAudit(UrlEvent event) {
        log.info("Audit - {} | shortCode={} | longUrl={}",
                event, event.getShortCode(), event.getLongUrl());
    }
}

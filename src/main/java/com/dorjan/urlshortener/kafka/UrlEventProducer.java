package com.dorjan.urlshortener.kafka;

import com.dorjan.urlshortener.dto.UrlEvent;
import com.dorjan.urlshortener.model.Url;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlEventProducer {

    private static final String TOPIC = "url-events";
    private final KafkaTemplate<String, UrlEvent> kafkaTemplate;

    public void publishUrlCreated(Url url) {
        UrlEvent event = UrlEvent.builder()
                .eventType(UrlEvent.EventType.URL_CREATED)
                .shortCode(url.getShortUrl())
                .longUrl(url.getLongUrl())
                .clickCount(0)
                .timestamp(LocalDateTime.now())
                .expiresAt(url.getExpiresAt())
                .build();

        send(url.getShortUrl(), event);
    }

    public void publishUrlClicked(String shortCode, long clickCount) {
        UrlEvent event = UrlEvent.builder()
                .eventType(UrlEvent.EventType.URL_CLICKED)
                .shortCode(shortCode)
                .clickCount(clickCount)
                .timestamp(LocalDateTime.now())
                .build();

        send(shortCode, event);
    }

    public void publishUrlExpired(Url url) {
        UrlEvent event = UrlEvent.builder()
                .eventType(UrlEvent.EventType.URL_EXPIRED)
                .shortCode(url.getShortUrl())
                .longUrl(url.getLongUrl())
                .clickCount(url.getClickCount())
                .timestamp(LocalDateTime.now())
                .build();

        send(url.getShortUrl(), event);
    }

    private void send(String key, UrlEvent event) {
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} for {}: {}",
                                event.getEventType(), key, ex.getMessage());
                    } else {
                        log.info("Published {} for {} -> partition {} offset {}",
                                event.getEventType(), key,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}

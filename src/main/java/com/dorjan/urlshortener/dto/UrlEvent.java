package com.dorjan.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UrlEvent {

    public enum EventType {
        URL_CREATED,
        URL_CLICKED,
        URL_EXPIRED
    }

    private EventType eventType;
    private String shortCode;
    private String longUrl;
    private long clickCount;
    private LocalDateTime timestamp;
    private LocalDateTime expiresAt;
}

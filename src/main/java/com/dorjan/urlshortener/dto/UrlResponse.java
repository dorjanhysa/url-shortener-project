package com.dorjan.urlshortener.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlResponse {

    private Long id;
    private String shortUrl;
    private String longUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private long clickCount;
}

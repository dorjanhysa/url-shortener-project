package com.dorjan.urlshortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "url-shortener")
public record UrlShortenerProperties(
        int defaultExpirationMinutes,
        String baseUrl,
        int shortCodeLength
) {}

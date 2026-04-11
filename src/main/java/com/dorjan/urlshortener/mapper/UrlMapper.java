package com.dorjan.urlshortener.mapper;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.model.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UrlMapper {

    private final UrlShortenerProperties properties;

    public UrlResponse toResponse(Url url) {
        UrlResponse response = new UrlResponse();
        response.setId(url.getId());
        response.setShortUrl(properties.baseUrl() + "/" + url.getShortUrl());
        response.setLongUrl(url.getLongUrl());
        response.setCreatedAt(url.getCreatedAt());
        response.setExpiresAt(url.getExpiresAt());
        response.setClickCount(url.getClickCount());
        return response;
    }
}

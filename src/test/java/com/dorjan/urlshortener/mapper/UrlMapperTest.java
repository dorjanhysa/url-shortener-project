package com.dorjan.urlshortener.mapper;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.model.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlMapperTest {

    @Mock
    private UrlShortenerProperties properties;

    @InjectMocks
    private UrlMapper urlMapper;

    @Test
    void toResponse_shouldMapAllFields() {
        when(properties.baseUrl()).thenReturn("https://localhost:8080");

        Url url = new Url();
        url.setId(1L);
        url.setShortUrl("abc12345");
        url.setLongUrl("https://www.google.com");
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        url.setClickCount(10);

        UrlResponse response = urlMapper.toResponse(url);

        assertEquals(1L, response.getId());
        assertEquals("https://localhost:8080/abc12345", response.getShortUrl());
        assertEquals("https://www.google.com", response.getLongUrl());
        assertEquals(10, response.getClickCount());
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getExpiresAt());
    }
}

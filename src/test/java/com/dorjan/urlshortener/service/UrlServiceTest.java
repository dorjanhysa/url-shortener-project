package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.dto.ShortenUrlRequest;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.exception.BusinessException;
import com.dorjan.urlshortener.mapper.UrlMapper;
import com.dorjan.urlshortener.model.Url;
import com.dorjan.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private UrlShortenerProperties properties;

    @InjectMocks
    private UrlService urlService;

    private Url testUrl;
    private UrlResponse testResponse;

    @BeforeEach
    void setUp() {
        testUrl = new Url();
        testUrl.setShortUrl("abc12345");
        testUrl.setLongUrl("https://www.google.com");
        testUrl.setCreatedAt(LocalDateTime.now());
        testUrl.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        testUrl.setClickCount(0);

        testResponse = new UrlResponse();
        testResponse.setId(1L);
        testResponse.setShortUrl("https://localhost:8080/abc12345");
        testResponse.setLongUrl("https://www.google.com");
        testResponse.setCreatedAt(testUrl.getCreatedAt());
        testResponse.setExpiresAt(testUrl.getExpiresAt());
        testResponse.setClickCount(0);
    }

    @Test
    void shortenUrl_shouldCreateNewShortUrl() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");

        when(properties.defaultExpirationMinutes()).thenReturn(5);
        when(properties.shortCodeLength()).thenReturn(8);
        when(urlRepository.findByLongUrlAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(testUrl);
        when(urlMapper.toResponse(any(Url.class))).thenReturn(testResponse);

        UrlResponse result = urlService.shortenUrl(request);

        assertNotNull(result);
        assertEquals("https://www.google.com", result.getLongUrl());
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void shortenUrl_shouldReturnExistingAndResetExpiration() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");

        when(properties.defaultExpirationMinutes()).thenReturn(5);
        when(urlRepository.findByLongUrlAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(testUrl));
        when(urlRepository.save(any(Url.class))).thenReturn(testUrl);
        when(urlMapper.toResponse(any(Url.class))).thenReturn(testResponse);

        UrlResponse result = urlService.shortenUrl(request);

        assertNotNull(result);
        verify(urlRepository).save(testUrl);
    }

    @Test
    void shortenUrl_shouldUseCustomExpiration() {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setLongUrl("https://www.google.com");
        request.setExpirationMinutes(30);

        when(properties.shortCodeLength()).thenReturn(8);
        when(urlRepository.findByLongUrlAndExpiresAtAfter(anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(urlRepository.save(any(Url.class))).thenReturn(testUrl);
        when(urlMapper.toResponse(any(Url.class))).thenReturn(testResponse);

        UrlResponse result = urlService.shortenUrl(request);

        assertNotNull(result);
        verify(urlRepository).save(any(Url.class));
    }

    @Test
    void resolveUrl_shouldReturnLongUrlAndIncrementClicks() {
        when(urlRepository.findByShortUrl("abc12345")).thenReturn(Optional.of(testUrl));

        String result = urlService.resolveUrl("abc12345");

        assertEquals("https://www.google.com", result);
        verify(urlRepository).incrementClickCount("abc12345");
    }

    @Test
    void resolveUrl_shouldThrowWhenNotFound() {
        when(urlRepository.findByShortUrl("notfound")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> urlService.resolveUrl("notfound"));
    }

    @Test
    void resolveUrl_shouldThrowWhenExpired() {
        testUrl.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(urlRepository.findByShortUrl("abc12345")).thenReturn(Optional.of(testUrl));

        assertThrows(BusinessException.class, () -> urlService.resolveUrl("abc12345"));
    }

    @Test
    void updateExpiration_shouldUpdateAndReturn() {
        when(urlRepository.findByShortUrl("abc12345")).thenReturn(Optional.of(testUrl));
        when(urlRepository.save(any(Url.class))).thenReturn(testUrl);
        when(urlMapper.toResponse(any(Url.class))).thenReturn(testResponse);

        UrlResponse result = urlService.updateExpiration("abc12345", 30);

        assertNotNull(result);
        verify(urlRepository).save(testUrl);
    }

    @Test
    void updateExpiration_shouldThrowWhenNotFound() {
        when(urlRepository.findByShortUrl("notfound")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> urlService.updateExpiration("notfound", 30));
    }

    @Test
    void getUrlStats_shouldReturnStats() {
        when(urlRepository.findByShortUrl("abc12345")).thenReturn(Optional.of(testUrl));
        when(urlMapper.toResponse(testUrl)).thenReturn(testResponse);

        UrlResponse result = urlService.getUrlStats("abc12345");

        assertNotNull(result);
        assertEquals(0, result.getClickCount());
    }

    @Test
    void getUrlStats_shouldThrowWhenNotFound() {
        when(urlRepository.findByShortUrl("notfound")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> urlService.getUrlStats("notfound"));
    }

    @Test
    void getAllUrls_shouldReturnAllUrls() {
        List<Url> urls = List.of(testUrl);
        when(urlRepository.findAll()).thenReturn(urls);

        List<Url> result = urlService.getAllUrls();

        assertEquals(1, result.size());
        verify(urlRepository).findAll();
    }

    @Test
    void deleteExpiredUrls_shouldCallRepository() {
        urlService.deleteExpiredUrls();

        verify(urlRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
    }
}

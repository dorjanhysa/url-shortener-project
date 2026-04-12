package com.dorjan.urlshortener.service;

import com.dorjan.urlshortener.config.UrlShortenerProperties;
import com.dorjan.urlshortener.dto.ShortenUrlRequest;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.exception.BusinessException;
import com.dorjan.urlshortener.mapper.UrlMapper;
import com.dorjan.urlshortener.model.Url;
import com.dorjan.urlshortener.repository.UrlRepository;
import com.dorjan.urlshortener.util.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlShortenerProperties properties;
    private final UrlMapper urlMapper;

    public UrlResponse shortenUrl(ShortenUrlRequest request) {
        int expiration = request.getExpirationMinutes() != null
                ? request.getExpirationMinutes()
                : properties.defaultExpirationMinutes();

        // Check if this long URL is already shortened and not expired
        var existing = urlRepository.findByLongUrlAndExpiresAtAfter(
                request.getLongUrl(), LocalDateTime.now());

        if (existing.isPresent()) {
            // Reset expiration time and return existing
            Url url = existing.get();
            url.setExpiresAt(LocalDateTime.now().plusMinutes(expiration));
            urlRepository.save(url);
            return urlMapper.toResponse(url);
        }

        // Create new shortened URL
        Url url = new Url();
        url.setLongUrl(request.getLongUrl());
        url.setShortUrl(ShortCodeGenerator.generate(properties.shortCodeLength()));
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiresAt(LocalDateTime.now().plusMinutes(expiration));
        url.setClickCount(0);

        urlRepository.save(url);
        return urlMapper.toResponse(url);
    }

    public String resolveUrl(String shortCode) {
        Url url = urlRepository.findByShortUrl(shortCode)
                .orElseThrow(BusinessException::urlNotFound);

        if (url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw BusinessException.urlExpired();
        }

        urlRepository.incrementClickCount(shortCode);

        return url.getLongUrl();
    }

    public UrlResponse updateExpiration(String shortCode, int minutes) {
        Url url = urlRepository.findByShortUrl(shortCode)
                .orElseThrow(BusinessException::urlNotFound);

        url.setExpiresAt(LocalDateTime.now().plusMinutes(minutes));
        urlRepository.save(url);
        return urlMapper.toResponse(url);
    }

    public UrlResponse getUrlStats(String shortCode) {
        Url url = urlRepository.findByShortUrl(shortCode)
                .orElseThrow(BusinessException::urlNotFound);
        return urlMapper.toResponse(url);
    }

    public List<Url> getAllUrls() {
        return urlRepository.findAll();
    }

    @Transactional
    public void deleteExpiredUrls() {
        urlRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}

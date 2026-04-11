package com.dorjan.urlshortener.controller;

import com.dorjan.urlshortener.dto.ShortenUrlRequest;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        UrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponse> getUrlStats(@PathVariable String shortCode) {
        UrlResponse response = urlService.getUrlStats(shortCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{shortCode}/update-expiration")
    public ResponseEntity<UrlResponse> updateExpiration(@PathVariable String shortCode,
                                                        @RequestParam int minutes) {
        UrlResponse response = urlService.updateExpiration(shortCode, minutes);
        return ResponseEntity.ok(response);
    }
}

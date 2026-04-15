package com.dorjan.urlshortener.controller;

import com.dorjan.urlshortener.dto.ShortenUrlRequest;
import com.dorjan.urlshortener.dto.UrlResponse;
import com.dorjan.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@Tag(name = "Url Shortener", description = "Creates a short URL or returns existing one if not expired")
@SecurityRequirement(name = "bearerAuth")
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    @Operation(summary = "Shorten a URL", description = "Creates a short URL or returns existing one if not expired")
    public ResponseEntity<UrlResponse> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        UrlResponse response = urlService.shortenUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}/stats")
    @Operation(summary = "Get URL stats", description = "Returns URL details including click count")
    public ResponseEntity<UrlResponse> getUrlStats(@PathVariable String shortCode) {
        UrlResponse response = urlService.getUrlStats(shortCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{shortCode}/update-expiration")
    @Operation(summary = "Update URL expiration", description = "Updates the expiration time of a shortened URL")
    public ResponseEntity<UrlResponse> updateExpiration(@PathVariable String shortCode,
                                                        @RequestParam int minutes) {
        UrlResponse response = urlService.updateExpiration(shortCode, minutes);
        return ResponseEntity.ok(response);
    }
}

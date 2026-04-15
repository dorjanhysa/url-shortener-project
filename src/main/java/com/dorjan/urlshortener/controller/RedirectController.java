package com.dorjan.urlshortener.controller;

import com.dorjan.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "Redirect", description = "Redirects short URLs to original URLs")
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    @Operation(summary = "Redirect to original URL", description = "Redirects to the original long URL and increments click count")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String longUrl = urlService.resolveUrl(shortCode);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}

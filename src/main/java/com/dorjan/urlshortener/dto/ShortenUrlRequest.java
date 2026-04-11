package com.dorjan.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShortenUrlRequest {

    @NotBlank(message = "URL cannot be empty")
    private String longUrl;

    private Integer expirationMinutes;
}

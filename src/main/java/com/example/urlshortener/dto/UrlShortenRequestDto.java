package com.example.urlshortener.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class UrlShortenRequestDto {
    @NotBlank
    private String originalUrl;

    private String customCode;
}

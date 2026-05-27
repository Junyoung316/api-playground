package com.example.apiplayground.urlshortener.dto.response;

import java.time.LocalDateTime;

public record UrlStatsResponse(
        String shortCode,
        String originalUrl,
        Long viewCount,
        LocalDateTime createdAt
) {}
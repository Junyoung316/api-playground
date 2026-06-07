package com.example.apiplayground.urlshortener.controller;

import com.example.apiplayground.common.response.ApiResponse;
import com.example.apiplayground.urlshortener.dto.request.ShortenRequest;
import com.example.apiplayground.urlshortener.dto.response.ShortenResponse;
import com.example.apiplayground.urlshortener.dto.response.UrlStatsResponse;
import com.example.apiplayground.urlshortener.service.UrlShortenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<ApiResponse<ShortenResponse>> createShortenedUrl(@RequestBody ShortenRequest originalUrl) {
        String shortCode = urlShortenerService.originalUrlShorten(originalUrl.originalUrl());

        return ResponseEntity.ok().body(ApiResponse.success(new ShortenResponse(shortCode)));
    }

    @GetMapping("/s/{code}")
    public ResponseEntity<ApiResponse<Void>> getShortenedUrl(@PathVariable String code) {
        String originalUrl = urlShortenerService.getOriginalUrl(code);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @GetMapping("/stats/{code}")
    public ResponseEntity<ApiResponse<UrlStatsResponse>> getShortenedUrlStats(@PathVariable String code) {
        return ResponseEntity.ok().body(ApiResponse.success(urlShortenerService.getUrlStats(code)));
    }

    @DeleteMapping("/shorten/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteShortenedUrl(@PathVariable String code) {
        urlShortenerService.delUrl(code);
        return ResponseEntity.ok().body(ApiResponse.success(null));
    }
}
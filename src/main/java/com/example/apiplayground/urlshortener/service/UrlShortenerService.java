package com.example.apiplayground.urlshortener.service;

import com.example.apiplayground.common.exception.BusinessException;
import com.example.apiplayground.common.exception.ErrorCode;
import com.example.apiplayground.urlshortener.domain.UrlShortener;
import com.example.apiplayground.urlshortener.dto.response.UrlStatsResponse;
import com.example.apiplayground.urlshortener.repository.UrlShortenerRedisRepository;
import com.example.apiplayground.urlshortener.repository.UrlShortenerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlShortenerRepository urlShortenerRepository;
    private final UrlShortenerRedisRepository urlShortenerRedisRepository;

    @Value("${url-shortener.expire-days}")
    private Long expireDays;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    // 원본 url 단축 서비스
    @Transactional
    public String originalUrlShorten(String originalUrl) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(expireDays);
        long ttlSeconds = Duration.between(now, expiresAt).getSeconds();

        String shortenedUrl = generateShortCode();
        while (urlShortenerRepository.existsByShortCode(shortenedUrl)) {
            shortenedUrl = generateShortCode();
        }

        UrlShortener urlShortener = UrlShortener.builder()
                .shortCode(shortenedUrl)
                .originalUrl(originalUrl)
                .expiresAt(expiresAt)
                .build();

        urlShortenerRepository.save(urlShortener);

        urlShortenerRedisRepository.save(shortenedUrl, originalUrl, ttlSeconds);

        return shortenedUrl;
    }

    // 단축 코드 -> 원본 URL 반환 서비스 (조회수 + 1)
    @Transactional
    public String getOriginalUrl(String shortCode) {
        String originalUrl = urlShortenerRedisRepository.findOriginalUrl(shortCode);

        if (originalUrl == null) {
            throw new BusinessException(ErrorCode.URL_NOT_FOUND);
        }

        urlShortenerRepository.incrementViewCount(shortCode);

        return originalUrl;
    }

    // 조회수, 생성일, 원본 URL 조회 서비스
    @Transactional(readOnly = true)
    public UrlStatsResponse getUrlStats(String shortCode) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.URL_NOT_FOUND)) ;

        return new UrlStatsResponse(
                urlShortener.getShortCode(),
                urlShortener.getOriginalUrl(),
                urlShortener.getViewCount(),
                urlShortener.getCreatedAt()
        );
    }

    // 단축 코드 및 원본 URL 삭제 서비스
    @Transactional
    public void delUrl(String shortCode) {
        String originalUrl = urlShortenerRedisRepository.findOriginalUrl(shortCode);
        if (originalUrl == null) {
            throw new BusinessException(ErrorCode.URL_NOT_FOUND);
        }

        urlShortenerRedisRepository.delete(shortCode);
        urlShortenerRepository.deleteByShortCode(shortCode);
    }
}

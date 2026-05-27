package com.example.apiplayground.urlshortener.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class UrlShortenerRedisRepository {

    private final StringRedisTemplate redisTemplate;

    public void save(String shortCode, String originalUrl, long ttlSeconds) {
        redisTemplate.opsForValue().set(shortCode, originalUrl, ttlSeconds, TimeUnit.SECONDS);
    }

    public String findOriginalUrl(String shortCode) {
        return redisTemplate.opsForValue().get(shortCode);
    }

    public void delete(String shortCode) {
        redisTemplate.delete(shortCode);
    }

}

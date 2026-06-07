package com.example.apiplayground.urlshortener.repository;

import com.example.apiplayground.urlshortener.domain.UrlShortener;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlShortener, Long> {
    Optional<UrlShortener> findByShortCode(String shortcode);
    boolean existsByShortCode(String shortcode);
    void deleteByShortCode(String shortcode);

    @Modifying
    @Query("UPDATE UrlShortener u SET u.viewCount = u.viewCount + 1 WHERE u.shortCode = :shortCode")
    void incrementViewCount(@Param("shortCode") String shortCode);
}

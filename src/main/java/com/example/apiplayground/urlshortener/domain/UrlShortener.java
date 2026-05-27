package com.example.apiplayground.urlshortener.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_shortener")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrlShortener {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
}

/*
 * id          - 기본키
 * originalUrl - 원본 URL
 * shortCode   - 단축 코드
 * viewCount   - 조회수
 * createdAt   - 생성일
 * expiresAt   - 만료일 (nullable)
 */
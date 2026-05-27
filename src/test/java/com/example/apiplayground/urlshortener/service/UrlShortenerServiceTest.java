package com.example.apiplayground.urlshortener.service;

import com.example.apiplayground.common.exception.BusinessException;
import com.example.apiplayground.common.exception.ErrorCode;
import com.example.apiplayground.urlshortener.repository.UrlShortenerRedisRepository;
import com.example.apiplayground.urlshortener.repository.UrlShortenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import com.example.apiplayground.urlshortener.domain.UrlShortener;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTest {

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @Mock
    private UrlShortenerRedisRepository urlShortenerRedisRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlShortenerService, "expireDays", 30L);
    }

    @Test
    @DisplayName("단축 URL 생성 성공")
    void createShortenedUrl_success() {
        // given
        String originalUrl = "https://www.naver.com";
        given(urlShortenerRepository.existsByShortCode(anyString())).willReturn(false);

        // when
        String shortCode = urlShortenerService.originalUrlShorten(originalUrl);

        // then
        assertThat(shortCode).isNotNull();
        assertThat(shortCode).hasSize(8);
        verify(urlShortenerRepository, times(1)).save(any(UrlShortener.class));
        verify(urlShortenerRedisRepository, times(1)).save(anyString(), eq(originalUrl), anyLong());
    }

    @Test
    @DisplayName("존재하지 않는 코드로 조회 시 예외 발생")
    void getOriginalUrl_notFound() {
        // given
        String shortCode = "abc12345";
        given(urlShortenerRedisRepository.findOriginalUrl(shortCode)).willReturn(null);

        // when & then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> urlShortenerService.getOriginalUrl(shortCode));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.URL_NOT_FOUND);
    }

    @Test
    @DisplayName("단축 URL 삭제 성공")
    void deleteUrl_success() {
        // given
        String shortCode = "abc12345";
        String originalUrl = "https://www.naver.com";
        given(urlShortenerRedisRepository.findOriginalUrl(shortCode)).willReturn(originalUrl);

        // when
        urlShortenerService.delUrl(shortCode);

        // then
        verify(urlShortenerRedisRepository, times(1)).delete(shortCode);
        verify(urlShortenerRepository, times(1)).deleteByShortCode(shortCode);
    }
}
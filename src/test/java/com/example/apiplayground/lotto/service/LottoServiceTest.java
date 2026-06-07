package com.example.apiplayground.lotto.service;


import com.example.apiplayground.lotto.domain.Lotto;
import com.example.apiplayground.lotto.dto.LottoHistoryResponse;
import com.example.apiplayground.lotto.dto.LottoResponse;
import com.example.apiplayground.lotto.repository.LottoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LottoServiceTest {

    @InjectMocks
    private LottoService lottoService;

    @Mock
    private LottoRepository lottoRepository;

    @Test
    @DisplayName("로또 번호 생성 성공")
    void generateLotto_success() {
        // When
        LottoResponse response = lottoService.generateLotto();

        // Then
        assertThat(response.numbers()).hasSize(6); // 6개 검증
        assertThat(response.numbers()).allMatch(n -> n >= 1 && n <= 45); // 1~45 사이 검증
        assertThat(response.numbers()).isSorted(); // 오름차순 검증
        verify(lottoRepository, times(1)).save(any(Lotto.class)); // save 호출 검증
    }

    @Test
    @DisplayName("이력 조회 성공")
    void getLottoHistory_success() {
        // Given
        Lotto lotto = Lotto.builder()
                .numbers(List.of(1, 5, 12, 23, 35, 45))
                .build();

        Page<Lotto> lottoPage = new PageImpl<>(List.of(lotto));
        given(lottoRepository.findAll(any(Pageable.class))).willReturn(lottoPage);

        // When
        Page<LottoHistoryResponse> result = lottoService.getLottoHistory(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).numbers()).hasSize(6);
    }

}
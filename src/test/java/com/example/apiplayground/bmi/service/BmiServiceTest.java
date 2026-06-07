package com.example.apiplayground.bmi.service;

import com.example.apiplayground.bmi.domain.Bmi;
import com.example.apiplayground.bmi.domain.BmiStatus;
import com.example.apiplayground.bmi.dto.request.BmiRequest;
import com.example.apiplayground.bmi.dto.response.BmiHistoryResponse;
import com.example.apiplayground.bmi.dto.response.BmiResponse;
import com.example.apiplayground.bmi.repository.BmiRepository;
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
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BmiServiceTest {

    @InjectMocks
    private BmiService bmiService;

    @Mock
    private BmiRepository bmiRepository;

    @Test
    @DisplayName("BMI 계산 성공")
    void calculateBmi_success() {
        // Given
        BmiRequest request = new BmiRequest(175.0, 70.0);
        double heightInMeter = 175.0 / 100.0;
        double expectedBmi = 70.0 / (heightInMeter * heightInMeter);

        // When
        BmiResponse response = bmiService.calculateBmi(request);

        // Then
        assertThat(response.bmi()).isCloseTo(expectedBmi, within(0.01));
        assertThat(response.status()).isEqualTo(BmiStatus.NORMAL.getDescription());
        verify(bmiRepository, times(1)).save(any(Bmi.class));
    }

    @Test
    @DisplayName("BMI 이력 조회 성공")
    void getBmiHistory_success() {
        // Given
        Bmi bmi = Bmi.builder()
                .height(175.0)
                .weight(70.0)
                .bmi(22.86)
                .status(BmiStatus.NORMAL)
                .build();

        Page<Bmi> bmiPage = new PageImpl<>(List.of(bmi));
        given(bmiRepository.findAll(any(Pageable.class))).willReturn(bmiPage);

        // When
        Page<BmiHistoryResponse> result = bmiService.getBmiHistory(PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).status()).isEqualTo(BmiStatus.NORMAL.getDescription());
    }
}
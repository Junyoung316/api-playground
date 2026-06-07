package com.example.apiplayground.bmi.service;

import com.example.apiplayground.bmi.domain.Bmi;
import com.example.apiplayground.bmi.domain.BmiStatus;
import com.example.apiplayground.bmi.dto.request.BmiRequest;
import com.example.apiplayground.bmi.dto.response.BmiHistoryResponse;
import com.example.apiplayground.bmi.dto.response.BmiResponse;
import com.example.apiplayground.bmi.repository.BmiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BmiService {

    private final BmiRepository bmiRepository;

    // 체중(kg) / (키(m) * 키(m))
    @Transactional
    public BmiResponse calculateBmi(BmiRequest bmiRequest) {

        double heightInMeter = bmiRequest.height() / 100.0;
        double bmi = bmiRequest.weight() / (heightInMeter * heightInMeter);

        BmiStatus status = BmiStatus.from(bmi);

        Bmi bmiResult = Bmi.builder()
                .height(bmiRequest.height())
                .weight(bmiRequest.weight())
                .bmi(bmi)
                .status(status)
                .build();

        bmiRepository.save(bmiResult);

        return new BmiResponse(
                bmi,
                status.getDescription()
        );
    }

    @Transactional(readOnly = true)
    public Page<BmiHistoryResponse> getBmiHistory(Pageable pageable) {
        return bmiRepository.findAll(pageable)
                .map(bmi -> new BmiHistoryResponse(
                        bmi.getId(),
                        bmi.getHeight(),
                        bmi.getWeight(),
                        bmi.getBmi(),
                        bmi.getStatus().getDescription(),
                        bmi.getCreatedAt()
                ));
    }

}
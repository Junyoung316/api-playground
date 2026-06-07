package com.example.apiplayground.bmi.dto.response;

import java.time.LocalDateTime;

public record BmiHistoryResponse(
        Long id,
        Double height,
        Double weight,
        Double bmi,
        String status,
        LocalDateTime createdAt
) {
}
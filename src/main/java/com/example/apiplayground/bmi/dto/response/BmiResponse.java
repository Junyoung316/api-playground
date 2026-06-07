package com.example.apiplayground.bmi.dto.response;

public record BmiResponse(
        Double bmi,
        String status
) {
}

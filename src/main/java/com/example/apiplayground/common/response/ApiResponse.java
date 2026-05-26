package com.example.apiplayground.common.response;

import com.example.apiplayground.common.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiResponse<T>(
        String code,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("SUCCESS", "성공", data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("SUCCESS", "성공", null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode code) {
        return new ApiResponse<>(code.name(), code.getMessage(), null);
    }
}

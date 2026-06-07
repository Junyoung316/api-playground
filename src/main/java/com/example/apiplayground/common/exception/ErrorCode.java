package com.example.apiplayground.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),

    // URL Shortener
    URL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 단축 URL입니다"),

    // Memo
    MEMO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하니 않는 메모입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

}

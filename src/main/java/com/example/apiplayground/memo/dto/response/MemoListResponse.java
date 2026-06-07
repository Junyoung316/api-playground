package com.example.apiplayground.memo.dto.response;

import java.time.LocalDateTime;

public record MemoListResponse(
    Long id,
    String title,
    String author,
    LocalDateTime createdAt
) { }
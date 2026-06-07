package com.example.apiplayground.memo.dto.response;

import java.time.LocalDateTime;

public record MemoDetailResponse(
    Long id,
    String title,
    String content,
    String author,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) { }
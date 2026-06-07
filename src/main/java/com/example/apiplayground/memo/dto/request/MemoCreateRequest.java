package com.example.apiplayground.memo.dto.request;

public record MemoCreateRequest(
        String title,
        String content,
        String author
) { }

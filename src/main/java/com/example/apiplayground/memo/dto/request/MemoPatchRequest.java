package com.example.apiplayground.memo.dto.request;

public record MemoPatchRequest(
        String title,
        String content
) { }

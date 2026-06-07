package com.example.apiplayground.memo.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemoUpdateRequest(
        @NotNull String title,
        @NotNull String content
) {
}

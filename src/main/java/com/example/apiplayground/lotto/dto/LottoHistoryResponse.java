package com.example.apiplayground.lotto.dto;

import java.time.LocalDateTime;
import java.util.List;

public record LottoHistoryResponse(
        Long id,
        List<Integer> numbers,
        LocalDateTime createdAt
) {
}

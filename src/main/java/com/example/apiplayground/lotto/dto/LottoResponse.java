package com.example.apiplayground.lotto.dto;

import java.util.List;

public record LottoResponse(
        List<Integer> numbers
) {
}

package com.example.apiplayground.lotto.controller;

import com.example.apiplayground.common.response.ApiResponse;
import com.example.apiplayground.lotto.dto.LottoHistoryResponse;
import com.example.apiplayground.lotto.dto.LottoResponse;
import com.example.apiplayground.lotto.service.LottoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lotto")
@RequiredArgsConstructor
public class LottoController {

    private final LottoService lottoService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<LottoResponse>> generateLottoNumbers() {
        return ResponseEntity.ok(ApiResponse.success(lottoService.generateLotto()));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<LottoHistoryResponse>>> lottoHistory(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<LottoHistoryResponse> lottoHistoryResponsePage = lottoService.getLottoHistory(pageable);

        return ResponseEntity.ok(ApiResponse.success(lottoHistoryResponsePage));
    }

}

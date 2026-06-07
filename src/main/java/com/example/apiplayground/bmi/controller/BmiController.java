package com.example.apiplayground.bmi.controller;

import com.example.apiplayground.bmi.dto.request.BmiRequest;
import com.example.apiplayground.bmi.dto.response.BmiHistoryResponse;
import com.example.apiplayground.bmi.dto.response.BmiResponse;
import com.example.apiplayground.bmi.service.BmiService;
import com.example.apiplayground.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bmi")
@RequiredArgsConstructor
public class BmiController {

    private final BmiService bmiService;

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<BmiResponse>> calculateBmi(@RequestBody BmiRequest bmiRequest) {
        return ResponseEntity.ok(ApiResponse.success(bmiService.calculateBmi(bmiRequest)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<BmiHistoryResponse>>> getBmiHistory(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<BmiHistoryResponse> bmiHistoryResponsePage = bmiService.getBmiHistory(pageable);

        return ResponseEntity.ok(ApiResponse.success(bmiHistoryResponsePage));
    }
}

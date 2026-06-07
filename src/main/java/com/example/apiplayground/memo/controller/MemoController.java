package com.example.apiplayground.memo.controller;

import com.example.apiplayground.common.response.ApiResponse;
import com.example.apiplayground.memo.dto.request.MemoCreateRequest;
import com.example.apiplayground.memo.dto.request.MemoPatchRequest;
import com.example.apiplayground.memo.dto.request.MemoUpdateRequest;
import com.example.apiplayground.memo.dto.response.MemoDetailResponse;
import com.example.apiplayground.memo.dto.response.MemoListResponse;
import com.example.apiplayground.memo.service.MemoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    @PostMapping("/memos")
    public ResponseEntity<ApiResponse<Void>> createMemo(@RequestBody MemoCreateRequest memoCreateRequest) {

        memoService.createMemo(memoCreateRequest);

        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/memos")
    public ResponseEntity<ApiResponse<Page<MemoListResponse>>> getMemos(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<MemoListResponse> memoListResponsePage = memoService.getMemos(pageable);

        return ResponseEntity.ok(ApiResponse.success(memoListResponsePage));
    }

    @GetMapping("/memos/{id}")
    public ResponseEntity<ApiResponse<MemoDetailResponse>> getMemo(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(memoService.getMemo(id)));
    }

    @PutMapping("/memos/{id}")
    public ResponseEntity<ApiResponse<Void>> updateMemo(@PathVariable Long id, @RequestBody @Valid MemoUpdateRequest memoUpdateRequest) {
        memoService.updateMemo(id, memoUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PatchMapping("/memos/{id}")
    public ResponseEntity<ApiResponse<Void>> patchMemo(@PathVariable Long id, @RequestBody MemoPatchRequest memoPatchRequest) {
        memoService.patchMemo(id, memoPatchRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @DeleteMapping("/memos/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/memos")
    public ResponseEntity<ApiResponse<Page<MemoListResponse>>> getMemos(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(memoService.getMemos(keyword, pageable)));
    }
}
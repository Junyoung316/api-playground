package com.example.apiplayground.memo.service;

import com.example.apiplayground.common.exception.BusinessException;
import com.example.apiplayground.common.exception.ErrorCode;
import com.example.apiplayground.memo.domain.Memo;
import com.example.apiplayground.memo.dto.request.MemoCreateRequest;
import com.example.apiplayground.memo.dto.request.MemoPatchRequest;
import com.example.apiplayground.memo.dto.request.MemoUpdateRequest;
import com.example.apiplayground.memo.dto.response.MemoDetailResponse;
import com.example.apiplayground.memo.dto.response.MemoListResponse;
import com.example.apiplayground.memo.repository.MemoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

    @InjectMocks
    private MemoService memoService;

    @Mock
    private MemoRepository memoRepository;

    @Test
    @DisplayName("메모 생성 성공")
    void createMemo_success() {
        // Given -> 어떠한 데이터가 주어질 때.
        MemoCreateRequest memoCreateRequest = new MemoCreateRequest("testTitle", "testContent", "test");

        // When -> 어떠한 기능을 실행하면.
        memoService.createMemo(memoCreateRequest);

        // Then -> 어떠한 결과를 기대한다.
        verify(memoRepository, times(1)).save(any(Memo.class));
    }

    @Test
    @DisplayName("메모 목록 조회 성공")
    void getMemos_success() {
        // Given
        Memo memo = Memo.builder()
                .title("testTitle")
                .author("test")
                .build();

        Page<Memo> memoPage = new PageImpl<>(List.of(memo));
        given(memoRepository.findAll(any(Pageable.class))).willReturn(memoPage);

        // When
        Page<MemoListResponse> result = memoService.getMemos(null, PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
    }

    @Test
    @DisplayName("키워드로 검색 성공")
    void getMemos_withKeyword() {
        // Given
        Memo memo = Memo.builder()
                .title("testTitle")
                .author("test")
                .build();

        Page<Memo> memoPage = new PageImpl<>(List.of(memo));
        given(memoRepository.findAll(any(Pageable.class))).willReturn(memoPage);

        // When
        Page<MemoListResponse> result = memoService.getMemos("test", PageRequest.of(0, 10));

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
    }

    @Test
    @DisplayName("메모 단건 조회 성공")
    void getMemo_success() {
        Memo memo = Memo.builder()
                .title("testTitle")
                .author("test")
                .build();

        Page<Memo> memoPage = new PageImpl<>(List.of(memo));
        given(memoRepository.findAll(any(Pageable.class))).willReturn(memoPage);

        MemoDetailResponse memoDetail = memoService.getMemo(1L);

        assertThat(memoDetail).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 메모 조회 시 예외 발생")
    void getMemo_notFound() {
        // Given
        given(memoRepository.findById(1L)).willReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> memoService.getMemo(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMO_NOT_FOUND);
    }

    @Test
    @DisplayName("메모 전체 수정 성공")
    void updateMemo_success() {
        // Given
        Memo memo = Memo.builder()
                .title("testTitle")
                .content("testContent")
                .author("test")
                .build();
        MemoUpdateRequest request = new MemoUpdateRequest("newTitle", "newContent");
        given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

        // When
        memoService.updateMemo(1L, request);

        // Then
        assertThat(memo.getTitle()).isEqualTo("newTitle");
        assertThat(memo.getContent()).isEqualTo("newContent");
    }

    @Test
    @DisplayName("존재하지 않는 메모 수정 시 예외 발생")
    void updateMemo_notFound() {
        // Given
        MemoUpdateRequest request = new MemoUpdateRequest("newTitle", "newContent");
        given(memoRepository.findById(1L)).willReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> memoService.updateMemo(1L, request));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMO_NOT_FOUND);
    }

    @Test
    @DisplayName("메모 부분 수정 성공")
    void patchMemo_success() {
        // Given
        Memo memo = Memo.builder()
                .title("testTitle")
                .content("testContent")
                .author("test")
                .build();
        MemoPatchRequest request = new MemoPatchRequest("newTitle", null);
        given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

        // When
        memoService.patchMemo(1L, request);

        // Then
        assertThat(memo.getTitle()).isEqualTo("newTitle");
        assertThat(memo.getContent()).isEqualTo("testContent");
    }

    @Test
    @DisplayName("메모 삭제 성공")
    void deleteMemo_success() {
        // Given
        Memo memo = Memo.builder()
                .title("testTitle")
                .content("testContent")
                .author("test")
                .build();
        given(memoRepository.findById(1L)).willReturn(Optional.of(memo));

        // When
        memoService.deleteMemo(1L);

        // Then
        verify(memoRepository, times(1)).delete(any(Memo.class));
    }

    @Test
    @DisplayName("존재하지 않는 메모 삭제 시 예외 발생")
    void deleteMemo_notFound() {
        // Given
        given(memoRepository.findById(1L)).willReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> memoService.deleteMemo(1L));
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMO_NOT_FOUND);
    }
}

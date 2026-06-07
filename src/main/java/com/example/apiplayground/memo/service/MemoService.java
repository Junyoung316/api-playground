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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;

    @Transactional
    public void createMemo(MemoCreateRequest memo) {
        Memo newMemo = Memo.builder()
                .title(memo.title())
                .content(memo.content())
                .author(memo.author())
                .build();

        memoRepository.save(newMemo);
    }

    @Transactional(readOnly = true)
    public Page<MemoListResponse> getMemos(Pageable pageable) {
        return memoRepository.findAll(pageable)
                .map(memo -> new MemoListResponse(
                        memo.getId(),
                        memo.getTitle(),
                        memo.getAuthor(),
                        memo.getCreatedAt()
                ));
    }

    @Transactional(readOnly = true)
    public MemoDetailResponse getMemo(long id) {
        Memo memo = memoRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMO_NOT_FOUND));

        return new MemoDetailResponse(
                memo.getId(),
                memo.getTitle(),
                memo.getContent(),
                memo.getAuthor(),
                memo.getCreatedAt(),
                memo.getUpdatedAt()
        );
    }

    @Transactional
    public void updateMemo(Long id, MemoUpdateRequest memo) {
        Memo memoToUpdate = memoRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMO_NOT_FOUND));

        memoToUpdate.put(memo.title(), memo.content());
    }

    @Transactional
    public void patchMemo(Long id, MemoPatchRequest memoPatchRequest) {
        Memo memoToUpdate = memoRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMO_NOT_FOUND));

        String title = (memoPatchRequest.title() != null && !memoPatchRequest.title().isBlank())
                ? memoPatchRequest.title() : null;
        String content = (memoPatchRequest.content() != null && !memoPatchRequest.content().isBlank())
                ? memoPatchRequest.content() : null;

        memoToUpdate.patch(title, content);
    }

    @Transactional
    public void deleteMemo(long id) {
        Memo memo = memoRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.MEMO_NOT_FOUND));

        memoRepository.delete(memo);
    }

    @Transactional(readOnly = true)
    public Page<MemoListResponse> getMemos(String keyword, Pageable pageable) {
        Page<Memo> memos = (keyword != null && !keyword.isBlank())
                ? memoRepository.findByTitleContaining(keyword, pageable)
                : memoRepository.findAll(pageable);

        return memos.map(memo -> new MemoListResponse(
                memo.getId(),
                memo.getTitle(),
                memo.getAuthor(),
                memo.getCreatedAt()
        ));
    }

}
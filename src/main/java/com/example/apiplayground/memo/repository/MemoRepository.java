package com.example.apiplayground.memo.repository;

import com.example.apiplayground.memo.domain.Memo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    Page<Memo> findByTitleContaining(String keyword, Pageable pageable);
}

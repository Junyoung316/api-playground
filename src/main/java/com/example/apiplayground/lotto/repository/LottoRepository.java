package com.example.apiplayground.lotto.repository;

import com.example.apiplayground.lotto.domain.Lotto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRepository extends JpaRepository<Lotto, Long> {
}

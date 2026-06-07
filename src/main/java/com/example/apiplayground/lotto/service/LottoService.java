package com.example.apiplayground.lotto.service;

import com.example.apiplayground.lotto.domain.Lotto;
import com.example.apiplayground.lotto.dto.LottoHistoryResponse;
import com.example.apiplayground.lotto.dto.LottoResponse;
import com.example.apiplayground.lotto.repository.LottoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LottoService {

    private final LottoRepository lottoRepository;

    private List<Integer> generateNumbers() {
        List<Integer> numbers = new ArrayList<>(IntStream.rangeClosed(1, 45).boxed().toList());

        Collections.shuffle(numbers);

        return numbers.subList(0, 6).stream().sorted().collect(Collectors.toList());
    }

    @Transactional // 로또 번호 생성 및 저장
    public LottoResponse generateLotto() {

        List<Integer> numbers = generateNumbers();

        Lotto lotto = Lotto.builder().numbers(numbers).build();

        lottoRepository.save(lotto);

        return new LottoResponse(numbers);
    }

    @Transactional(readOnly = true) // 생성 이력 조회(페이징)
    public Page<LottoHistoryResponse> getLottoHistory(Pageable pageable) {
        return lottoRepository.findAll(pageable)
                .map(lotto -> new LottoHistoryResponse(
                        lotto.getId(),
                        lotto.getNumbers(),
                        lotto.getCreatedAt()
                ));
    }

}

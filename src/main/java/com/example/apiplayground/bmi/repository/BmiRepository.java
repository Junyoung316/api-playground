package com.example.apiplayground.bmi.repository;

import com.example.apiplayground.bmi.domain.Bmi;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BmiRepository extends JpaRepository<Bmi, Long> {
}

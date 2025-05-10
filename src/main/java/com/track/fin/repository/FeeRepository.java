package com.track.fin.repository;

import com.track.fin.domain.Fee;
import com.track.fin.type.GradeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    Optional<Fee> findByGradeType(GradeType gradeType);
}

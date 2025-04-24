package com.track.fin.service;

import com.track.fin.domain.Fee;
import com.track.fin.exception.FeeException;
import com.track.fin.repository.FeeRepository;
import com.track.fin.type.ErrorCode;
import com.track.fin.type.GradeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeRepository feeRepository;

    @Transactional
    public Fee createFeeByGrade(GradeType gradeType) {
        Fee fee = Fee.builder()
                .gradeType(gradeType)
                .collateralRate(gradeType.getCollateralRate())
                .interestRate(gradeType.getInterestRate())
                .discount(gradeType.getDiscount())
                .build();

        return feeRepository.save(fee);
    }

    @Transactional
    public Fee updateFee(Long id, GradeType newGrade) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new FeeException(ErrorCode.FEE_NOT_FOUND));

        fee = Fee.builder()
                .id(fee.getId())
                .gradeType(newGrade)
                .collateralRate(newGrade.getCollateralRate())
                .interestRate(newGrade.getInterestRate())
                .discount(newGrade.getDiscount())
                .build();

        return feeRepository.save(fee);
    }

    public Fee getFeeByGrade(GradeType gradeType) {
        return feeRepository.findByGradeType(gradeType)
                .orElseThrow(() -> new IllegalArgumentException("등급에 해당하는 수수료 없음"));
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    @Transactional
    public void deleteFee(Long id) {
        feeRepository.deleteById(id);
    }
}

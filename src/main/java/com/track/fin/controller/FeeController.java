package com.track.fin.controller;

import com.track.fin.domain.Fee;
import com.track.fin.record.FeeRecord;
import com.track.fin.service.FeeService;
import com.track.fin.type.GradeType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    @PostMapping("/fee/{gradeType}")
    public FeeRecord createFee(@PathVariable GradeType gradeType) {
        return FeeRecord.from(feeService.createFeeByGrade(gradeType));
    }

    @PutMapping("/fee/{id}/{newGrade}")
    public FeeRecord updateFee(@PathVariable Long id, @PathVariable GradeType newGrade) {
        return FeeRecord.from(feeService.updateFee(id, newGrade));
    }

    @GetMapping("/fee/{gradeType}")
    public FeeRecord getFee(@PathVariable GradeType gradeType) {
        return FeeRecord.from(feeService.getFeeByGrade(gradeType));
    }

    @GetMapping
    public List<Fee> getAllFees() {
        return feeService.getAllFees();
    }

    @DeleteMapping("/fee/{id}")
    public void deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
    }
}

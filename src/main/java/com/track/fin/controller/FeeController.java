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

    @PostMapping("/fee")
    public FeeRecord createFee(@RequestBody FeeRecord feeRecord) {
        return FeeRecord.from(feeService.createFeeByGrade(feeRecord));
    }

    @GetMapping("/fee")
    public List<Fee> getAllFees() {
        return feeService.getAllFees();
    }

    @GetMapping("fee/{gradeType}")
    public FeeRecord getFee(@PathVariable GradeType gradeType) {
        return FeeRecord.from(feeService.getFeeByGrade(gradeType));
    }

    @PutMapping("fee/{id}")
    public FeeRecord updateFee(@PathVariable Long id, @RequestBody FeeRecord feeRecord) {
        return FeeRecord.from(feeService.updateFee(id, feeRecord));
    }

    @DeleteMapping("fee/{id}")
    public void deleteFee(@PathVariable Long id) {
        feeService.deleteFee(id);
    }

}

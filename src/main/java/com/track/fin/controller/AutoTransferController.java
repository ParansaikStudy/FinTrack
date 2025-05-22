package com.track.fin.controller;

import com.track.fin.service.AutoTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AutoTransferController {

    private final AutoTransferService autoTransferService;

    @GetMapping("/accounts/auto-transfer/{accountNumber}")
    public boolean isAutoTransferRegistered(@PathVariable String accountNumber) {
        return autoTransferService.isAutoTransferRegistered(accountNumber);
    }

    @GetMapping("/accounts/auto-transfer/{accountNumber}/validate")
    public void validateAutoTransferNotRegistered(@PathVariable String accountNumber) {
        autoTransferService.validateAutoTransferNotRegistered(accountNumber);
    }
}

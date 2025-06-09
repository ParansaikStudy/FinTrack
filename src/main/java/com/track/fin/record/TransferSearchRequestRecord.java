package com.track.fin.record;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record TransferSearchRequestRecord(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endDate,

        Boolean sortDesc,

        String transactionType
) {}

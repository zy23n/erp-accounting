package com.erp.erp_accounting.accounting.period.controller;

import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodCloseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/closing")
@RequiredArgsConstructor
public class AccountingPeriodCloseController {

    private final AccountingPeriodCloseService accountingPeriodCloseService;

    // 마감
    @PatchMapping("/{period}/close")
    public ResponseEntity<AccountingPeriodResponse> close(
            @PathVariable("period") String period,
            @RequestHeader("X-USER-ID") Long userId // 추후 로그인/권한 적용
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.closePeriod(YearMonth.parse(period), userId));
    }

    // 마감 취소
    @PatchMapping("/{period}/reopen")
    public ResponseEntity<AccountingPeriodResponse> reopen(
            @PathVariable("period") String period,
            @RequestHeader("X-USER-ID") Long userId // 추후 로그인/권한 적용
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.reopenPeriod(YearMonth.parse(period), userId));
    }
}

package com.erp.erp_accounting.accounting.period.controller;

import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodCloseService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.closePeriod(YearMonth.parse(period), principal.getUser()));
    }

    // 마감 취소
    @PatchMapping("/{period}/reopen")
    public ResponseEntity<AccountingPeriodResponse> reopen(
            @PathVariable("period") String period,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.reopenPeriod(YearMonth.parse(period), principal.getUser()));
    }
}

package com.erp.erp_accounting.accounting.period.controller;

import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodCloseService;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Slf4j
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
        log.info("[ACCOUNTING_PERIOD] action=CLOSE_REQUEST, period={}, closerId={}", period, principal.getUser().getId());

        AccountingPeriodResponse response = accountingPeriodCloseService.closePeriod(YearMonth.parse(period), principal.getUser());

        log.info("[ACCOUNTING_PERIOD] action=CLOSE_COMPLETE, period={}, closerId={}, status={}",
                period, principal.getUser().getId(), response.getStatus());
        return ResponseEntity.ok(response);
    }

    // 마감 취소
    @PatchMapping("/{period}/reopen")
    public ResponseEntity<AccountingPeriodResponse> reopen(
            @PathVariable("period") String period,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        log.info("[ACCOUNTING_PERIOD] action=REOPEN_REQUEST, period={}, reopenerId={}", period, principal.getUser().getId());

        AccountingPeriodResponse response = accountingPeriodCloseService.reopenPeriod(YearMonth.parse(period), principal.getUser());

        log.info("[ACCOUNTING_PERIOD] action=REOPEN_COMPLETE, period={}, reopenerId={}, status={}",
                period, principal.getUser().getId(), response.getStatus());

        return ResponseEntity.ok(response);
    }
}

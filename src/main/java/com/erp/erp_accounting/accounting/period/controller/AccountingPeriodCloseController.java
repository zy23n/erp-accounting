package com.erp.erp_accounting.accounting.period.controller;

import com.erp.erp_accounting.accounting.period.dto.response.AccountingPeriodResponse;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodCloseService;
import com.erp.erp_accounting.security.annotation.CurrentUser;
import com.erp.erp_accounting.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/closing")
@RequiredArgsConstructor
@Tag(name = "회계기간 마감", description = "회계기간 마감 및 마감 취소 API")
public class AccountingPeriodCloseController {

    private final AccountingPeriodCloseService accountingPeriodCloseService;

    @Operation(summary = "회계기간 마감", description = "지정한 회계기간을 마감 처리합니다.")
    @PatchMapping("/{period}/close")
    public ResponseEntity<AccountingPeriodResponse> close(
            @Parameter(description = "회계기간 (yyyy-MM)", required = true, example = "2026-01")
            @PathVariable("period") String period, @CurrentUser User user
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.closePeriod(YearMonth.parse(period), user));
    }

    @Operation(summary = "회계기간 마감 취소", description = "이미 마감된 회계기간을 다시 오픈합니다.")
    @PatchMapping("/{period}/reopen")
    public ResponseEntity<AccountingPeriodResponse> reopen(
            @Parameter(description = "회계기간 (yyyy-MM)", required = true, example = "2026-01")
            @PathVariable("period") String period, @CurrentUser User user
    ) {
        return ResponseEntity.ok(accountingPeriodCloseService.reopenPeriod(YearMonth.parse(period), user));
    }
}

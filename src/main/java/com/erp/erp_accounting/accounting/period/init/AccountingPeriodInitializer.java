package com.erp.erp_accounting.accounting.period.init;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Year;

@Component
@RequiredArgsConstructor
public class AccountingPeriodInitializer {

    private final AccountingPeriodService accountingPeriodService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        int currentYear = Year.now().getValue();
        accountingPeriodService.createYear(currentYear);
    }
}
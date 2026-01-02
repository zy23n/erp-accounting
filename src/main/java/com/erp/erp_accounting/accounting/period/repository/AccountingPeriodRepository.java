package com.erp.erp_accounting.accounting.period.repository;

import com.erp.erp_accounting.accounting.period.entity.AccountingPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface AccountingPeriodRepository extends JpaRepository<AccountingPeriod, Long> {

    Optional<AccountingPeriod> findByPeriod(YearMonth period);
}

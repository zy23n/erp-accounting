package com.erp.erp_accounting.accounting.balance.repository;

import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface MonthlyAccountBalanceRepository extends JpaRepository<MonthlyAccountBalance, Long> {

    List<MonthlyAccountBalance> findByPeriod(YearMonth period);
}

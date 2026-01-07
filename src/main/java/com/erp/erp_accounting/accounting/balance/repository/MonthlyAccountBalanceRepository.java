package com.erp.erp_accounting.accounting.balance.repository;

import com.erp.erp_accounting.accounting.balance.entity.MonthlyAccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface MonthlyAccountBalanceRepository extends JpaRepository<MonthlyAccountBalance, Long> {

    // 월 전체 스냅샷 조회
    List<MonthlyAccountBalance> findByPeriod(YearMonth period);

    // 특정 계정의 월 스냅샷 조회
    Optional<MonthlyAccountBalance> findByPeriodAndAccountId(YearMonth period, Long accountId);

    // 월 마감 재처리 대비
    void deleteByPeriod(YearMonth period);
}

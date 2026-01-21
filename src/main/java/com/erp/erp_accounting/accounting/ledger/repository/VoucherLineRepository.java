package com.erp.erp_accounting.accounting.ledger.repository;

import com.erp.erp_accounting.accounting.balance.dto.query.AccountAmountDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VoucherLineRepository extends JpaRepository<VoucherLine, Long> {

    // 전기이월 잔액 계산
    @Query("""
        select new com.erp.erp_accounting.accounting.ledger.dto.query.OpeningBalanceDto(
            coalesce(sum(case when vl.type = 'DEBIT' then vl.amount else 0 end), 0),
            coalesce(sum(case when vl.type = 'CREDIT' then vl.amount else 0 end), 0)
        )
        from VoucherLine vl
        join vl.voucher v
        where vl.account.id = :accountId
          and v.voucherDate < :startDate
          and v.status = 'APPROVED'
    """)
    OpeningBalanceDto findOpeningBalance(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate
    );

    // 이번 달 총합 조회
    @Query("""
        select new com.erp.erp_accounting.accounting.ledger.dto.query.MonthlyBalanceQueryDto(
            coalesce(sum(case when vl.type = 'DEBIT' then vl.amount else 0 end), 0),
            coalesce(sum(case when vl.type = 'CREDIT' then vl.amount else 0 end), 0)
        )
        from VoucherLine vl
        join vl.voucher v
        where vl.account.id = :accountId
          and v.voucherDate between :monthStart and :monthEnd
          and v.status = 'APPROVED'
    """)
    MonthlyBalanceQueryDto findMonthlyTotal(
            @Param("accountId") Long accountId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );

    // 특정 기간 차변 합계
    @Query("""
        select new com.erp.erp_accounting.accounting.balance.dto.query.AccountAmountDto(
            vl.account.id,
            sum(vl.amount)
        )
        from VoucherLine vl
        join vl.voucher v
        where vl.type = 'DEBIT'
          and v.voucherDate between :start and :end
          and v.status = 'APPROVED'
        group by vl.account.id
    """)
    List<AccountAmountDto> findMonthlyDebitSum(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // 특정 기간 대변 합계
    @Query("""
        select new com.erp.erp_accounting.accounting.balance.dto.query.AccountAmountDto(
            vl.account.id,
            sum(vl.amount)
        )
        from VoucherLine vl
        join vl.voucher v
        where vl.type = 'CREDIT'
          and v.voucherDate between :start and :end
          and v.status = 'APPROVED'
        group by vl.account.id
    """)
    List<AccountAmountDto> findMonthlyCreditSum(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
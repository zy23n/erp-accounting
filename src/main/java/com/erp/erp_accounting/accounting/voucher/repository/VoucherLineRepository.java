package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.ledger.dto.AccountLedgerQueryDto;
import com.erp.erp_accounting.accounting.ledger.dto.OpeningBalanceDto;
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
        select new com.erp.erp_accounting.accounting.ledger.dto.OpeningBalanceDto(
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

    // 원장 조회
    @Query("""
        select new com.erp.erp_accounting.accounting.ledger.dto.AccountLedgerQueryDto(
            v.voucherDate,
            v.voucherNo,
            v.description,
            vl.type,
            vl.amount
        )
        from VoucherLine vl
        join vl.voucher v
        where vl.account.id = :accountId
          and v.voucherDate between :startDate and :endDate
          and v.status = 'APPROVED'
        order by v.voucherDate, vl.id
    """)
    List<AccountLedgerQueryDto> findAccountLedger(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

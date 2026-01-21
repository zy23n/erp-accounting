package com.erp.erp_accounting.accounting.ledger.repository;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AccountLedgerQueryRepositoryImpl implements AccountLedgerQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AccountLedgerItemDto> searchLedger(AccountLedgerSearchCondition condition) {
        QVoucherLine vl = QVoucherLine.voucherLine;
        QVoucher v = QVoucher.voucher;

        return queryFactory
                .select(Projections.constructor(
                        AccountLedgerItemDto.class,
                        v.voucherDate,
                        v.voucherNo,
                        v.description,
                        new CaseBuilder()
                                .when(vl.type.eq(LineType.DEBIT))
                                .then(vl.amount)
                                .otherwise(BigDecimal.ZERO),
                        new CaseBuilder()
                                .when(vl.type.eq(LineType.CREDIT))
                                .then(vl.amount)
                                .otherwise(BigDecimal.ZERO),
                        Expressions.nullExpression(BigDecimal.class)
                ))
                .from(vl)
                .join(vl.voucher, v)
                .where(
                        vl.account.id.eq(condition.getAccountId()),
                        voucherDateBetween(condition.getStartDateOrDefault(), condition.getEndDateOrDefault(), v),
                        v.status.eq(VoucherStatus.APPROVED)
                )
                .orderBy(
                        v.voucherDate.asc(),
                        v.voucherNo.asc(),
                        vl.id.asc()
                )
                .fetch();
    }

    private BooleanExpression voucherDateBetween(LocalDate start, LocalDate end, QVoucher v) {
        return v.voucherDate.between(start, end);
    }
}

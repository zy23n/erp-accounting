package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.voucher.dto.query.VoucherSearchCondition;
import com.erp.erp_accounting.accounting.voucher.dto.response.QVoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.common.util.QuerydslUtils;
import com.erp.erp_accounting.user.entity.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class VoucherQueryRepositoryImpl implements VoucherQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VoucherListResponse> search(VoucherSearchCondition cond, Pageable pageable) {

        QVoucher v = QVoucher.voucher;
        QVoucherLine l = QVoucherLine.voucherLine;
        QUser u = QUser.user;

        NumberExpression<BigDecimal> debitSumExpr =
                new CaseBuilder()
                        .when(l.type.eq(LineType.DEBIT))
                        .then(l.amount)
                        .otherwise(BigDecimal.ZERO)
                        .sum();

        NumberExpression<BigDecimal> creditSumExpr =
                new CaseBuilder()
                        .when(l.type.eq(LineType.CREDIT))
                        .then(l.amount)
                        .otherwise(BigDecimal.ZERO)
                        .sum();

        // 정렬 필드 매핑
        Map<String, ComparableExpressionBase<?>> sortMap = Map.of(
                "voucherDate", v.voucherDate,
                "voucherNo", v.voucherNo,
                "createdAt", v.createdAt,
                "voucherType", v.voucherType,
                "sourceType", v.sourceType
        );

        List<VoucherListResponse> content =
                queryFactory
                    .select(new QVoucherListResponse(
                            v.id,
                            v.voucherNo,
                            v.voucherDate,
                            v.status,
                            v.description,
                            u.id,
                            u.username,
                            debitSumExpr,
                            creditSumExpr,
                            v.createdAt,
                            v.voucherType,
                            v.sourceType
                    ))
                    .from(v)
                    .join(v.createdBy, u)
                    .leftJoin(v.lines, l)
                    .where(
                            voucherNoContains(cond.getVoucherNo(), v),
                            statusEq(cond.getStatus(), v),
                            voucherTypeEq(cond.getVoucherType(), v),
                            sourceTypeEq(cond.getSourceType(), v),
                            voucherDateBetween(cond.getStartDate(), cond.getEndDate(), cond.getVoucherDate(), v),
                            accountEq(cond.getAccountId(), v)
                    )
                    .groupBy(
                            v.id,
                            v.voucherNo,
                            v.voucherDate,
                            v.status,
                            v.description,
                            u.id,
                            u.username,
                            v.createdAt,
                            v.voucherType,
                            v.sourceType
                    )
                    .orderBy(QuerydslUtils.toOrderSpecifiers(pageable, sortMap))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        Long total =
                queryFactory
                    .select(v.countDistinct())
                    .from(v)
                    .where(
                            voucherNoContains(cond.getVoucherNo(), v),
                            statusEq(cond.getStatus(), v),
                            voucherTypeEq(cond.getVoucherType(), v),
                            sourceTypeEq(cond.getSourceType(), v),
                            voucherDateBetween(cond.getStartDate(), cond.getEndDate(), cond.getVoucherDate(), v),
                            accountEq(cond.getAccountId(), v)
                    )
                    .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /* ===== 조건 메서드 ===== */
    private BooleanExpression voucherNoContains(String voucherNo, QVoucher v) {
        return hasText(voucherNo) ? v.voucherNo.contains(voucherNo) : null;
    }

    private BooleanExpression statusEq(VoucherStatus status, QVoucher v) {
        return status != null ? v.status.eq(status) : null;
    }

    private BooleanExpression voucherTypeEq(VoucherType type, QVoucher v) {
        return type != null ? v.voucherType.eq(type) : null;
    }

    private BooleanExpression sourceTypeEq(SourceType sourceType, QVoucher v) {
        return sourceType != null ? v.sourceType.eq(sourceType) : null;
    }

    private BooleanExpression voucherDateBetween(LocalDate start, LocalDate end, LocalDate exactDate, QVoucher v) {
        if (exactDate != null) return v.voucherDate.eq(exactDate);
        if (start != null && end != null) return v.voucherDate.between(start, end);
        if (start != null) return v.voucherDate.goe(start);
        if (end != null) return v.voucherDate.loe(end);
        return null;
    }

    private BooleanExpression accountEq(Long accountId, QVoucher v) {
        if (accountId == null) return null;

        QVoucherLine l = QVoucherLine.voucherLine;

        return JPAExpressions
                .selectOne()
                .from(l)
                .where(
                        l.voucher.eq(v),
                        l.account.id.eq(accountId)
                )
                .exists();
    }
}
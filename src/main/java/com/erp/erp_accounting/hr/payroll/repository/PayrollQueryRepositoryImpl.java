package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.common.util.QuerydslUtils;
import com.erp.erp_accounting.hr.employee.entity.QEmployee;
import com.erp.erp_accounting.hr.payroll.dto.query.PayrollSearchCondition;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import com.erp.erp_accounting.hr.payroll.entity.QPayroll;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class PayrollQueryRepositoryImpl implements PayrollQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PayrollListResponse> search(PayrollSearchCondition cond, Pageable pageable) {
        return executeQuery(null, cond, pageable);
    }

    @Override
    public Page<PayrollListResponse> searchByEmployee(Long employeeId, PayrollSearchCondition cond, Pageable pageable) {
        return executeQuery(employeeId, cond, pageable);
    }

    private Page<PayrollListResponse> executeQuery(Long employeeId, PayrollSearchCondition cond, Pageable pageable) {

        QPayroll p = QPayroll.payroll;
        QEmployee e = QEmployee.employee;

        Map<String, ComparableExpressionBase<?>> sortMap = Map.of(
                "payMonth", p.payMonth,
                "baseSalary", p.baseSalary,
                "netAmount", p.netAmount,
                "status", p.status,
                "createdAt", p.createdAt
        );

        List<PayrollListResponse> content =
                queryFactory
                        .select(Projections.constructor(
                                PayrollListResponse.class,
                                p.id,
                                e.id,
                                e.name,
                                e.empNo,
                                e.department,
                                e.position,
                                p.payMonth,
                                p.netAmount,
                                p.status
                        ))
                        .from(p)
                        .join(p.employee, e)
                        .where(
                                employeeEq(employeeId, e),
                                empNoContains(cond.getEmpNo(), e),
                                empNameContains(cond.getEmpName(), e),
                                departmentEq(cond.getDepartment(), e),
                                positionEq(cond.getPosition(), e),
                                statusEq(cond.getStatus(), p),
                                payMonthBetween(cond.getPayMonth(), cond.getStartPayMonth(), cond.getEndPayMonth(), p),
                                hireDateBetween(cond.getStartHireDate(), cond.getEndHireDate(), e)
                        )
                        .orderBy(QuerydslUtils.toOrderSpecifiers(pageable, sortMap))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(p.count())
                        .from(p)
                        .join(p.employee, e)
                        .where(
                                employeeEq(employeeId, e),
                                empNoContains(cond.getEmpNo(), e),
                                empNameContains(cond.getEmpName(), e),
                                departmentEq(cond.getDepartment(), e),
                                positionEq(cond.getPosition(), e),
                                statusEq(cond.getStatus(), p),
                                payMonthBetween(cond.getPayMonth(), cond.getStartPayMonth(), cond.getEndPayMonth(), p),
                                hireDateBetween(cond.getStartHireDate(), cond.getEndHireDate(), e)
                        )
                        .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    /* ===== 조건 메서드 ===== */
    private BooleanExpression employeeEq(Long employeeId, QEmployee e) {
        return employeeId != null ? e.id.eq(employeeId) : null;
    }

    private BooleanExpression empNoContains(String empNo, QEmployee e) {
        return hasText(empNo) ? e.empNo.contains(empNo) : null;
    }

    private BooleanExpression empNameContains(String name, QEmployee e) {
        return hasText(name) ? e.name.contains(name) : null;
    }

    private BooleanExpression departmentEq(String department, QEmployee e) {
        return hasText(department) ? e.department.eq(department) : null;
    }

    private BooleanExpression positionEq(String position, QEmployee e) {
        return hasText(position) ? e.position.eq(position) : null;
    }

    private BooleanExpression statusEq(PayrollStatus status, QPayroll p) {
        return status != null ? p.status.eq(status) : null;
    }

    private BooleanExpression payMonthBetween(YearMonth exact, YearMonth start, YearMonth end, QPayroll p) {
        if (exact != null) return p.payMonth.eq(exact);
        if (start != null && end != null) return p.payMonth.between(start, end);
        if (start != null) return p.payMonth.goe(start);
        if (end != null) return p.payMonth.loe(end);
        return null;
    }

    private BooleanExpression hireDateBetween(LocalDate start, LocalDate end, QEmployee e) {
        if (start != null && end != null) return e.hireDate.between(start, end);
        if (start != null) return e.hireDate.goe(start);
        if (end != null) return e.hireDate.loe(end);
        return null;
    }
}

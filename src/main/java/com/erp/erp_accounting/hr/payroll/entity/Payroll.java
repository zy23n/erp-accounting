package com.erp.erp_accounting.hr.payroll.entity;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.common.base.BaseEntity;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.math.BigDecimal;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_payroll_employee_month",
                        columnNames = {"employee_id", "pay_month"}
                )
        }
)
@Entity
@Getter
@NoArgsConstructor
public class Payroll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "pay_month", nullable = false)
    private YearMonth payMonth;

    @Column(name = "base_salary", nullable = false, precision=15, scale=2)
    private BigDecimal baseSalary;

    @Column(name = "allowance_amount", nullable = false, precision=15, scale=2)
    private BigDecimal allowanceAmount;

    @Column(name = "deduction_amount", nullable = false, precision=15, scale=2)
    private BigDecimal deductionAmount;

    @Column(name = "net_amount", precision=15, scale=2)
    private BigDecimal netAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_confirm_id")
    private PayrollConfirm payrollConfirm;

    // 헤더 연결
    public void setPayrollConfirm(PayrollConfirm payrollConfirm) {
        this.payrollConfirm = payrollConfirm;
    }

    // 총 급여 계산
    public void calculateNetAmount() {
        if (baseSalary == null || allowanceAmount == null || deductionAmount == null) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "급여 항목 금액 누락 (기본급/수당/공제)");
        }

        if (baseSalary.signum() < 0 || allowanceAmount.signum() < 0 || deductionAmount.signum() < 0) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "급여 항목 금액 범위 오류 (0 이상)");
        }

        this.netAmount = baseSalary.add(allowanceAmount).subtract(deductionAmount);

        if (this.netAmount.signum() < 0) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "총 급여 금액 음수");
        }

        this.status = PayrollStatus.CALCULATED;
    }

    public void markConfirmed() {
        if (this.status != PayrollStatus.CALCULATED) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "급여 상태 불일치 (required=CALCULATED)");
        }
        this.status = PayrollStatus.CONFIRMED;
    }

    public void rollbackToCalculated() {
        if (this.status != PayrollStatus.CONFIRMED) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "급여 상태 불일치 (required=CONFIRMED)");
        }
        this.status = PayrollStatus.CALCULATED;
    }

    @Builder
    public Payroll(Employee employee, YearMonth payMonth, BigDecimal baseSalary,
                   BigDecimal allowanceAmount, BigDecimal deductionAmount, PaymentMethod paymentMethod) {
        this.employee = employee;
        this.payMonth = payMonth;
        this.baseSalary = baseSalary;
        this.allowanceAmount = allowanceAmount;
        this.deductionAmount = deductionAmount;
        this.paymentMethod = paymentMethod;
        this.status = PayrollStatus.CREATED;
    }
}

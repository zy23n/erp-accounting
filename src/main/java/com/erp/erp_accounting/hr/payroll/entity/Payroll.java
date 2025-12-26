package com.erp.erp_accounting.hr.payroll.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_confirm_id")
    private PayrollConfirm payrollConfirm;

    // 편의 메서드 : 헤더 연결
    public void setPayrollConfirm(PayrollConfirm payrollConfirm) {
        this.payrollConfirm = payrollConfirm;
    }

    // 총 급여 계산
    public void calculateNetAmount() {
        if (baseSalary == null || allowanceAmount == null || deductionAmount == null) {
            throw new IllegalStateException("급여, 수당, 공제 금액이 모두 있어야 계산 가능");
        }
        this.netAmount = baseSalary.add(allowanceAmount).subtract(deductionAmount);
    }

    @Builder
    public Payroll(Employee employee, YearMonth payMonth, BigDecimal baseSalary,
                   BigDecimal allowanceAmount, BigDecimal deductionAmount, BigDecimal netAmount,
                   PayrollStatus status, PayrollConfirm payrollConfirm) {
        this.employee = employee;
        this.payMonth = payMonth;
        this.baseSalary = baseSalary;
        this.allowanceAmount = allowanceAmount;
        this.deductionAmount = deductionAmount;
        this.netAmount = netAmount;
        this.status = status;
        this.payrollConfirm = payrollConfirm;
    }
}

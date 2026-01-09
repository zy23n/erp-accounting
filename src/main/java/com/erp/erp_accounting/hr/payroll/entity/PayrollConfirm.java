package com.erp.erp_accounting.hr.payroll.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import com.erp.erp_accounting.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class PayrollConfirm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pay_month", unique = true, nullable = false)
    private YearMonth payMonth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollConfirmStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToMany(mappedBy = "payrollConfirm", cascade = CascadeType.ALL)
    private List<Payroll> payrolls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by")
    private User canceledBy;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    public void addPayroll(Payroll payroll) {
        if (this.status == PayrollConfirmStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 급여에는 급여 추가 불가");
        }

        if (payroll.getPayrollConfirm() != null) {
            throw new IllegalStateException("이미 다른 급여 확정에 포함된 급여는 추가 불가");
        }

        if (!this.payMonth.equals(payroll.getPayMonth())) {
            throw new IllegalStateException("급여 월이 급여 확정 월과 불일치");
        }

        if (payroll.getStatus() != PayrollStatus.CALCULATED) {
            throw new IllegalStateException("CALCULATED 상태의 급여만 확정 가능");
        }

        payrolls.add(payroll);
        payroll.setPayrollConfirm(this);
    }

    public void removePayroll(Payroll payroll) {
        payrolls.remove(payroll);
        payroll.setPayrollConfirm(null);
    }

    // 재확정을 위한 초기화
    public void clearPayrolls() {
        payrolls.forEach(p -> p.setPayrollConfirm(null));
        payrolls.clear();
    }

    // 확정 (최초 확정 + 재확정 공용)
    public void confirm(User confirmer) {
        if (this.status == PayrollConfirmStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 급여확정");
        }

        this.status = PayrollConfirmStatus.CONFIRMED;
        this.confirmedBy = confirmer;
        this.confirmedAt = LocalDateTime.now();

        this.payrolls.forEach(Payroll::markConfirmed);
    }

    // 확정 취소
    public void cancel(User canceler) {
        if (this.status != PayrollConfirmStatus.CONFIRMED) {
            throw new IllegalStateException("확정된 급여만 취소 가능");
        }

        this.status = PayrollConfirmStatus.CANCELED;
        this.canceledBy = canceler;
        this.canceledAt = LocalDateTime.now();

        this.payrolls.forEach(Payroll::rollbackToCalculated);
        this.clearPayrolls();
    }

    @Builder
    public PayrollConfirm(YearMonth payMonth) {
        this.payMonth = payMonth;
        this.status = PayrollConfirmStatus.CREATED;
    }
}

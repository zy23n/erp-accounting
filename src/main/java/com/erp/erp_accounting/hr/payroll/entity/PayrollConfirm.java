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

    @OneToMany(mappedBy = "payrollConfirm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payroll> payrolls = new ArrayList<>();

    // 편의 메서드 : Payroll 추가
    public void addPayroll(Payroll payroll) {
        payrolls.add(payroll);
        payroll.setPayrollConfirm(this);
    }

    // 편의 메서드 : Payroll 제거
    public void removePayroll(Payroll payroll) {
        payrolls.remove(payroll);
        payroll.setPayrollConfirm(null);
    }

    // 상태 변경 : 확정
    public void confirm(User user) {
        this.status = PayrollConfirmStatus.CONFIRMED;
        this.confirmedBy = user;
        this.confirmedAt = LocalDateTime.now();
    }

    // 상태 변경 : 취소
    public void cancel() {
        this.status = PayrollConfirmStatus.CANCELED;
        this.confirmedBy = null;
        this.confirmedAt = null;
    }

    @Builder
    public PayrollConfirm(YearMonth payMonth, PayrollConfirmStatus status) {
        this.payMonth = payMonth;
        this.status = status;
    }
}

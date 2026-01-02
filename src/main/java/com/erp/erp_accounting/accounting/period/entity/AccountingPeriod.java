package com.erp.erp_accounting.accounting.period.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import com.erp.erp_accounting.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingPeriod extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private YearMonth period; // "2026-01"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountingPeriodStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by")
    private User closedBy;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public void close(User closedBy, LocalDateTime closedAt) {
        this.status = AccountingPeriodStatus.CLOSED;
        this.closedBy = closedBy;
        this.closedAt = closedAt;
    }
    public void reopen() {
        this.status = AccountingPeriodStatus.OPEN;
        this.closedBy = null;
        this.closedAt = null;
    }
}

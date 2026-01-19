package com.erp.erp_accounting.accounting.voucher.entity;

import com.erp.erp_accounting.accounting.account.entity.Account;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class VoucherLine implements LineAmount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;  // 전표 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;  // 계정 참조

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LineType type; // DEBIT, CREDIT

    @Column(nullable = false)
    private BigDecimal amount;

    @Override
    public LineType getType() {
        return type;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Builder
    public VoucherLine(Voucher voucher, Account account, LineType type, BigDecimal amount) {
        this.voucher = voucher;
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    // 양방향 연관관계 편의 메서드
    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}

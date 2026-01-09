package com.erp.erp_accounting.accounting.voucher.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import com.erp.erp_accounting.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "voucher_no", unique = true, nullable = false)
    private String voucherNo;

    @Column(name = "voucher_date", nullable = false)
    private LocalDate voucherDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", nullable = false)
    private VoucherType voucherType;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private SourceType sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoucherStatus status;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherLine> lines = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canceled_by")
    private User canceledBy;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Builder
    public Voucher(String voucherNo, LocalDate voucherDate, User createdBy, VoucherStatus status, String description,
                   VoucherType voucherType, SourceType sourceType, Long sourceId) {
        this.voucherNo = voucherNo;
        this.voucherDate = voucherDate;
        this.createdBy = createdBy;
        this.status = status;
        this.description = description;
        this.voucherType = voucherType;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
    }

    // 양방향 연관관계 헬퍼 메서드
    public void addLine(VoucherLine line) {
        lines.add(line);
        line.setVoucher(this);
    }

    public void removeLine(VoucherLine line) {
        lines.remove(line);
        line.setVoucher(null);
    }

    // 상태 변경 헬퍼 메서드
    private void assertDraft() {
        if (this.status != VoucherStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태의 전표만 처리 가능");
        }
    }

    public void validateBalanced() {
        BigDecimal debitSum = BigDecimal.ZERO;
        BigDecimal creditSum = BigDecimal.ZERO;

        if (lines.isEmpty()) {
            throw new IllegalStateException("전표 라인 없음");
        }

        for (VoucherLine line : lines) {
            BigDecimal amount = line.getAmount();

            if (line.getType() == LineType.DEBIT) {
                debitSum = debitSum.add(amount);
            } else if (line.getType() == LineType.CREDIT) {
                creditSum = creditSum.add(amount);
            }
        }

        if (debitSum.compareTo(creditSum) != 0) {
            throw new IllegalStateException(
                    String.format("대차 불일치 (차변=%s, 대변=%s)", debitSum, creditSum)
            );
        }
    }

    public void approve(User approver) {
        assertDraft();
        validateBalanced();
        this.status = VoucherStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(User approver) {
        assertDraft();
        this.status = VoucherStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public boolean isCancelable() {
        return this.status == VoucherStatus.APPROVED;
    }

    public void cancel(User canceler) {
        if (!isCancelable()) {
            throw new IllegalStateException("승인된 전표만 취소 가능");
        }

        this.status = VoucherStatus.CANCELED;
        this.canceledBy = canceler;
        this.canceledAt = LocalDateTime.now();
    }
}

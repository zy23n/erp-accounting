package com.erp.erp_accounting.accounting.voucher.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import com.erp.erp_accounting.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private LocalDateTime approvedAt;

    @Builder
    public Voucher(String voucherNo, LocalDate voucherDate, User createdBy, VoucherStatus status, String description) {
        this.voucherNo = voucherNo;
        this.voucherDate = voucherDate;
        this.createdBy = createdBy;
        this.status = status;
        this.description = description;
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
    public void approve(User approver) {
        if (this.status != VoucherStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태의 전표만 승인할 수 있습니다.");
        }
        this.status = VoucherStatus.APPROVED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(User approver) {
        if (this.status != VoucherStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태의 전표만 반려할 수 있습니다.");
        }
        this.status = VoucherStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }
}

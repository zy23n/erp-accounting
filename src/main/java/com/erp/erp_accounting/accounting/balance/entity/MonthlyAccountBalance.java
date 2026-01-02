package com.erp.erp_accounting.accounting.balance.entity;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.entity.NormalBalance;
import com.erp.erp_accounting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"account_id", "period"})
        }
)
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyAccountBalance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private YearMonth period;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal openingBalance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal debitSum;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal creditSum;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal closingBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NormalBalance normalBalance;
}

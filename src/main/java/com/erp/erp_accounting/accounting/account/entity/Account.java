package com.erp.erp_accounting.accounting.account.entity;

import com.erp.erp_accounting.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Account parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Account> children = new ArrayList<>();

    @Column(nullable = false)
    private boolean leaf = true; // 전표 입력 가능 여부

    // 정상 잔액 방향
    public NormalBalance getNormalBalance() {
        return switch (category) {
            case ASSET, EXPENSE -> NormalBalance.DEBIT;
            case LIABILITY, EQUITY, REVENUE -> NormalBalance.CREDIT;
        };
    }

    @Builder
    public Account(String code, String name, AccountCategory category, Account parent) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.parent = parent;
        if (parent != null) {
            parent.leaf = false;
        }
    }
}

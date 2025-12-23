package com.erp.erp_accounting.accounting.account.entity;

import com.erp.erp_accounting.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private boolean leaf = true; // 전표 입력 가능 여부

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

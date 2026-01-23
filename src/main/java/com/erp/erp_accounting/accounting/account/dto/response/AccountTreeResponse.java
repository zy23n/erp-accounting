package com.erp.erp_accounting.accounting.account.dto.response;

import com.erp.erp_accounting.accounting.account.entity.Account;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class AccountTreeResponse {
    private Long id;
    private String code;
    private String name;
    private String category;
    private List<AccountTreeResponse> children;

    public static AccountTreeResponse fromEntity(Account account) {
        List<AccountTreeResponse> children = Optional.ofNullable(account.getChildren())
                .orElse(Collections.emptyList())
                .stream()
                .map(AccountTreeResponse::fromEntity)
                .toList();

        return AccountTreeResponse.builder()
                .id(account.getId())
                .code(account.getCode())
                .name(account.getName())
                .category(account.getCategory().getKoreanName())
                .children(children)
                .build();
    }
}

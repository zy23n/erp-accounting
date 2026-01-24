package com.erp.erp_accounting.accounting.account.dto.response;

import com.erp.erp_accounting.accounting.account.entity.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@Schema(description = "계정 트리 응답 DTO")
public class AccountTreeResponse {

    @Schema(description = "계정 ID", example = "1")
    private Long id;

    @Schema(description = "계정 코드", example = "1000")
    private String code;

    @Schema(description = "계정 이름", example = "현금")
    private String name;

    @Schema(description = "계정 카테고리", example = "자산")
    private String category;

    @Schema(description = "하위 계정 리스트")
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

package com.erp.erp_accounting.accounting.account.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccountTreeResponse {
    private Long id;
    private String code;
    private String name;
    private String category;
    private List<AccountTreeResponse> children;
}

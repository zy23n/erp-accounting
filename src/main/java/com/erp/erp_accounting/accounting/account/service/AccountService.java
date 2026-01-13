package com.erp.erp_accounting.accounting.account.service;

import com.erp.erp_accounting.accounting.account.dto.response.AccountTreeResponse;
import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.PayrollItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    // 전체 트리 조회
    public List<AccountTreeResponse> getAccountTree() {
        List<Account> roots = accountRepository.findRootAccounts();
        return roots.stream()
                .map(this::toResponse)
                .toList();
    }

    // leaf 계정만 조회
    public List<AccountTreeResponse> getLeafAccounts() {
        List<Account> leafAccounts = accountRepository.findLeafAccounts();
        return leafAccounts.stream()
                .map(account -> AccountTreeResponse.builder()
                        .id(account.getId())
                        .code(account.getCode())
                        .name(account.getName())
                        .category(account.getCategory().getKoreanName())
                        .children(List.of())
                        .build())
                .toList();
    }

    // DTO 변환, 재귀 처리
    private AccountTreeResponse toResponse(Account account) {
        List<AccountTreeResponse> children = account.getChildren().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return AccountTreeResponse.builder()
                .id(account.getId())
                .code(account.getCode())
                .name(account.getName())
                .category(account.getCategory().getKoreanName())
                .children(children)
                .build();
    }

    // 급여 항목 → 계정 ID 반환
    public Long getAccountId(PayrollItem item) {
        String code;
        switch (item) {
            case BASE_SALARY -> code = "1010";
            case BONUS -> code = "1020";
            case DEDUCTION -> code = "2010";
            case CASH -> code = "1011";
            default -> throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        return accountRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND))
                .getId();
    }
}

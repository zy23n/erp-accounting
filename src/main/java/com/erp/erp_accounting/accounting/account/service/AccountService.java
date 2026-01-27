package com.erp.erp_accounting.accounting.account.service;

import com.erp.erp_accounting.accounting.account.dto.response.AccountTreeResponse;
import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.PayrollItem;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {

    private final AccountRepository accountRepository;

    // 전체 트리 조회
    @Cacheable("account:tree")
    public List<AccountTreeResponse> getAccountTree() {
        List<Account> roots = accountRepository.findRootAccounts();
        return roots.stream()
                .map(AccountTreeResponse::fromEntity)
                .toList();
    }

    // leaf 계정만 조회
    @Cacheable("account:leaf")
    public List<AccountTreeResponse> getLeafAccounts() {
        List<Account> leafAccounts = accountRepository.findLeafAccounts();
        return leafAccounts.stream()
                .map(AccountTreeResponse::fromEntity)
                .toList();
    }

    // 급여 항목 → 계정 ID 반환
    @Cacheable(value = "account:payrollItem", key = "#item", condition = "#item != null")
    public Long getAccountIdByPayrollItem(PayrollItem item) {
        if (item == null) throw new BusinessException(ErrorCode.INVALID_REQUEST, "급여 항목 미지정");
        return accountRepository.findByCode(item.getAccountCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 항목-계정 매핑 정보 미존재 (item=%s, code=%s)", item.name(), item.getAccountCode())))
                .getId();
    }

    // 지급 수단 → 계정 ID 반환
    @Cacheable(value = "account:paymentMethod", key = "#method", condition = "#method != null")
    public Long getAccountIdByPaymentMethod(PaymentMethod method) {
        if (method == null) throw new BusinessException(ErrorCode.INVALID_REQUEST, "지급수단 미지정");
        return accountRepository.findByCode(method.getAccountCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("지급 수단-계정 매핑 정보 미존재 (method=%s, code=%s)", method.name(), method.getAccountCode())))
                .getId();
    }
}

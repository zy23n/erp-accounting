package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmListResponse;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollConfirmQueryService {

    private final PayrollConfirmRepository payrollConfirmRepository;

    // 상세 조회
    public PayrollConfirmResponse getPayrollConfirm(Long confirmId) {
        PayrollConfirm confirm = payrollConfirmRepository.findDetailById(confirmId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 확정 미존재 (confirmId=%d)", confirmId)));

        return PayrollConfirmResponse.fromEntity(confirm);
    }

    // 목록 조회
    public Page<PayrollConfirmListResponse> getPayrollConfirmList(Pageable pageable) {
        return payrollConfirmRepository.findAll(pageable)
                .map(PayrollConfirmListResponse::fromEntity);
    }
}

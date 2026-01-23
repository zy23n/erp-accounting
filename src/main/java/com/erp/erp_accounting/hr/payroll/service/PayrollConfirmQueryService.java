package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollConfirmListResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollConfirmQueryService {

    private final PayrollConfirmRepository payrollConfirmRepository;

    // 단건 조회
    public PayrollConfirmResponse getPayrollConfirm(Long payrollConfirmId) {
        PayrollConfirm confirm = payrollConfirmRepository.findDetailById(payrollConfirmId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 확정 미존재 (confirmId=%d)", payrollConfirmId)));

        List<PayrollResponse> payrolls = confirm.getPayrolls().stream()
                .map(this::toPayrollResponse)
                .toList();

        return new PayrollConfirmResponse(
                confirm.getId(),
                confirm.getPayMonth(),
                confirm.getStatus(),
                userId(confirm.getConfirmedBy()),
                username(confirm.getConfirmedBy()),
                confirm.getConfirmedAt(),
                userId(confirm.getCanceledBy()),
                username(confirm.getCanceledBy()),
                confirm.getCanceledAt(),
                payrolls
        );
    }

    // 목록 조회
    public List<PayrollConfirmListResponse> getPayrollConfirmList() {
        return payrollConfirmRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private PayrollResponse toPayrollResponse(Payroll payroll) {
        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getEmployee().getName(),
                payroll.getPayMonth(),
                payroll.getBaseSalary(),
                payroll.getAllowanceAmount(),
                payroll.getDeductionAmount(),
                payroll.getNetAmount(),
                payroll.getStatus(),
                payroll.getPaymentMethod()
        );
    }

    private PayrollConfirmListResponse toResponse(PayrollConfirm confirm) {
        return new PayrollConfirmListResponse(
                confirm.getId(),
                confirm.getPayMonth(),
                confirm.getStatus(),
                confirm.getConfirmedBy() != null ? confirm.getConfirmedBy().getId() : null,
                confirm.getConfirmedBy() != null ? confirm.getConfirmedBy().getUsername() : null,
                confirm.getConfirmedAt()
        );
    }

    private Long userId(User user) {
        return user != null ? user.getId() : null;
    }

    private String username(User user) {
        return user != null ? user.getUsername() : null;
    }
}

package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollQueryService {

    private final PayrollRepository payrollRepository;

    // 급여 단건 조회
    public PayrollResponse getPayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("급여 없음"));

        return toResponse(payroll);
    }

    // 급여 목록 조회 (전체)
    public List<PayrollResponse> getPayrollList() {
        return payrollRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private PayrollResponse toResponse(Payroll payroll) {
        return new PayrollResponse(
                payroll.getId(),
                payroll.getEmployee().getId(),
                payroll.getEmployee().getName(),
                payroll.getPayMonth(),
                payroll.getBaseSalary(),
                payroll.getAllowanceAmount(),
                payroll.getDeductionAmount(),
                payroll.getNetAmount(),
                payroll.getStatus()
        );
    }
}


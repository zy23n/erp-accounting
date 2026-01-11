package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import com.erp.erp_accounting.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollQueryService {

    private final PayrollRepository payrollRepository;

    // 급여 상세 조회
    public PayrollResponse getPayroll(Long payrollId, UserPrincipal principal) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("급여 없음"));

        // HR / ADMIN은 전체 조회 가능
        if (principal.hasRole(UserRole.HR) || principal.hasRole(UserRole.ADMIN)) {
            return toResponse(payroll);
        }

        // USER는 본인 데이터만 조회 가능
        if (!payroll.getEmployee().getId().equals(principal.getId())) {
            throw new AccessDeniedException("본인 급여만 조회 가능");
        }

        return toResponse(payroll);
    }

    // 급여 목록 조회 (전체)
    public List<PayrollResponse> getPayrollList(UserPrincipal principal) {

        List<Payroll> payrolls = payrollRepository.findAll();

        // USER는 본인 데이터만 필터링
        if (!principal.hasRole(UserRole.HR) && !principal.hasRole(UserRole.ADMIN)) {
            payrolls = payrolls.stream()
                    .filter(p -> p.getEmployee().getId().equals(principal.getId()))
                    .toList();
        }

        return payrolls.stream()
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


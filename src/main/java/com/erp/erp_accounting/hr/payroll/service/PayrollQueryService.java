package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.dto.query.PayrollSearchCondition;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollQueryRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.security.principal.UserPrincipal;
import com.erp.erp_accounting.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollQueryService {

    private final PayrollRepository payrollRepository;
    private final PayrollQueryRepository payrollQueryRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "payMonth", "baseSalary", "netAmount", "status", "createdAt"
    );

    // 급여 상세 조회
    public PayrollResponse getPayroll(Long payrollId, UserPrincipal principal) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 미존재 (payrollId=%d)", payrollId)));

        // HR / ADMIN은 전체 조회 가능
        if (principal.hasRole(UserRole.HR) || principal.hasRole(UserRole.ADMIN)) {
            return toResponse(payroll);
        }

        // USER는 본인 데이터만 조회 가능
        if (!payroll.getEmployee().getId().equals(principal.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인 급여만 조회 가능");
        }

        return toResponse(payroll);
    }

    // 급여 목록 조회 (전체)
    public Page<PayrollListResponse> searchPayrolls(UserPrincipal principal, PayrollSearchCondition condition, Pageable pageable) {

        validateCondition(condition);
        Pageable safePageable = validateSortFields(pageable);

        // HR / ADMIN → 전체 조회, USER → 본인 급여만
        if (principal.hasRole(UserRole.HR) || principal.hasRole(UserRole.ADMIN)) {
            return payrollQueryRepository.search(condition, safePageable);
        }
        return payrollQueryRepository.searchByEmployee(principal.getId(), condition, safePageable);
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

    private void validateCondition(PayrollSearchCondition cond) {
        if (cond.getPayMonth() != null && (cond.getStartPayMonth() != null || cond.getEndPayMonth() != null)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "조회 조건 충돌 (payMonth, startPayMonth/endPayMonth)");
        }

        if (cond.getStartPayMonth() != null && cond.getEndPayMonth() != null &&
                cond.getStartPayMonth().isAfter(cond.getEndPayMonth())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "급여월 범위 오류 (startPayMonth > endPayMonth)");
        }

        if (cond.getStartHireDate() != null && cond.getEndHireDate() != null &&
                cond.getStartHireDate().isAfter(cond.getEndHireDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "입사일 범위 오류 (startHireDate > endHireDate)");
        }
    }

    private Pageable validateSortFields(Pageable pageable) {
        List<Sort.Order> safeOrders = pageable.getSort().stream()
                .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                .toList();
        Sort sort = safeOrders.isEmpty()
                ? Sort.by(Sort.Direction.DESC, "payMonth")
                : Sort.by(safeOrders);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}


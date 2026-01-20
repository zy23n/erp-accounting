package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.dto.request.PayrollCreateRequest;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public Long createPayroll(PayrollCreateRequest request) {

        log.info("[PAYROLL] action=CREATE_REQUEST, employeeId={}, payMonth={}", request.getEmployeeId(), request.getPayMonth());

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("직원 미존재 (employeeId=%d)", request.getEmployeeId())));

        boolean exists = payrollRepository.existsByEmployee_IdAndPayMonth(request.getEmployeeId(), request.getPayMonth());

        if (exists) {
            log.warn("[PAYROLL] action=CREATE_DUPLICATE, employeeId={}, payMonth={}", request.getEmployeeId(), request.getPayMonth());
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE,
                    String.format("급여 중복 존재 (payMonth=%s, employeeId=%d)", request.getPayMonth(), request.getEmployeeId()));
        }

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payMonth(request.getPayMonth())
                .baseSalary(request.getBaseSalary())
                .allowanceAmount(request.getAllowanceAmount())
                .deductionAmount(request.getDeductionAmount())
                .build();

        payroll.calculateNetAmount();
        payrollRepository.save(payroll);

        log.info("[PAYROLL] action=CREATE_COMPLETE, payrollId={}, employeeId={}, payMonth={}",
                payroll.getId(), request.getEmployeeId(), request.getPayMonth());

        return payroll.getId();
    }
}


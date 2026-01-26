package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.hr.payroll.service.command.CreatePayrollCommand;
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

    public Long createPayroll(CreatePayrollCommand command) {

        log.info("[PAYROLL] action=CREATE_REQUEST, employeeId={}, payMonth={}", command.getEmployeeId(), command.getPayMonth());

        Employee employee = employeeRepository.findById(command.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("직원 미존재 (employeeId=%d)", command.getEmployeeId())));

        boolean exists = payrollRepository.existsByEmployee_IdAndPayMonth(command.getEmployeeId(), command.getPayMonth());

        if (exists) {
            log.warn("[PAYROLL] action=CREATE_DUPLICATE, employeeId={}, payMonth={}", command.getEmployeeId(), command.getPayMonth());
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE,
                    String.format("급여 중복 존재 (payMonth=%s, employeeId=%d)", command.getPayMonth(), command.getEmployeeId()));
        }

        PaymentMethod method = command.getPaymentMethod() != null ? command.getPaymentMethod() : employee.getDefaultPaymentMethod();

        Payroll payroll = Payroll.builder()
                .employee(employee)
                .payMonth(command.getPayMonth())
                .baseSalary(command.getBaseSalary())
                .allowanceAmount(command.getAllowanceAmount())
                .deductionAmount(command.getDeductionAmount())
                .paymentMethod(method)
                .build();

        payroll.calculateNetAmount();
        payrollRepository.save(payroll);

        log.info("[PAYROLL] action=CREATE_COMPLETE, payrollId={}, employeeId={}, payMonth={}",
                payroll.getId(), command.getEmployeeId(), command.getPayMonth());

        return payroll.getId();
    }
}


package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.dto.request.PayrollCreateRequest;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public Long createPayroll(PayrollCreateRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("직원 없음"));

        boolean exists = payrollRepository
                .existsByEmployee_IdAndPayMonth(
                        request.getEmployeeId(),
                        request.getPayMonth()
                );

        if (exists) {
            throw new IllegalStateException("이미 해당 월의 급여가 존재");
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
        return payroll.getId();
    }
}


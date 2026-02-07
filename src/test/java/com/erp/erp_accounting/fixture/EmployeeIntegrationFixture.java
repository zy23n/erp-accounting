package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeIntegrationFixture {

    public static Employee saveEmployee(EmployeeRepository employeeRepository, UserRepository userRepository) {

        User user = userRepository.findByUsername("normal")
                .orElseGet(() -> userRepository.save(UserFixture.normalUser()));

        Employee employee = Employee.builder()
                .empNo("EMP-" + UUID.randomUUID())
                .name("김개발")
                .hireDate(LocalDate.of(2024, 1, 1))
                .user(user)
                .position("사원")
                .department("개발팀")
                .defaultPaymentMethod(PaymentMethod.BANK_TRANSFER)
                .build();

        return employeeRepository.save(employee);
    }
}

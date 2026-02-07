package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollCommandFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.employee.repository.EmployeeRepository;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.hr.payroll.service.command.CreatePayrollCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("급여 서비스 테스트")
class PayrollServiceTest {

    @InjectMocks
    private PayrollService payrollService;

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = EmployeeFixture.employeeWithId(1L);
    }

    @Test
    @DisplayName("급여 생성 성공")
    void createPayroll_success() {
        // given
        CreatePayrollCommand command = PayrollCommandFixture.valid(employee.getId());

        given(employeeRepository.findById(employee.getId())).willReturn(Optional.of(employee));
        given(payrollRepository.existsByEmployee_IdAndPayMonth(any(), any())).willReturn(false);
        given(payrollRepository.save(any())).willReturn(PayrollFixture.savedCreatedPayroll(1L, employee));

        // when
        Long payrollId = payrollService.createPayroll(command);

        // then
        assertThat(payrollId).isEqualTo(1L);
        verify(payrollRepository).save(any(Payroll.class));
        verify(payrollRepository).existsByEmployee_IdAndPayMonth(any(), any());
    }
}
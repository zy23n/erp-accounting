package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.fixture.EmployeeFixture;
import com.erp.erp_accounting.fixture.PayrollFixture;
import com.erp.erp_accounting.fixture.SearchPayrollCommandFixture;
import com.erp.erp_accounting.fixture.UserFixture;
import com.erp.erp_accounting.hr.employee.entity.Employee;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.repository.PayrollQueryRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.hr.payroll.service.command.SearchPayrollCommand;
import com.erp.erp_accounting.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("급여 조회 서비스 테스트")
class PayrollQueryServiceTest {

    @InjectMocks
    private PayrollQueryService payrollQueryService;

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PayrollQueryRepository payrollQueryRepository;

    private User hrUser;
    private User normalUser;
    private Payroll payroll;

    @BeforeEach
    void setUp() {
        hrUser = UserFixture.hrUser();
        ReflectionTestUtils.setField(hrUser, "id", 1L);

        normalUser = UserFixture.normalUser();
        ReflectionTestUtils.setField(normalUser, "id", 2L);

        Employee employee = EmployeeFixture.employee(2L);
        payroll = PayrollFixture.savedCalculatedPayroll(100L, employee);
    }

    @Test
    @DisplayName("USER는 본인 급여만 조회 가능")
    void getPayroll_success_when_user_own_data() {
        // given
        given(payrollRepository.findById(100L)).willReturn(Optional.of(payroll));

        // when
        PayrollResponse response = payrollQueryService.getPayroll(100L, normalUser);

        // then
        assertThat(response.getPayrollId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("USER가 타인 급여 조회 시 예외")
    void getPayroll_fail_when_user_not_owner() {
        // given
        User otherUser = UserFixture.normalUser();
        ReflectionTestUtils.setField(otherUser, "id", 99L);

        given(payrollRepository.findById(100L)).willReturn(Optional.of(payroll));

        // when & then
        assertThatThrownBy(() -> payrollQueryService.getPayroll(100L, otherUser))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> {
                    BusinessException be = (BusinessException) e;
                    assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
                });
    }

    @Test
    @DisplayName("HR은 전체 급여 목록 조회")
    void searchPayrolls_success_when_hr() {
        // given
        SearchPayrollCommand command = SearchPayrollCommandFixture.valid();
        Pageable pageable = PageRequest.of(0, 10);

        given(payrollQueryRepository.search(any(), any())).willReturn(Page.empty());

        // when
        Page<PayrollListResponse> result = payrollQueryService.searchPayrolls(hrUser, command, pageable);

        // then
        assertThat(result).isEmpty();
        verify(payrollQueryRepository).search(any(), any());
    }
}
package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.hr.payroll.dto.query.PayrollSearchCondition;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PayrollQueryRepository {

    Page<PayrollListResponse> search(PayrollSearchCondition cond, Pageable pageable);

    Page<PayrollListResponse> searchByEmployee(Long employeeId, PayrollSearchCondition cond, Pageable pageable);
}

package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.hr.payroll.dto.response.PayrollListResponse;
import com.erp.erp_accounting.hr.payroll.service.command.SearchPayrollCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PayrollQueryRepository {

    Page<PayrollListResponse> search(SearchPayrollCommand command, Pageable pageable);

    Page<PayrollListResponse> searchByEmployee(Long employeeId, SearchPayrollCommand command, Pageable pageable);
}

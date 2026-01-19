package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployee_IdAndPayMonth(Long employeeId, YearMonth payMonth);

    List<Payroll> findByPayMonthAndStatus(YearMonth payMonth, PayrollStatus status);
}

package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    boolean existsByEmployee_IdAndPayMonth(Long employeeId, YearMonth payMonth);

    List<Payroll> findByPayMonthAndStatus(YearMonth payMonth, PayrollStatus status);

    // 모든 급여 조회
    @Query(
        value = """
            select p
            from Payroll p
            join fetch p.employee e
        """,
        countQuery = "select count(p) from Payroll p"
    )
    Page<Payroll> findPayrollList(Pageable pageable);

    // 특정 직원 본인 급여 조회
    @Query(
        value = """
            select p
            from Payroll p
            join fetch p.employee
            where p.employee.id = :employeeId
        """,
        countQuery = "select count(p) from Payroll p where p.employee.id = :employeeId"
    )
    Page<Payroll> findAllByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);
}

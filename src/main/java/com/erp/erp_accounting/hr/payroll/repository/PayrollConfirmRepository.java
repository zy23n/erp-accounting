package com.erp.erp_accounting.hr.payroll.repository;

import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.Optional;

@Repository
public interface PayrollConfirmRepository extends JpaRepository<PayrollConfirm, Long> {

    boolean existsByPayMonth(YearMonth payMonth);

    @Query("""
        select distinct pc
        from PayrollConfirm pc
        left join fetch pc.payrolls p
        left join fetch p.employee
        where pc.id = :id
    """)
    Optional<PayrollConfirm> findDetailById(@Param("id") Long id);
}

package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // 단건 조회
    @EntityGraph(attributePaths = {"lines", "lines.account", "createdBy"})
    Optional<Voucher> findWithLinesById(Long id);

    // 목록 조회
    @Query("""
            select new com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse(
                v.id,
                v.voucherNo,
                v.voucherDate,
                v.status,
                v.description,
                u.id,
                u.username,
                sum(case when l.type = :debit then l.amount else 0 end),
                sum(case when l.type = :credit then l.amount else 0 end),
                v.createdAt,
                v.voucherType,
                v.sourceType
            )
            from Voucher v
            join v.createdBy u
            left join v.lines l
            group by v.id, v.voucherNo, v.voucherDate, v.status, v.description, u.id, u.username, v.createdAt,
                     v.voucherType, v.sourceType
            order by v.voucherDate desc
            """)
    List<VoucherListResponse> findVoucherList(
        @Param("debit") LineType debit,
        @Param("credit") LineType credit
    );
}

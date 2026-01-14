package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // 단건 조회
    @EntityGraph(attributePaths = {"lines", "lines.account", "createdBy"})
    Optional<Voucher> findWithLinesById(Long id);

    // 목록 조회
    @Query(
        value = """
            select new com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse(
                v.id,
                v.voucherNo,
                v.voucherDate,
                v.status,
                v.description,
                u.id,
                u.username,
                sum(case when l.type = 'DEBIT' then l.amount else 0 end),
                sum(case when l.type = 'CREDIT' then l.amount else 0 end),
                v.createdAt,
                v.voucherType,
                v.sourceType
            )
            from Voucher v
            join v.createdBy u
            left join v.lines l
            group by v.id, v.voucherNo, v.voucherDate, v.status, v.description, u.id, u.username, v.createdAt,
                     v.voucherType, v.sourceType
        """,
        countQuery = "select count(distinct v) from Voucher v"
    )
    Page<VoucherListResponse> findVoucherList(Pageable pageable);

    List<Voucher> findBySourceTypeAndSourceId(SourceType sourceType, Long sourceId);

    boolean existsBySourceTypeAndSourceIdAndStatusNot(SourceType sourceType, Long sourceId, VoucherStatus status);
}

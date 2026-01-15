package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    // 단건 조회
    @EntityGraph(attributePaths = {"lines", "lines.account", "createdBy"})
    Optional<Voucher> findWithLinesById(Long id);

    List<Voucher> findBySourceTypeAndSourceId(SourceType sourceType, Long sourceId);

    boolean existsBySourceTypeAndSourceIdAndStatusNot(SourceType sourceType, Long sourceId, VoucherStatus status);
}

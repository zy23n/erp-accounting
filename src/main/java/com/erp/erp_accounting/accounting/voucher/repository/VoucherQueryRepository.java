package com.erp.erp_accounting.accounting.voucher.repository;

import com.erp.erp_accounting.accounting.voucher.dto.query.VoucherSearchCondition;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherQueryRepository {

    Page<VoucherListResponse> search(VoucherSearchCondition condition, Pageable pageable);

    Page<VoucherListResponse> searchByCreator(Long creatorId, VoucherSearchCondition condition, Pageable pageable);
}
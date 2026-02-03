package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.voucher.dto.query.VoucherSearchCondition;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherQueryRepository;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.dto.response.PayrollResponse;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;
    private final VoucherQueryRepository voucherQueryRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "voucherDate", "voucherNo", "createdAt", "voucherType", "sourceType"
    );

    // 전표 상세 조회
    public VoucherResponse getVoucher(Long voucherId, User user) {
        Voucher voucher = voucherRepository.findWithLinesById(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("전표 미존재 (voucherId=%d)", voucherId)));

        // ACCOUNTING / ADMIN은 전체 조회 가능
        if (user.hasRole(UserRole.ACCOUNTING) || user.hasRole(UserRole.ADMIN)) {
            return VoucherResponse.fromEntity(voucher);
        }

        // USER는 본인 데이터만 조회 가능
        if (!voucher.getCreatedBy().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "본인 전표만 조회 가능");
        }

        return VoucherResponse.fromEntity(voucher);
    }

    // 전표 목록 조회 (QueryDSL)
    public Page<VoucherListResponse> searchVouchers(User user, VoucherSearchCondition condition, Pageable pageable) {
        validateCondition(condition);
        Pageable safePageable = validateSortFields(pageable);

        // ACCOUNTING / ADMIN → 전체 조회, USER → 본인 전표만
        if (user.hasRole(UserRole.ACCOUNTING) || user.hasRole(UserRole.ADMIN)) {
            return voucherQueryRepository.search(condition, safePageable);
        }
        return voucherQueryRepository.searchByCreator(user.getId(), condition, safePageable);
    }

    private void validateCondition(VoucherSearchCondition cond) {
        if (cond.getVoucherDate() != null && (cond.getStartDate() != null || cond.getEndDate() != null)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "조회 조건 충돌 (voucherDate, startDate/endDate)");
        }
        if (cond.getStartDate() != null && cond.getEndDate() != null && cond.getStartDate().isAfter(cond.getEndDate())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "조회 기간 범위 오류 (startDate > endDate)");
        }
    }

    private Pageable validateSortFields(Pageable pageable) {
        List<Sort.Order> safeOrders = pageable.getSort().stream()
                .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                .toList();
        Sort sort = safeOrders.isEmpty()
                ? Sort.by(Sort.Order.desc("voucherDate"), Sort.Order.desc("voucherNo"))
                : Sort.by(safeOrders);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}

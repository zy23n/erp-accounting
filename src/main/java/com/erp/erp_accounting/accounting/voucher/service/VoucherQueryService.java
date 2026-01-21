package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.voucher.dto.query.VoucherSearchCondition;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherLineResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherQueryRepository;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
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

    // 단건 조회
    public VoucherResponse getVoucher(Long voucherId) {
        Voucher voucher = voucherRepository.findWithLinesById(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("전표 미존재 (voucherId=%d)", voucherId)));

        return toResponse(voucher);
    }

    // 목록 조회 (QueryDSL)
    public Page<VoucherListResponse> searchVouchers(VoucherSearchCondition condition, Pageable pageable) {
        validateCondition(condition);
        Pageable safePageable = validateSortFields(pageable);
        return voucherQueryRepository.search(condition, safePageable);
    }

    // DTO 변환
    private VoucherResponse toResponse(Voucher voucher) {

        List<VoucherLineResponse> lines = voucher.getLines().stream()
                .map(line -> new VoucherLineResponse(
                        line.getId(),
                        line.getAccount().getId(),
                        line.getAccount().getName(),
                        line.getType().name(),
                        line.getAmount()
                ))
                .toList();

        return new VoucherResponse(
                voucher.getId(),
                voucher.getVoucherNo(),
                voucher.getVoucherDate(),
                voucher.getDescription(),
                voucher.getStatus(),
                userId(voucher.getCreatedBy()),
                username(voucher.getCreatedBy()),
                lines,
                voucher.getVoucherType(),
                voucher.getSourceType(),
                voucher.getSourceId(),
                userId(voucher.getApprovedBy()),
                username(voucher.getApprovedBy()),
                voucher.getApprovedAt(),
                userId(voucher.getCanceledBy()),
                username(voucher.getCanceledBy()),
                voucher.getCanceledAt()
        );
    }

    private Long userId(User user) {
        return user != null ? user.getId() : null;
    }

    private String username(User user) {
        return user != null ? user.getUsername() : null;
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

package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherLineResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherListResponse;
import com.erp.erp_accounting.accounting.voucher.dto.response.VoucherResponse;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;

    // 단건 조회
    public VoucherResponse getVoucher(Long voucherId) {

        Voucher voucher = voucherRepository.findWithLinesById(voucherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        return toResponse(voucher);
    }

    // 목록 조회
    public List<VoucherListResponse> getVoucherList() {
        return voucherRepository.findVoucherList(LineType.DEBIT, LineType.CREDIT);
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
}

package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public Voucher createVoucher(VoucherCreateRequest request, Long userId) {
        // 사용자 조회
        User user = findUser(userId);

        // 전표 라인 검증
        validateLines(request);

        // voucherType, sourceType 자동 세팅
        VoucherType voucherType = request.getSourceId() != null ? VoucherType.PAYROLL : VoucherType.GENERAL;
        SourceType sourceType = request.getSourceId() != null ? SourceType.PAYROLL : SourceType.NONE;

        // 전표 생성
        Voucher voucher = Voucher.builder()
                .voucherNo(generateVoucherNo())
                .voucherDate(request.getVoucherDate())
                .description(request.getDescription())
                .status(VoucherStatus.DRAFT)
                .createdBy(user)
                .voucherType(voucherType)
                .sourceType(sourceType)
                .sourceId(request.getSourceId())
                .build();

        // 전표 라인 생성
        for (VoucherLineRequest lineRequest : request.getLines()) {
            VoucherLine voucherLine = createVoucherLine(lineRequest);
            voucher.addLine(voucherLine);
        }

        // 저장
        voucherRepository.save(voucher);

        return voucher;
    }

    public Long createAndAutoApprove(VoucherCreateRequest request, Long userId) {
        Voucher voucher = createVoucher(request, userId);
        voucher.approve(voucher.getCreatedBy());
        return voucher.getId();
    }

    public void cancelAutoVouchers(SourceType sourceType, Long sourceId, User user) {
        List<Voucher> vouchers =
                voucherRepository.findBySourceTypeAndSourceId(sourceType, sourceId);

        for (Voucher voucher : vouchers) {
            if (voucher.isCancelable()) {
                voucher.cancel(user);
            }
        }
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private void validateLines(VoucherCreateRequest request) {
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new IllegalArgumentException("전표 라인은 최소 1개 이상 필요합니다.");
        }

        // 대차검증
        BigDecimal debitSum = request.getLines().stream()
                .filter(line -> line.getType() == LineType.DEBIT)
                .map(VoucherLineRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal creditSum = request.getLines().stream()
                .filter(line -> line.getType() == LineType.CREDIT)
                .map(VoucherLineRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (debitSum.compareTo(creditSum) != 0) {
            throw new IllegalArgumentException("차변과 대변의 합계 불일치");
        }
    }

    private VoucherLine createVoucherLine(VoucherLineRequest lineRequest) {
        Account account = findAccount(lineRequest.getAccountId());

        if (!account.isLeaf()) {
            throw new IllegalArgumentException("하위 계정이 있는 계정에는 전표 입력 불가");
        }

        return VoucherLine.builder()
                .account(account)
                .type(lineRequest.getType())
                .amount(lineRequest.getAmount())
                .build();
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("계정과목 없음"));
    }

    private String generateVoucherNo() {
        return "V" + System.currentTimeMillis();
    }

    @Transactional(readOnly = true)
    public boolean existsBySource(SourceType sourceType, Long sourceId) {
        return voucherRepository.existsBySourceTypeAndSourceId(sourceType, sourceId);
    }
}

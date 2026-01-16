package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final AccountRepository accountRepository;
    private final AccountingPeriodService accountingPeriodService;

    public Voucher createVoucher(VoucherCreateRequest request, User user) {

        // 회계기간 마감 여부 검증
        assertVoucherPeriodOpen(request.getVoucherDate());

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

    public Long createAndAutoApprove(VoucherCreateRequest request, User user) {
        assertVoucherPeriodOpen(request.getVoucherDate());
        Voucher voucher = createVoucher(request, user);
        voucher.approve(voucher.getCreatedBy());
        return voucher.getId();
    }

    public void cancelAutoVouchers(SourceType sourceType, Long sourceId, User canceler) {
        List<Voucher> vouchers = voucherRepository.findBySourceTypeAndSourceId(sourceType, sourceId);

        assertVoucherPeriodOpen(vouchers.get(0).getVoucherDate());

        for (Voucher voucher : vouchers) {
            if (voucher.isCancelable()) {
                voucher.cancel(canceler);
            }
        }
    }

    private void assertVoucherPeriodOpen(LocalDate voucherDate) {
        accountingPeriodService.assertPeriodOpen(YearMonth.from(voucherDate));
    }

    private void validateLines(VoucherCreateRequest request) {
        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        for (VoucherLineRequest line : request.getLines()) {
            if (line.getAmount() == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "금액은 필수입니다.");
            }

            if (line.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "전표 라인 금액은 0보다 커야 합니다.");
            }

            if (line.getType() == null) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, "차변/대변 타입은 필수입니다.");
            }
        }

        boolean hasDebit = request.getLines().stream().anyMatch(l -> l.getType() == LineType.DEBIT);
        boolean hasCredit = request.getLines().stream().anyMatch(l -> l.getType() == LineType.CREDIT);

        if (!hasDebit || !hasCredit) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "차변과 대변은 각각 최소 1줄 이상 필요합니다.");
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
            throw new BusinessException(ErrorCode.IMBALANCE_AMOUNT);
        }
    }

    private VoucherLine createVoucherLine(VoucherLineRequest lineRequest) {
        Account account = findAccount(lineRequest.getAccountId());

        if (!account.isLeaf()) throw new BusinessException(ErrorCode.INVALID_STATE);

        return VoucherLine.builder()
                .account(account)
                .type(lineRequest.getType())
                .amount(lineRequest.getAmount())
                .build();
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    private String generateVoucherNo() {
        return "V" + System.currentTimeMillis();
    }
}

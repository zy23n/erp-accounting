package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherCreateRequest;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.LineType;
import com.erp.erp_accounting.accounting.voucher.entity.Voucher;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherLine;
import com.erp.erp_accounting.accounting.voucher.entity.VoucherStatus;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public Long createVoucher(VoucherCreateRequest request, Long userId) {

        // 사용자 조회
        User user = findUser(userId);

        // 전표 라인 검증
        validateLines(request);

        // 전표 생성
        Voucher voucher = Voucher.builder()
                .voucherNo(generateVoucherNo())
                .voucherDate(request.getVoucherDate())
                .description(request.getDescription())
                .status(VoucherStatus.DRAFT)
                .createdBy(user)
                .build();

        // 전표 라인 생성
        for (VoucherLineRequest lineRequest : request.getLines()) {
            VoucherLine voucherLine = createVoucherLine(lineRequest);
            voucher.addLine(voucherLine);
        }

        // 저장
        voucherRepository.save(voucher);
        return voucher.getId();
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
}

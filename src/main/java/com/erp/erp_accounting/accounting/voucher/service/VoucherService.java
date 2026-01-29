package com.erp.erp_accounting.accounting.voucher.service;

import com.erp.erp_accounting.accounting.account.entity.Account;
import com.erp.erp_accounting.accounting.account.repository.AccountRepository;
import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.dto.request.VoucherLineRequest;
import com.erp.erp_accounting.accounting.voucher.entity.*;
import com.erp.erp_accounting.accounting.voucher.repository.VoucherRepository;
import com.erp.erp_accounting.accounting.voucher.service.command.CreateVoucherCommand;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VoucherService {

    private final VoucherRepository voucherRepository;
    private final AccountRepository accountRepository;
    private final AccountingPeriodService accountingPeriodService;
    private final VoucherValidator validator;

    public Voucher createVoucher(CreateVoucherCommand command, User user) {

        log.info("[VOUCHER] action=CREATE_REQUEST, sourceType={}, sourceId={}, userId={}",
                command.getSourceType(), command.getSourceId(), user.getId());

        // 회계기간 마감 여부 검증
        assertVoucherPeriodOpen(command.getVoucherDate());

        // 전표 라인 검증
        validator.validateForCreate(command.getLines());

        // 전표 생성
        Voucher voucher = Voucher.builder()
                .voucherNo(generateVoucherNo())
                .voucherDate(command.getVoucherDate())
                .description(command.getDescription())
                .status(VoucherStatus.DRAFT)
                .createdBy(user)
                .voucherType(command.getSourceType().getVoucherType())
                .sourceType(command.getSourceType())
                .sourceId(command.getSourceId())
                .build();

        // 전표 라인 생성
        for (VoucherLineRequest lineRequest : command.getLines()) {
            VoucherLine voucherLine = createVoucherLine(lineRequest);
            voucher.addLine(voucherLine);
        }

        // 저장
        voucherRepository.save(voucher);

        log.info("[VOUCHER] action=CREATE_COMPLETE, voucherId={}, voucherNo={}, sourceType={}, sourceId={}, userId={}",
                voucher.getId(), voucher.getVoucherNo(), voucher.getSourceType(), voucher.getSourceId(), user.getId());

        return voucher;
    }

    private void assertVoucherPeriodOpen(LocalDate voucherDate) {
        accountingPeriodService.assertPeriodOpen(YearMonth.from(voucherDate));
    }

    private VoucherLine createVoucherLine(VoucherLineRequest lineRequest) {
        Account account = findAccount(lineRequest.getAccountId());

        if (!account.isLeaf()) {
            throw new BusinessException(ErrorCode.INVALID_STATE, "말단 계정에만 전표 라인 추가 가능");
        }

        return VoucherLine.builder()
                .account(account)
                .type(lineRequest.getType())
                .amount(lineRequest.getAmount())
                .build();
    }

    private Account findAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("계정과목 미존재 (accountId=%d)", accountId)));
    }

    private String generateVoucherNo() {
        return "V" + System.currentTimeMillis();
    }
}

package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.service.AutoVoucherService;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PayrollConfirmService {

    private final PayrollConfirmRepository payrollConfirmRepository;
    private final PayrollRepository payrollRepository;
    private final AutoVoucherService autoVoucherService;
    private final VoucherService voucherService;
    private final AccountingPeriodService accountingPeriodService;

    // 급여 확정 생성
    public Long createConfirm(YearMonth payMonth) {

        log.info("[PAYROLL_CONFIRM] action=CREATE_REQUEST, payMonth={}", payMonth);

        assertCreateAllowed(payMonth);

        if (payrollConfirmRepository.existsByPayMonth(payMonth)) {
            log.info("[PAYROLL_CONFIRM] action=DUPLICATE_DETECTED, payMonth={}", payMonth);
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, String.format("급여 확정 중복 존재 (payMonth=%s)", payMonth));
        }

        getCalculatedPayrolls(payMonth); // 해당 월의 계산 완료된 급여 유무 검증

        PayrollConfirm confirm = PayrollConfirm.builder().payMonth(payMonth).build();
        payrollConfirmRepository.save(confirm);

        log.info("[PAYROLL_CONFIRM] action=CREATE_COMPLETE, payrollConfirmId={}, payMonth={}", confirm.getId(), confirm.getPayMonth());

        return confirm.getId();
    }

    // 급여 확정 처리 (최초 확정 + 재확정 공용)
    public void confirm(Long confirmId, User confirmer) {

        log.info("[PAYROLL_CONFIRM] action=PROCESS_REQUEST, payrollConfirmId={}, confirmerId={}",
                confirmId, confirmer.getId());

        PayrollConfirm confirm = findConfirm(confirmId);

        assertConfirmAllowed(confirm.getPayMonth());

        // 해당 월의 계산 완료된 급여 조회
        List<Payroll> payrolls = getCalculatedPayrolls(confirm.getPayMonth());

        // 기존 연관관계 정리 (재확정 대비)
        confirm.clearPayrolls();
        payrolls.forEach(confirm::addPayroll);

        // 급여 확정 + 자동분개
        confirm.confirm(confirmer);
        autoVoucherService.createFromPayrollConfirm(confirm);

        log.info("[PAYROLL_CONFIRM] action=PROCESS_COMPLETE, payrollConfirmId={}, confirmerId={}, payMonth={}",
                confirmId, confirmer.getId(), confirm.getPayMonth());
    }

    // 급여 확정 취소
    public void cancel(Long confirmId, User canceler) {

        log.info("[PAYROLL_CONFIRM] action=CANCEL_REQUEST, payrollConfirmId={}, cancelerId={}",
                confirmId, canceler.getId());

        PayrollConfirm confirm = findConfirm(confirmId);

        assertCancelAllowed(confirm.getPayMonth());

        // 자동분개 전표 취소
        voucherService.cancelAutoVouchers(SourceType.PAYROLL, confirm.getId(), canceler);

        // 급여 확정 취소
        confirm.cancel(canceler);

        log.info("[PAYROLL_CONFIRM] action=CANCEL_COMPLETE, payrollConfirmId={}, cancelerId={}, payMonth={}",
                confirmId, canceler.getId(), confirm.getPayMonth());
    }

    private PayrollConfirm findConfirm(Long confirmId) {
        return payrollConfirmRepository.findById(confirmId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 확정 미존재 (confirmId=%d)", confirmId)));
    }

    private void assertCreateAllowed(YearMonth payMonth) {
        accountingPeriodService.assertPeriodOpen(payMonth);
    }

    private void assertConfirmAllowed(YearMonth payMonth) {
        accountingPeriodService.assertPreviousPeriodClosed(payMonth);
        accountingPeriodService.assertPeriodOpen(payMonth);

        if (payMonth.isAfter(YearMonth.now())) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("미래 월 급여 확정 불가 (payMonth=%s)", payMonth));
        }
    }

    private void assertCancelAllowed(YearMonth payMonth) {
        accountingPeriodService.assertPeriodOpen(payMonth);
    }

    private List<Payroll> getCalculatedPayrolls(YearMonth payMonth) {
        List<Payroll> payrolls = payrollRepository.findByPayMonthAndStatus(payMonth, PayrollStatus.CALCULATED);

        if (payrolls.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_STATE,
                    String.format("CALCULATED 상태 급여 미존재 (payMonth=%s)", payMonth));
        }

        return payrolls;
    }
}

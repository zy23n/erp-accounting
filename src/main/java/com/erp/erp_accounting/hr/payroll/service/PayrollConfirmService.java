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

        log.info("급여 확정 생성 요청: payMonth={}", payMonth);

        assertPayrollPeriodOpen(payMonth);

        if (payrollConfirmRepository.existsByPayMonth(payMonth)) {
            log.warn("이미 존재하는 급여 확정: payMonth={}", payMonth);
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, String.format("급여 확정 중복 존재 (payMonth=%s)", payMonth));
        }

        getCalculatedPayrolls(payMonth); // 해당 월의 계산 완료된 급여 유무 검증

        PayrollConfirm confirm = PayrollConfirm.builder().payMonth(payMonth).build();
        payrollConfirmRepository.save(confirm);

        log.info("급여 확정 생성 완료: payrollConfirmId={}", confirm.getId());

        return confirm.getId();
    }

    // 급여 확정 처리 (최초 확정 + 재확정 공용)
    public void confirm(Long payrollConfirmId, User confirmer) {

        log.info("급여 확정 처리 요청: payrollConfirmId={}, user={}", payrollConfirmId, confirmer.getUsername());

        PayrollConfirm confirm = findConfirm(payrollConfirmId);

        accountingPeriodService.assertPreviousPeriodClosed(confirm.getPayMonth());
        assertPayrollPeriodOpen(confirm.getPayMonth());

        // 해당 월의 계산 완료된 급여 조회
        List<Payroll> payrolls = getCalculatedPayrolls(confirm.getPayMonth());

        // 기존 연관관계 정리 (재확정 대비)
        confirm.clearPayrolls();
        payrolls.forEach(confirm::addPayroll);

        // 급여 확정 + 자동분개
        confirm.confirm(confirmer);
        autoVoucherService.createFromPayrollConfirm(confirm);

        log.info("급여 확정 완료: payrollConfirmId={}", payrollConfirmId);
    }

    // 급여 확정 취소
    public void cancel(Long payrollConfirmId, User canceler) {

        log.info("급여 확정 취소 요청: payrollConfirmId={}, user={}", payrollConfirmId, canceler.getUsername());

        PayrollConfirm confirm = findConfirm(payrollConfirmId);

        assertPayrollPeriodOpen(confirm.getPayMonth());

        // 자동분개 전표 취소
        voucherService.cancelAutoVouchers(SourceType.PAYROLL, confirm.getId(), canceler);

        // 급여 확정 취소
        confirm.cancel(canceler);

        log.info("급여 확정 취소 요청: payrollConfirmId={}, user={}", payrollConfirmId, canceler.getUsername());
    }

    private PayrollConfirm findConfirm(Long payrollConfirmId) {
        return payrollConfirmRepository.findById(payrollConfirmId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("급여 확정 미존재 (confirmId=%d)", payrollConfirmId)));
    }

    private void assertPayrollPeriodOpen(YearMonth payMonth) {
        accountingPeriodService.assertPeriodOpen(payMonth);
    }

    private List<Payroll> getCalculatedPayrolls(YearMonth payMonth) {
        List<Payroll> payrolls =
                payrollRepository.findByPayMonthAndStatus(payMonth, PayrollStatus.CALCULATED);

        if (payrolls.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_STATE, String.format("CALCULATED 상태 급여 미존재 (payMonth=%s)", payMonth));
        }

        return payrolls;
    }
}

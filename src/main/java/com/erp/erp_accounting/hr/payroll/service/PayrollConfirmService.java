package com.erp.erp_accounting.hr.payroll.service;

import com.erp.erp_accounting.accounting.period.service.AccountingPeriodService;
import com.erp.erp_accounting.accounting.voucher.entity.SourceType;
import com.erp.erp_accounting.accounting.voucher.service.AutoVoucherService;
import com.erp.erp_accounting.accounting.voucher.service.VoucherService;
import com.erp.erp_accounting.hr.payroll.entity.Payroll;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirm;
import com.erp.erp_accounting.hr.payroll.entity.PayrollConfirmStatus;
import com.erp.erp_accounting.hr.payroll.entity.PayrollStatus;
import com.erp.erp_accounting.hr.payroll.repository.PayrollConfirmRepository;
import com.erp.erp_accounting.hr.payroll.repository.PayrollRepository;
import com.erp.erp_accounting.user.entity.User;
import com.erp.erp_accounting.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollConfirmService {

    private final PayrollConfirmRepository payrollConfirmRepository;
    private final UserRepository userRepository;
    private final PayrollRepository payrollRepository;
    private final AutoVoucherService autoVoucherService;
    private final VoucherService voucherService;
    private final AccountingPeriodService accountingPeriodService;

    // 급여 확정 생성
    public Long createConfirm(YearMonth payMonth) {

        assertPayrollPeriodOpen(payMonth);

        if (payrollConfirmRepository.existsByPayMonth(payMonth)) {
            throw new IllegalStateException("이미 해당 월의 급여 확정이 존재");
        }

        getCalculatedPayrolls(payMonth); // 해당 월의 계산 완료된 급여 유무 검증

        PayrollConfirm confirm = PayrollConfirm.builder()
                .payMonth(payMonth)
                .build();

        payrollConfirmRepository.save(confirm);
        return confirm.getId();
    }

    // 급여 확정 처리 (최초 확정 + 재확정 공용)
    public void confirm(Long payrollConfirmId, Long userId) {
        PayrollConfirm confirm = findConfirm(payrollConfirmId);

        assertPayrollPeriodOpen(confirm.getPayMonth());

        // 이미 확정된 상태면 재확정 불가
        if (confirm.getStatus() == PayrollConfirmStatus.CONFIRMED) {
            throw new IllegalStateException("이미 확정된 급여확정");
        }

        User user = findUser(userId);

        // 해당 월의 계산 완료된 급여 조회
        List<Payroll> payrolls = getCalculatedPayrolls(confirm.getPayMonth());

        // 기존 연관관계 정리 (재확정 대비)
        confirm.clearPayrolls();
        payrolls.forEach(confirm::addPayroll);

        // 급여 확정 + 자동분개
        confirm.confirm(user);
        autoVoucherService.createFromPayrollConfirm(confirm);
    }

    // 급여 확정 취소
    public void cancel(Long payrollConfirmId, Long userId) {
        PayrollConfirm confirm = findConfirm(payrollConfirmId);

        assertPayrollPeriodOpen(confirm.getPayMonth());

        User user = findUser(userId);

        // 자동분개 전표 취소
        voucherService.cancelAutoVouchers(SourceType.PAYROLL, confirm.getId(), user);

        // 급여 확정 취소
        confirm.cancel(user);
    }

    private PayrollConfirm findConfirm(Long id) {
        return payrollConfirmRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("급여확정 없음"));
    }

    private void assertPayrollPeriodOpen(YearMonth payMonth) {
        accountingPeriodService.assertPeriodOpen(payMonth);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
    }

    private List<Payroll> getCalculatedPayrolls(YearMonth payMonth) {
        List<Payroll> payrolls =
                payrollRepository.findByPayMonthAndStatus(payMonth, PayrollStatus.CALCULATED);

        if (payrolls.isEmpty()) {
            throw new IllegalStateException("확정 가능한 급여 없음");
        }
        return payrolls;
    }
}

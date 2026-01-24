package com.erp.erp_accounting.hr.payroll.service.command;

import com.erp.erp_accounting.common.exception.BusinessException;
import com.erp.erp_accounting.common.exception.ErrorCode;
import com.erp.erp_accounting.hr.payroll.entity.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@AllArgsConstructor
public class CreatePayrollCommand {
    private final Long employeeId;
    private final YearMonth payMonth;
    private final BigDecimal baseSalary;
    private final BigDecimal allowanceAmount;
    private final BigDecimal deductionAmount;
    private final PaymentMethod paymentMethod;

    public void validate() {
        if (payMonth.isAfter(YearMonth.now())) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST,
                    String.format("급여 월이 현재 월 초과 (payMonth=%s)", payMonth)
            );
        }
    }
}

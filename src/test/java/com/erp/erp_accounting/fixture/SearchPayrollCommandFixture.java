package com.erp.erp_accounting.fixture;

import com.erp.erp_accounting.hr.payroll.service.command.SearchPayrollCommand;

public class SearchPayrollCommandFixture {

    public static SearchPayrollCommand valid() {
        return SearchPayrollCommand.builder().build();
    }
}
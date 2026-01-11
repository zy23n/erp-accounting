package com.erp.erp_accounting.security.config;

public class ApiRoles {
    public static final String[] ACCOUNTING_APIS = {"/api/accounts/**", "/api/ledger/**", "/api/vouchers/**"};
    public static final String[] HR_APIS = {"/api/payrolls/**", "/api/payroll-confirms/**"};
}

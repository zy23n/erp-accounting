package com.erp.erp_accounting.accounting.ledger.repository;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;

import java.util.List;

public interface AccountLedgerQueryRepository {

    List<AccountLedgerItemDto> searchLedger(AccountLedgerSearchCondition condition);
}
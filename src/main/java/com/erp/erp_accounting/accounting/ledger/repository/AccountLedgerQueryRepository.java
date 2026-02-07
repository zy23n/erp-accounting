package com.erp.erp_accounting.accounting.ledger.repository;

import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerSearchCondition;
import com.erp.erp_accounting.accounting.ledger.dto.query.AccountLedgerItemDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountLedgerQueryRepository {

    List<AccountLedgerItemDto> searchLedger(AccountLedgerSearchCondition condition);
}
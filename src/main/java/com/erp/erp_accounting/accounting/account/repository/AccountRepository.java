package com.erp.erp_accounting.accounting.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.erp.erp_accounting.accounting.account.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // 루트 계정 조회 (parent = null)
    @Query("select a from Account a where a.parent is null order by a.code")
    List<Account> findRootAccounts();

    // leaf 계정만 조회 (전표 입력 가능 계정)
    @Query("select a from Account a where a.leaf = true order by a.code")
    List<Account> findLeafAccounts();
}

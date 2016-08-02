package com.westernalliancebancorp.positivepay.dao;

import java.util.List;
import java.util.Set;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

/**
 * AccountDao is
 *
 * @author Giridhar Duggirala
 */ 

public interface AccountDao extends GenericDao<Account, Long> {
    Account findByAccountNumberAndBankId(String accountNumber, Long bankId);
    List<Account> findAllByBank(Bank bank);
    List<Account> findAllByBankId(Long bankId);
    List<Account> findAllByBankIds(Long bankIds);
    List<Account> findAllByCompany(Company company);
    List<Account> findAllByCompanyId(String companyId);
    List<Account> findAllByCompany(Long companyId);
    List<Account> findAllByCompanyIds(List<Long> companyIds);
    Account findByAccountNumberAndCompanyId(String accountNumber, Long companyId);
    List<Account> findByAccountNumberCompanyIdBank(Long bankId, Long companyId);
    List<Account> findByAccountIds(List<Long> accountIds);
	Set<Account>  getAccountByUserDetailId(long userId);
    Account findByAccountNumber(String accountNo);
}

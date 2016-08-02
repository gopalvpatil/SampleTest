package com.westernalliancebancorp.positivepay.service;

import java.util.List;
import java.util.Set;

import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AccountCycleCutOff;
import com.westernalliancebancorp.positivepay.model.AccountServiceOption;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;

/**
 * Interface providing service methods to work with the Account
 * @author Anand Kumar
 */

public interface AccountService {
	Account update(Account account);
	Account save(Account account);
	void delete(Account account);
	Account findById(Long id);
	List<Account> findAll();
	List<Account> findAllByCompanyId(String companyId);
	List<Account> findAllByCompanyIds(List<Long> companyIds);
	Account findByAccountNumberAndCompanyId(String accountNumber, Long companyId);
	List<Account> findByAccountNumberCompanyIdbank(Long bankId, Long companyId);
	Set<Account> findByUserDetail(UserDetail userDetail);
	List<AccountServiceOption> findOptionByName();
	List<AccountCycleCutOff> findCycle();
	Account findByAccountNumber(String accountNo);
	AccountCycleCutOff fetchAccountCycleId(String accountNumber);
	AccountServiceOption fetchAccountServiceOption(String accountNumber);
    Account findByAccountNumberAndBankId(String accountNumber, Long bankId);
    void makeAccountInactive(Long accountId);
    AccountDto getAccountDetails(Long accountId);
    Long saveAccountDetails(AccountDto accountDto);
    List<String> getAccountInfoByAccountNumber(String accountNumber);

    Account getAccountFromAccountNumberAndAssignedBankNumber(String accountNumber, String assignedBankNumber);
    public Account getAccountFromAccountNumberString(String accountNumber, String routingNumber, List<Account> userAccounts);
}

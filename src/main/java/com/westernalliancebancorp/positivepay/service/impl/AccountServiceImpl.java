package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.ResetCaching;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.dto.AccountDtoBuilder;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.AccountService;

/**
 * providing implementation for service methods to work with the AccountS.
 * 
 * @author Anand Kumar
 */

@Service
public class AccountServiceImpl implements AccountService {
	/**findByAccountNumberAndBankId
	 * The logger object
	 */
	@Loggable
	private Logger logger;

	/**
	 * The SampleDao dependency
	 */
	@Autowired
	private AccountDao accountDao;

	@Autowired
	private AccountOptionDao accountOptionDao;

	@Autowired
	private AccountCycleCutOffDao accountCycleCutOffDao;
	
	@Autowired
	private BatchDao batchDao;

    @Autowired
    BankDao bankDao;
    
    @Autowired
    CompanyDao companyDao;

    @Autowired
    ResetCaching resetCaching;
	/**
	 * Updates Account to database by calling appropriate dao method.
	 * 
	 * @param account
	 * @return Account object that was saved
	 * @see com.westernalliancebancorp.positivepay.service.AccountService#update(com.westernalliancebancorp.positivepay.model.Account)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Account update(Account account) {
		return accountDao.update(account);
	}

	/**
	 * Saves a Account to database by calling appropriate dao method.
	 * 
	 * @param account
	 * @return Account object that was saved
	 * @see com.westernalliancebancorp.positivepay.service.AccountService#save(com.westernalliancebancorp.positivepay.model.Account)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Account save(Account account) {
		return accountDao.save(account);
	}

	/**
	 * Deletes the Account from the database by calling appropriate dao method.
	 * 
	 * @param account
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Account account) {
		accountDao.delete(account);
	}

	/**
	 * Finds the Account by given id by calling appropriate dao method.
	 * 
	 * @param id
	 *            to find the Account
	 * @return Account object that was saved
	 * @see com.westernalliancebancorp.positivepay.service.AccountService#findById(java.lang.Long)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Account findById(Long id) {
		return accountDao.findById(id);
	}

	/**
	 * finds all the Accounts from the database by calling appropriate dao
	 * method.
	 * 
	 * @return List of Account objects
	 * @see com.westernalliancebancorp.positivepay.service.AccountService#findAll()
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Account> findAll() {
		return accountDao.findAll();
	}

	/**
	 * Finds all accounts for a given company id
	 * 
	 * @param companyId
	 *            for which the accounts are to be retrieved.
	 * @return List of Accounts
	 */
	@Override
	public List<Account> findAllByCompanyId(String companyId) {
		return accountDao.findAllByCompanyId(companyId);
	}

	@Override
	public List<Account> findAllByCompanyIds(List<Long> companyIds) {
		return accountDao.findAllByCompanyIds(companyIds);
	}

	@Override
	public Account findByAccountNumberAndCompanyId(String accountNumber,
			Long companyId) {
		return accountDao.findByAccountNumberAndCompanyId(accountNumber,
				companyId);
	}

	@Override
	public List<Account> findByAccountNumberCompanyIdbank(Long bankId,
			Long companyId) {
		return accountDao.findByAccountNumberCompanyIdBank(bankId, companyId);
	}

	@Override
	public Set<Account> findByUserDetail(UserDetail userDetail) {
		return userDetail.getAccounts();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<AccountServiceOption> findOptionByName() {
		return accountOptionDao.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<AccountCycleCutOff> findCycle() {
		return accountCycleCutOffDao.findAll();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Account findByAccountNumber(String accountNo) {
		return accountDao.findByAccountNumber(accountNo);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public AccountCycleCutOff fetchAccountCycleId(String accountNumber) {
		return accountCycleCutOffDao.fetchAccountCycleInfo(accountNumber);
	}

	@Override
	public AccountServiceOption fetchAccountServiceOption(String accountNumber) {
		return accountOptionDao.fetchAccountServiceOptionInfo(accountNumber);
	}

	@Override
	public Account findByAccountNumberAndBankId(String accountNumber,
			Long bankId) {
		return accountDao.findByAccountNumberAndBankId(accountNumber, bankId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void makeAccountInactive(Long accountId) {
		Account account = accountDao.findById(accountId);
		account.setActive(false);
		accountDao.update(account);
	}
	
	@Override
	@Transactional(readOnly=true)
	public AccountDto getAccountDetails(Long accountId) {
		Account account = accountDao.findById(accountId);
		AccountDto accountDto = new AccountDto();
		AccountDtoBuilder.updateAccountDtoFromEntity(accountDto, account);
		return accountDto;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
    @PositivePaySecurity(group= Permission.TYPE.USER_ROLE_MANAGEMENT, resource = "SAVE_ACCOUNTS", errorMessage = "Doesn't have permission to save accounts.")
	public Long saveAccountDetails(AccountDto accountDto) {
		Account account = null;
		List<Long> accountExisitingUsers = null;
		if(accountDto.getId() != null) {
			account = accountDao.findById(accountDto.getId());
			Set<UserDetail> userDetails = account.getUserDetails();
			accountExisitingUsers = Lambda.extract(userDetails, Lambda.on(UserDetail.class).getId());
		}else{
			account = new Account();
		}
		
		AccountDtoBuilder.updateAccountFromDto(account, accountDto);
		
		if(account.getId() == null) {
			Company company = companyDao.findById(accountDto.getCompanyId());
			Bank bank = company.getBank();
			account.setCompany(company);
			account.setBank(bank); //Fix for WALPP-361 . Setting Bank in account
			account.setOpenDate(new Date()); //Fix for WALPP-392. Setting open date to current date when a new account is saved.
			account = accountDao.save(account);
		}else{
			account = accountDao.update(account);
		}
		
		//Insert new Users to Account
		if(accountDto.getSelectedUserIds() != null && !accountDto.getSelectedUserIds().isEmpty()) {
			List<Long> newUserIds = new ArrayList<Long>();
			newUserIds.addAll(accountDto.getSelectedUserIds());
			if(accountExisitingUsers != null) 
				newUserIds.removeAll(accountExisitingUsers);
			batchDao.addUsersToAccount(account.getId(), newUserIds);
		}
		
		//Delete existing user which are not selected
		if(accountExisitingUsers != null && !accountExisitingUsers.isEmpty()) {
			List<Long> removedUserIds = new ArrayList<Long>();
			removedUserIds.addAll(accountExisitingUsers);
			if(accountDto.getSelectedUserIds() != null)
				removedUserIds.removeAll(accountDto.getSelectedUserIds());
			batchDao.deleteUsersFromAccount(account.getId(), removedUserIds);
		}
		resetCaching.resetGetUserAccountsByCompanyId();
		return account.getId();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> getAccountInfoByAccountNumber(String accountNumber) {
		List<String> accountNumbers = new ArrayList<String>();
		Account account = this.findByAccountNumber(accountNumber);
		List<Account> accounts = this.findAllByCompanyId(String.valueOf(account.getCompany().getId()));
		for(Account acc :  accounts) {
			accountNumbers.add(acc.getNumber());
		}
		return accountNumbers;
	}

    /**
     * This method retrieves the Account Entity for a given accountNumber and assignedBankNumber
     *
     * @param accountNumber
     * @param assignedBankNumber
     * @return Account
     */
    @Override
    @Cacheable(cacheName = "getAccountFromAccountNumberAndAssignedBankNumber")
    public Account getAccountFromAccountNumberAndAssignedBankNumber(String accountNumber, String assignedBankNumber) {
        Account account = null;
        Bank bank = null;
        try {
            //Get the bank for the referenceId
            bank = bankDao.findByAssignedBankNumber(Short.parseShort(assignedBankNumber));
        } catch (Exception emptyResultDataAccessException) {
            logger.warn("Couldn't find bank for given assigned bank number = {}", assignedBankNumber);
            return account;
        }
        //Now get all the accounts within that bank
        try {
            if (bank != null) {
                List<Account> bankAccounts = accountDao.findAllByBankId(bank.getId());
                //Find the account based on accountNumber, bank and accounts within that bank
                account = getAccountFromAccountNumberString(accountNumber, bank.getRoutingNumber(), bankAccounts);
            }
        } catch (Exception emptyResultDataAccessException) {
            logger.warn("Couldn't find account for given account number and assigned Bank number = {}", assignedBankNumber);
        }
        return account;
    }

    //This method return null when a matching account if not found in the list of UserAccounts for a given accountNumber and routingNumber
    public Account getAccountFromAccountNumberString(String accountNumber, String routingNumber, List<Account> userAccounts) {
        //Get the account Number Model from the accountNumber String and routingNumber
        List<Account> matchedAccounts = new ArrayList<Account>();
        for(Account userAccount: userAccounts) {
            if (userAccount.getNumber().equalsIgnoreCase(accountNumber)) {
                matchedAccounts.add(userAccount);
            }
        }
        if (matchedAccounts.size() ==0) {
            logger.info("account number was not found");
            return null;
        } else if (matchedAccounts.size() ==1) {
            logger.info("One matched account was found");
            return matchedAccounts.get(0);
        }else {
            //More than 1 accounts with the same accountNumber
            //Match the routing number too to find the correct account number
            logger.info("More than 1 accounts with the same accountNumber");
            for(Account matchedAccount: matchedAccounts) {
                if (matchedAccount.getBank().getRoutingNumber().equalsIgnoreCase(routingNumber)) {
                    return matchedAccount;
                }
            }
        }
        return null;
    }
}

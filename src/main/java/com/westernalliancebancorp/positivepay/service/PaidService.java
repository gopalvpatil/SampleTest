package com.westernalliancebancorp.positivepay.service;

import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

/**
 * User:	Gopal Patil
 * Date:	Mar 20, 2014
 * Time:	7:55:08 PM
 */
public interface PaidService {
    Map<String, Integer> markCheckPaidByBank(List<Bank> banks);
    Map<String, Integer> markChecksPaidByCompany(List<Company> company);
    Map<String, Integer> markChecksPaidByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksPaidByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksPaidByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksPaidByAccountIds(List<Long> accountIds);
}

package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 17/4/14
 * Time: 3:39 PM
 */
public interface StalePaidService {
    Map<String, Integer> markChecksStalePaidByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStalePaidByCompany(List<Company> company);
    Map<String, Integer> markCheckStalePaidByBank(List<Bank> banks);
    Map<String, Integer> markChecksStalePaidByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStalePaidByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStalePaidByAccountIds(List<Long> accountIds);
}

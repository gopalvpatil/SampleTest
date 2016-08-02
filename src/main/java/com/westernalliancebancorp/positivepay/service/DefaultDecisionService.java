package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * User: gduggirala
 * Date: 6/6/14
 * Time: 7:06 PM
 */
public interface DefaultDecisionService {
    Map<String, Integer> takeDefaultDecisionByAccounts(List<Account> accounts);
    Map<String, Integer> takeDefaultDecisionByCompany(List<Company> company);
    Map<String, Integer> markCheckInvalidAmountByBank(List<Bank> banks);
    Map<String, Integer> takeDefaultDecisionByCompanyIds(List<Long> companyIds);
    Map<String, Integer> takeDefaultDecisionByBankIds(List<Long> bankIds);
    Map<String, Integer> takeDefaultDecisionByAccountIds(List<Long> accountIds);
}

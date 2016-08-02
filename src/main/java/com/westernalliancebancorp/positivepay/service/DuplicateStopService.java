package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * User: gduggirala
 * Date: 24/3/14
 * Time: 12:30 PM
 */
public interface DuplicateStopService {
    Map<String, Integer> markChecksDuplicateStopByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksDuplicateStopByCompany(List<Company> company);
    Map<String, Integer> markCheckDuplicateStopByBank(List<Bank> banks);
    Map<String, Integer> markChecksDuplicateStopByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksDuplicateStopByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksDuplicateStopByAccountIds(List<Long> accountIds);
}

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
public interface StaleStopService {
    Map<String, Integer> markChecksStaleStopByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStaleStopByCompany(List<Company> company);
    Map<String, Integer> markCheckStaleStopByBank(List<Bank> banks);
    Map<String, Integer> markChecksStaleStopByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStaleStopByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStaleStopByAccountIds(List<Long> accountIds);
}

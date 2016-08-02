package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/9/14
 * Time: 1:52 PM
 */
public interface StopPaidService {
    Map<String, Integer> markChecksStopPaidByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStopPaidByCompany(List<Company> company);
    Map<String, Integer> markCheckStopPaidByBank(List<Bank> banks);
    Map<String, Integer> markChecksStopPaidByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStopPaidByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStopPaidByAccountIds(List<Long> accountIds);
}

package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 26/3/14
 * Time: 2:59 PM
 */
public interface StopPresentedService {
    Map<String, Integer> markChecksStopByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStopByCompany(List<Company> company);
    Map<String, Integer> markCheckStopByBank(List<Bank> banks);
    Map<String, Integer> markChecksStopByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStopByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStopByAccountIds(List<Long> accountIds);
}

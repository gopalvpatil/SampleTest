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
 * Time: 3:52 PM
 */
public interface StopNotIssuedService {
    Map<String, Integer> markChecksStopNotIssuedByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStopNotIssuedByCompany(List<Company> company);
    Map<String, Integer> markCheckStopNotIssuedByBank(List<Bank> banks);
    Map<String, Integer> markChecksStopNotIssuedByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStopNotIssuedByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStopNotIssuedByAccountIds(List<Long> accountIds);
}

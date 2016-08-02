package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created By : Moumita Ghosh
 * Date: 03/4/14
 * Time: 3:33 PM
 */
public interface StopStatusService {
    Map<String, Integer> markChecksStopByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksStopByCompany(List<Company> company);
    Map<String, Integer> markCheckStopByBank(List<Bank> banks);
    Map<String, Integer> markChecksStopByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksStopByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksStopByAccountIds(List<Long> accountIds);
}

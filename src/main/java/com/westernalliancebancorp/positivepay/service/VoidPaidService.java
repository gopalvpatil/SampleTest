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
 * Time: 4:32 PM
 */
public interface VoidPaidService {
    Map<String, Integer> markChecksVoidPaidByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksVoidPaidByCompany(List<Company> company);
    Map<String, Integer> markCheckVoidPaidByBank(List<Bank> banks);
    Map<String, Integer> markChecksVoidPaidByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksVoidPaidByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksVoidPaidByAccountIds(List<Long> accountIds);
}

package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 24/3/14
 * Time: 3:15 PM
 */
public interface InvalidAmountService {
    Map<String, Integer> markChecksInvalidAmountByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksInvalidAmountByCompany(List<Company> company);
    Map<String, Integer> markCheckInvalidAmountByBank(List<Bank> banks);
    Map<String, Integer> markChecksInvalidAmountByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksInvalidAmountByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksInvalidAmountByAccountIds(List<Long> accountIds);
}

package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/20/14
 * Time: 5:35 AM
 */
public interface DuplicatePaidService {
    Map<String, Integer> markChecksDuplicatePaidByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksDuplicatePaidByCompany(List<Company> company);
    Map<String, Integer> markCheckDuplicatePaidByBank(List<Bank> banks);
    Map<String, Integer> markChecksDuplicatePaidByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksDuplicatePaidByBankIds(List<Long> bankIds);
    Map<String, Integer>  markChecksDuplicatePaidByAccountIds(List<Long> accountIds);
}

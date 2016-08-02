package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;

import java.util.List;
import java.util.Map;

/**
 * User: gduggirala
 * Date: 26/3/14
 * Time: 3:52 PM
 */
public interface PaidNotIssuedService {
    Map<String, Integer> markChecksPaidNotIssuedByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksPaidNotIssuedByCompany(List<Company> company);
    Map<String, Integer> markCheckPaidNotIssuedByBank(List<Bank> banks);
    Map<String, Integer> markChecksPaidNotIssuedByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksPaidNotIssuedByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksPaidNotIssuedByAccountIds(List<Long> accountIds);
}

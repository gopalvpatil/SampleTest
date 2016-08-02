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
public interface VoidStopService {
    Map<String, Integer> markChecksVoidStopByAccounts(List<Account> accounts);
    Map<String, Integer> markChecksVoidStopByCompany(List<Company> company);
    Map<String, Integer> markCheckVoidStopByBank(List<Bank> banks);
    Map<String, Integer> markChecksVoidStopByCompanyIds(List<Long> companyIds);
    Map<String, Integer> markChecksVoidStopByBankIds(List<Long> bankIds);
    Map<String, Integer> markChecksVoidStopByAccountIds(List<Long> accountIds);
    Map<String, Integer> markChecksVoidStop();
}

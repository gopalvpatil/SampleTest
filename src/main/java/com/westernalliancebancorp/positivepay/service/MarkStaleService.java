package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/9/14
 * Time: 1:52 PM
 */
public interface MarkStaleService {
    Map<String, Integer> markChecksStaleByAccounts(List<Account> accounts) throws CallbackException;
    Map<String, Integer> markChecksStaleByCompany(List<Company> company) throws CallbackException;
    Map<String, Integer> markCheckStaleByBank(List<Bank> banks) throws CallbackException;
    Map<String, Integer> markChecksStaleByCompanyIds(List<Long> companyIds) throws CallbackException;
    Map<String, Integer> markChecksStaleByBankIds(List<Long> bankIds) throws CallbackException;
    Map<String, Integer> markChecksStaleByAccountIds(List<Long> accountIds) throws CallbackException;
}

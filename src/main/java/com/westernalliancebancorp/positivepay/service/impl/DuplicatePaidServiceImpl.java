package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.DuplicatePaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/20/14
 * Time: 5:57 AM
 */
@Service
public class DuplicatePaidServiceImpl implements DuplicatePaidService {
    @Loggable
    Logger logger;

    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckDao checkDao;

    @Autowired
    BatchDao batchDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    WorkflowService workflowService;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;

    @Override
    public Map<String, Integer> markChecksDuplicatePaidByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksDuplicatePaidByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksDuplicatePaidByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksDuplicatePaidByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckDuplicatePaidByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksDuplicatePaidByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksDuplicatePaidByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksDuplicatePaidByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksDuplicatePaidByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksDuplicatePaidByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksDuplicatePaidByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        /**
         * Get all the rows from reference_data table matching the account numbers and which are in UnProcessed state.
         * Find all the checks in the check_detail table which are having the same account number and check number as in reference data and in the status paid.
         * Perform the workflow action "Duplicate Paid"
         */
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.PAID_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        List<CheckDto> checkDtos = batchDao.findAllIssuedMatchedChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                logger.debug("CheckDto id "+checkDto.getId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action duplicatePaid for check id"+checkDto.getId());
                workflowService.performAction(checkDto.getId(), "duplicatePaid", userData);
                logger.debug("Completed Performing workflow action duplicatePaid for check id"+checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_DUPLICATE_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_DUPLICATE_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_DUPLICATE_PAID_UNSUCCESSFUL, re.getMessage() + " CheckDto Id " + checkDto.getId(),re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

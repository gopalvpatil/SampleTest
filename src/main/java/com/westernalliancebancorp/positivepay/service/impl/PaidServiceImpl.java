package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.service.PaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User:	Gopal Patil
 * Date:	Mar 20, 2014
 * Time:	7:56:21 PM
 */
@Service
public class PaidServiceImpl implements PaidService {

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
    ReferenceDataDao referenceDataDao;

    @Override
    public Map<String, Integer> markCheckPaidByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksPaidByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksPaidByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksPaidByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markChecksPaidByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksPaidByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksPaidByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksPaidByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksPaidByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksPaidByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> markChecksPaidByAccountIds(List<Long> accountIds) {
        int itemsProcessedSuccessfully = 0;
        int itemsInError = 0;
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.ISSUED_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        //List of all checks from check detail table which are in issued status where check number, 
        //account id, amount are equal with reference data table
        List<CheckDto> checkDtos = batchDao.findAllIssuedMatchedChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                logger.debug("CheckDto id " + checkDto.getId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action paid for check id" + checkDto.getId());
                workflowService.performAction(checkDto.getId(), "matched", userData);
                logger.debug("Completed Performing workflow action paid for check id" + checkDto.getId());
                itemsProcessedSuccessfully++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, re.getMessage() + " CheckDto Id " + checkDto.getId(),re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfully);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

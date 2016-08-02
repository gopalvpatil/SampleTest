package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.service.StopPaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * User: gduggirala
 * Date: 27/3/14
 * Time: 4:03 PM
 */
@Service
public class StopPaidServiceImpl implements StopPaidService {
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
    public Map<String, Integer> markCheckStopPaidByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksStopPaidByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksStopPaidByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksStopPaidByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markChecksStopPaidByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksStopPaidByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksStopPaidByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksStopPaidByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStopPaidByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksStopPaidByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> markChecksStopPaidByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        //No need to consider stopNotIssued as it is resolving itself to "Stop"
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.STOP_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        //List of all checks from check detail table which are in issued status where check number,
        //account id, amount are equal with reference data table
        List<CheckDto> checkDtos = batchDao.findAllStopButPaidChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                logger.debug("CheckDto id " + checkDto.getId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action paid for check id" + checkDto.getId());
                workflowService.performAction(checkDto.getId(), "stopPaid", userData);
                logger.debug("Completed Performing workflow action paid for check id" + checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException ce) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, ce.getMessage() + " CheckDto Id " + checkDto.getId(), ce), ce);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, re.getMessage() + " CheckDto Id " + checkDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

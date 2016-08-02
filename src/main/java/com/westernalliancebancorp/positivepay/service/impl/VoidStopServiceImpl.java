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
import com.westernalliancebancorp.positivepay.service.VoidStopService;
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
 * User: gduggirala
 * Date: 26/3/14
 * Time: 4:33 PM
 */
@Service
public class VoidStopServiceImpl implements VoidStopService {
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
    
    @Autowired
    UserDetailDao userDetailDao;

    @Override
    public Map<String, Integer> markChecksVoidStopByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksVoidStopByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksVoidStopByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksVoidStopByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckVoidStopByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksVoidStopByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksVoidStopByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksVoidStopByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksVoidStopByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksVoidStopByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksVoidStopByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.VOID_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        //List of all checks from check detail table which are in issued status where check number,
        //account id, amount are equal with reference data table
        List<CheckDto> checkDtos = batchDao.findAllVoidButStopChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action Void, Stop for check id " + checkDto.getId());
                workflowService.performAction(checkDto.getId(), "stopAfterVoid", userData);
                logger.debug("Completed Performing workflow action Void, Stop for check id " + checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, e.getMessage()+ " CheckDto Id " + checkDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_PAID_UNSUCCESSFUL, e.getMessage()+ " CheckDto Id " + checkDto.getId(), e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_VOID_PAID_UNSUCCESSFUL, re.getMessage() + " CheckDto Id " + checkDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
    
    @Override
    public Map<String, Integer> markChecksVoidStop() {
	    Map<String, Integer> returnMap = new HashMap<String, Integer>();
            List<Long> accountIdsList = ModelUtils.executeWithNoCriteria(userDetailDao, batchDao);
            if (accountIdsList != null && !accountIdsList.isEmpty()) {
        	returnMap = markChecksVoidStopByAccountIds(accountIdsList);
               
            } else {
                logger.error("User doesn't seems to have any accounts attached, so silently exiting");
            }
            return returnMap;
        }

}

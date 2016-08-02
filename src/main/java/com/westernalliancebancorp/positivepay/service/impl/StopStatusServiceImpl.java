package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.westernalliancebancorp.positivepay.service.StopStatusService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * Created By : Moumita Ghosh
 * Date: 03/4/14
 * Time: 3:33 PM
 */

@Service
public class StopStatusServiceImpl implements StopStatusService {
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
    public Map<String, Integer> markChecksStopByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksStopByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksStopByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksStopByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckStopByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksStopByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksStopByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksStopByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStopByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksStopByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStopByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.ISSUED_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        //List of all checks from check detail table which are in issued status where check number,
        //account id, amount are equal with reference data table
        List<CheckDto> checkDtos = batchDao.findAllStopUnProcessedChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                logger.debug("CheckDto id "+checkDto.getId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action paid for check id"+checkDto.getId());
                workflowService.performAction(checkDto.getId(), "stop", userData);
                logger.debug("Completed Performing workflow action paid for check id"+checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_STOP_UNSUCCESSFUL, e.getMessage()+" CheckDto Id "+checkDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_STOP_UNSUCCESSFUL, e.getMessage()+" CheckDto Id "+checkDto.getId(), e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_STOP_UNSUCCESSFUL, re.getMessage() +" CheckDto Id "+checkDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

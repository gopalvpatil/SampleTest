package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.StopNotIssuedService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User: gduggirala
 * Date: 26/3/14
 * Time: 3:54 PM
 */
@Service
public class StopNotIssuedServiceImpl implements StopNotIssuedService {

    @Loggable
    Logger logger;

    @Autowired
    AccountDao accountDao;

    @Autowired
    BatchDao batchDao;

    @Autowired
    ReferenceDataDao referenceDataDao;
   
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;

    @Override
    public Map<String, Integer> markChecksStopNotIssuedByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksStopNotIssuedByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksStopNotIssuedByCompany(List<Company> company) {
        List<Long> companyIds = Lambda.extract(company, on(Company.class).getId());
        return markChecksStopNotIssuedByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckStopNotIssuedByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksStopNotIssuedByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksStopNotIssuedByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksStopNotIssuedByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStopNotIssuedByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksStopNotIssuedByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> markChecksStopNotIssuedByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<Long> referenceDataIds = batchDao.findAllStopNotIssuedChecks(accountIds);
        if (!referenceDataIds.isEmpty()) {
            List<ReferenceData> referenceDatas = referenceDataDao.findAllReferenceDataBy(referenceDataIds);
            if (!referenceDatas.isEmpty()) {
                for (ReferenceData referenceData : referenceDatas) {
                    try {
                	logger.debug("Delegating to Reference Data Processor Service : Reference Data Id"+referenceData.getId());
                        referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
                        itemsProcessedSuccessfuly++;
                    } catch (WorkFlowServiceException e) {
                        logger.error(Log.event(Event.MARK_STOP_NOT_ISSUED_UNSUCCESSFUL, e.getMessage() +" ReferenceData Id "+referenceData.getId(), e), e);
                        itemsInError++;
                    } catch (CallbackException e) {
                        logger.error(Log.event(Event.MARK_STOP_NOT_ISSUED_UNSUCCESSFUL, e.getMessage() +" ReferenceData Id "+referenceData.getId(), e), e);
                        itemsInError++;
                    } catch (RuntimeException re) {
                        logger.error(Log.event(Event.MARK_STOP_NOT_ISSUED_UNSUCCESSFUL, re.getMessage() +" ReferenceData Id "+referenceData.getId(), re), re);
                        itemsInError++;
                    }
                }
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

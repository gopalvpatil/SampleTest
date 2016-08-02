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
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.InvalidAmountService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User: gduggirala
 * Date: 24/3/14
 * Time: 5:03 PM
 */
@Service
public class InvalidAmountServiceImpl implements InvalidAmountService {
    @Autowired
    AccountDao accountDao;

    @Autowired
    BatchDao batchDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    ReferenceDataDao referenceDataDao;
   
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;

    @Loggable
    Logger logger;

    @Override
    public Map<String, Integer> markChecksInvalidAmountByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksInvalidAmountByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksInvalidAmountByCompany(List<Company> companies) {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksInvalidAmountByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckInvalidAmountByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksInvalidAmountByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksInvalidAmountByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksInvalidAmountByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksInvalidAmountByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksInvalidAmountByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksInvalidAmountByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.ISSUED_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        List<CheckDto> checkDtos = batchDao.findAllIssuedUnMatchedChecks(accountIds, checkStatusIds);
        for (CheckDto checkDto : checkDtos) {
            try {
                ReferenceData referenceData = referenceDataDao.findById(checkDto.getReferenceDataId());
                logger.debug("Delegating to Reference Data Processor Service : Check id " + checkDto.getId()+"Reference Data Id"+checkDto.getReferenceDataId());
                referenceDataProcessorService.processNonDuplicateReferenceData(referenceData, checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_INVALID_AMOUNT_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_INVALID_AMOUNT_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(),e), e);
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

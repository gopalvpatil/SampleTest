package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.DefaultDecisionService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;
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
 * Date: 6/6/14
 * Time: 7:08 PM
 */
@Service
public class DefaultDecisionServiceImpl implements DefaultDecisionService {
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
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    ExceptionTypeDao exceptionTypeDao;

    @Autowired
    ItemTypeDao itemTypeDao;

    @Override
    public Map<String, Integer> takeDefaultDecisionByAccounts(List<Account> accounts) {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return takeDefaultDecisionByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> takeDefaultDecisionByCompany(List<Company> company) {
        List<Long> companyIds = Lambda.extract(company, on(Company.class).getId());
        return takeDefaultDecisionByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckInvalidAmountByBank(List<Bank> banks) {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return takeDefaultDecisionByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> takeDefaultDecisionByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return takeDefaultDecisionByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> takeDefaultDecisionByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return takeDefaultDecisionByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> takeDefaultDecisionByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<Long> checkDetailIds = batchDao.findAllChecksInExceptionalState(accountIds);
        if (!checkDetailIds.isEmpty()) {
            for (Long checkDetailId : checkDetailIds) {
                try {
                    Check check = checkDao.findCheckByCheckId(checkDetailId);
                    WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                    Account account = accountDao.findById(check.getAccount().getId());
                    String actionNameToPerform = null;
                    actionNameToPerform = account.getDefaultPpDecision().equalsIgnoreCase("pay") ? "pay" : "noPay";
                    if (workflowService.canPerformAction(check, actionNameToPerform, workflowManager)) {
                        ReferenceData referenceData = null;
                        Map<String, Object> userData = new HashMap<String, Object>();
                        //At this moment I cannot think of any states which cannot have reference data and end up in exceptional state.
                        //So I am not handling else condition.
                        if(check.getReferenceData() != null) {
                            referenceData = referenceDataDao.findById(check.getReferenceData().getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_ID.name(),referenceData.getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name(),"Exception resolved with Positive Pay Default Decision :"+actionNameToPerform);
                        }
                        //Get the referenceData and set that into the userData
                        workflowService.performAction(check, actionNameToPerform,userData);
                    }
                    itemsProcessedSuccessfuly++;
                } catch (WorkFlowServiceException e) {
                    logger.error(Log.event(Event.MARK_PAIDNOTISSUED_UNSUCCESSFUL, e.getMessage() + " Check Id " + checkDetailId,e), e);
                    itemsInError++;
                } catch (CallbackException e) {
                    logger.error(Log.event(Event.MARK_PAIDNOTISSUED_UNSUCCESSFUL, e.getMessage() + " Check Id " + checkDetailId,e), e);
                    itemsInError++;
                } catch (RuntimeException re) {
                    logger.error(Log.event(Event.MARK_PAIDNOTISSUED_UNSUCCESSFUL, re.getMessage() + " Check Id " + checkDetailId,re), re);
                    itemsInError++;
                }
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: gduggirala
 * Date: 22/4/14
 * Time: 7:05 PM
 */
@Service("workflowUtil")
public class WorkflowUtil {
    @Loggable
    Logger logger;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    WorkflowDao workflowDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    CheckLinkageDao checkLinkageDao;
    @Autowired
    ReferenceDataDao referenceDataDao;

    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    ActionDao actionDao;
    @Autowired
    StartStatusService startStatusService;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean changeCurrentCheckNumber(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        if (referenceData == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into this state there must " +
                    "be reference data set, check id :%d", check.getId()));
        }
        Map<String, Object> userData = callbackContext.getUserData();
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        String actionDescription = workflowManager.getActionDescription(callbackContext.getCurrenStatusName(), callbackContext.getActionNameToPerform());
        String checkNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId()), "start", checkStatusDao);
        ModelUtils.handleChangeCheckNumberNonWorkflowCheckHistory(check, checkStatus, Action.ACTION_NAME.CHANGE_CHECK_NUMBER,
                check.getCheckNumber(), checkNumber, actionDescription, checkStatusDao, actionDao, checkHistoryDao);

        //We are in the process of correcting the Current Check number, so there is no need to correct the reference data.
        check.setCheckNumber(checkNumber);
        checkDao.update(check);
        try {
            //As the checks earlier status is "Paid" and paid checks do have reference id associated to them, we have to set the referenceId back to original referenceId from History table.
            userData.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
            workflowService.performAction(check, "moveToStart", callbackContext.getUserData());
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }

        try {
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        List<Check> checkList = new ArrayList<Check>();
        checkList.add(check);
        try {
            startStatusService.processStartChecks(checkList);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean changeCurrentAccountNumber(CallbackContext callbackContext)  throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into 'Duplicate Stop' there must be reference data set, check id :%d", check.getId()));
        }
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        String actionDescription = workflowManager.getActionDescription(callbackContext.getCurrenStatusName(), callbackContext.getActionNameToPerform());
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        Map<String, Object> userData = callbackContext.getUserData();
        String accountNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name());
        Account existingAccount = accountDao.findById(check.getAccount().getId());
        Account newAccount = accountDao.findByAccountNumberAndBankId(accountNumber, existingAccount.getBank().getId());
        if (newAccount == null) {
            throw new CallbackException(String.format("Account number %s is not existing with the bank id %d", accountNumber, existingAccount.getBank().getId()));
        }
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId()), "start", checkStatusDao);
        ModelUtils.handleChangeAccountNumberNonWorkflowCheckHistory(check, checkStatus, Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER,
                existingAccount.getNumber(), newAccount.getNumber(), actionDescription, checkStatusDao, actionDao, checkHistoryDao);
        check.setAccount(newAccount);
        checkDao.update(check);
        try {
            //As the checks earlier status is "Paid" and paid checks do have reference id associated to them, we have to set the referenceId back to original referenceId from History table.
            userData.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
            workflowService.performAction(check, "moveToStart", callbackContext.getUserData());
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        try {
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        List<Check> checkList = new ArrayList<Check>();
        checkList.add(check);
        try {
            startStatusService.processStartChecks(checkList);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory insertNonWorkflowActionIntoHistory(Check check,String comment, Action.ACTION_NAME action_name, CallbackContext callbackContext,AdjustmentCheck adjustmentCheck) throws CallbackException {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        Action action = ModelUtils.createOrRetrieveAction(action_name,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        CheckHistory checkHistory = new CheckHistory();
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        String targetStatusName = workflowManager.getTargetStatusName(callbackContext.getActionNameToPerform(), checkStatus.getName());
        CheckStatus targetCheckStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, targetStatusName, checkStatusDao);
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setAction(action);
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(comment);
        checkHistory.setAdjustmentCheck(adjustmentCheck);
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory insertNonWorkflowActionIntoHistory(Check check,String comment, Action.ACTION_NAME action_name, CallbackContext callbackContext) throws CallbackException {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        Action action = ModelUtils.createOrRetrieveAction(action_name,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        CheckHistory checkHistory = new CheckHistory();
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        String targetStatusName = workflowManager.getTargetStatusName(callbackContext.getActionNameToPerform(), checkStatus.getName());
        CheckStatus targetCheckStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, targetStatusName, checkStatusDao);
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setAction(action);
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(comment);
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory insertNonWorkflowActionIntoHistory(Check check,String comment, Action.ACTION_NAME action_name,CheckStatus targetCheckStatus,ReferenceData referenceData)  {
        Action action = ModelUtils.createOrRetrieveAction(action_name,
                check.getWorkflow().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        CheckHistory checkHistory = new CheckHistory();
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setAction(action);
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(comment);
        checkHistory.setUserComment("None");
        checkHistory.setReferenceData(referenceData);
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }
}

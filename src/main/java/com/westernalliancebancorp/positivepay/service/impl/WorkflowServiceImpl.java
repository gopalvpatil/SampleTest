package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Date;
import java.util.Map;

import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;
import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackContext;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.PostActionCallback;
import com.westernalliancebancorp.positivepay.workflow.PreActionCallback;
import com.westernalliancebancorp.positivepay.workflow.StatusArrivalCallback;
import com.westernalliancebancorp.positivepay.workflow.StatusDepartureCallback;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * WorkflowServiceImpl is
 *
 * @author Giridhar Duggirala
 */

@Service("workflowService")
public class WorkflowServiceImpl implements WorkflowService, ApplicationContextAware {
    ApplicationContext applicationContext;
    /**
     * The logger object
     */
    @Loggable
    private Logger logger;

    @Autowired
    CheckDao checkDao;

    @Autowired
    ActionDao actionDao;

    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Autowired
    CheckStatusDao checkStatusDao;
    
    @Autowired
    CheckService checkService;
    
    @Autowired
    ExceptionStatusDao exceptionStatusDao;
    
    @Autowired
    ReferenceDataDao referenceDataDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String forceStatusChange(Long checkId, String targetStatusName, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException {
        Check check = checkDao.findById(checkId);
        return forceStatusChange(check, targetStatusName, userData);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String forceStatusChange(Check check, String targetStatusName, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException {
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        CheckStatus currentStatus = check.getCheckStatus();
        if(currentStatus == null) {
            logger.info("Assuming that the check is in start state as there is no current state");
            currentStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, CheckStatus.START_STATUS_NAME, checkStatusDao);
            check.setCheckStatus(currentStatus);
            checkDao.update(check);
        }else{
            currentStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        }
        CallbackContext callbackContext = new CallbackContext(check, userData, workflowManager,targetStatusName, currentStatus.getName());
        String targetStatus = workflowManager.getStatusDescription(targetStatusName);
        CheckStatus targetCheckStatus = retrieveOrCreateCheckStatus(workflowManager, targetStatusName);
        handleCheckHistory(check, userData, targetCheckStatus, "Force Status Change", "Force Status Change");
        userData.put(STANDARD_MAP_KEYS.FORCE_STATUS_CHANGE.name(), Boolean.TRUE);
        handleStatusCallbacks(workflowManager.getOnStatusDepatureCallbak(currentStatus.getName()), callbackContext, "ON_STATUS_DEPARTURE");

        check.setCheckStatus(targetCheckStatus);
        checkDao.update(check);
        handleStatusCallbacks(workflowManager.getOnStatusArrivalCallback(targetStatusName), callbackContext, "ON_STATUS_ARRIVAL");
        return targetCheckStatus.getName();
    }

    @Override
    @RollbackForEmulatedUser
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String performAction(Long checkId, String actionNameToPerform, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException {
        Check check = checkDao.findById(checkId);
        logger.debug("Check information retreived successfully by check id, workflow being used is "+check.getWorkflow().getName());
        return performAction(check, actionNameToPerform, userData);
    }

    @Override
    //@WorkFlowExecutionSequence
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String  performAction(Check check, String actionNameToPerform, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException {
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        CheckStatus currentStatus = check.getCheckStatus();
        if(currentStatus == null) {
            logger.info("Assuming that the check is in start state as there is no current state");
            currentStatus = retrieveOrCreateCheckStatus(workflowManager, CheckStatus.START_STATUS_NAME);
            check.setCheckStatus(currentStatus);
            checkDao.update(check);
        }else{
            currentStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        }
        CallbackContext callbackContext = new CallbackContext(check, actionNameToPerform, userData, workflowManager, currentStatus.getName());
        if (!canPerformAction(check, actionNameToPerform, workflowManager)) {
            throw new WorkFlowServiceException(String.format("Action %s is not available for status %s Check Id : %d", actionNameToPerform, check.getCheckStatus().getName(), check.getId()));
        }
        if(currentStatus.getName().equals(CheckStatus.START_STATUS_NAME))
        {
            checkEntryHistoryforStart(check.getId());
        }
        Action currentActionPerformed = createOrRetrieveAction(actionNameToPerform, workflowManager.getActionDescription(currentStatus.getName(), actionNameToPerform), currentStatus.getVersion(), workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId()), currentStatus);
        handleActionCallbacks(workflowManager.getPreActionExecutionCallback(actionNameToPerform, currentStatus.getName()), callbackContext, "PRE_ACTION");
        String targetStatus =  workflowManager.getTargetStatusName(actionNameToPerform, currentStatus.getName());
        CheckStatus targetCheckStatus = retrieveOrCreateCheckStatus(workflowManager, targetStatus);
        CheckHistory checkHistory = handleCheckHistory(check, userData, targetCheckStatus, currentActionPerformed);
        callbackContext.setCheckHistory(checkHistory);
        handleStatusCallbacks(workflowManager.getOnStatusDepatureCallbak(currentStatus.getName()), callbackContext, "ON_STATUS_DEPARTURE");
        String targetStatusName = workflowManager.getTargetStatusName(actionNameToPerform, currentStatus.getName());
        /* If the target status is exceptional status, mark it as "Open" */
        if(targetCheckStatus.getExceptionalStatus())
        {
            ExceptionStatus exceptionStatus = ModelUtils.createOrRetrieveExceptionStatus(ExceptionStatus.STATUS.OPEN, exceptionStatusDao);
            check.setExceptionStatus(exceptionStatus);
        }
        /* If the previous state is exceptional status and the target status is non exceptional status then set that as closed */
        else if (!targetCheckStatus.getExceptionalStatus() && currentStatus.getExceptionalStatus())
        {
            ExceptionStatus exceptionStatus = ModelUtils.createOrRetrieveExceptionStatus(ExceptionStatus.STATUS.CLOSED, exceptionStatusDao);
            check.setExceptionStatus(exceptionStatus);
        }
        check.setCheckStatus(targetCheckStatus);
        check.setAction(currentActionPerformed);
        check.setPaymentStatus(workflowManager.getPaymentStatus(actionNameToPerform, currentStatus.getName()));
        checkDao.update(check);
        handleActionCallbacks(workflowManager.getPostActionExecutionCallback(actionNameToPerform, currentStatus.getName()), callbackContext, "POST_ACTION");
        handleStatusCallbacks(workflowManager.getOnStatusArrivalCallback(targetStatusName), callbackContext, "ON_STATUS_ARRIVAL");
        return targetCheckStatus.getName();
    }

    private boolean handleActionCallbacks(String callbackClass, CallbackContext callbackContext, String callBackFor) throws WorkFlowServiceException, CallbackException {
        if (callbackClass != null && !callbackClass.isEmpty()) {
            if (callBackFor.equals("PRE_ACTION")) {
                PreActionCallback preActionCallback = applicationContext.getBean(callbackClass, PreActionCallback.class);
                return preActionCallback.executePreActionCallback(callbackContext);
            }
            if (callBackFor.equals("POST_ACTION")) {
                PostActionCallback postActionCallback = applicationContext.getBean(callbackClass,PostActionCallback.class );
                return postActionCallback.executePostActionCallback(callbackContext);
            }
        }
        return Boolean.FALSE;
    }

    private boolean handleStatusCallbacks(String callbackClass, CallbackContext callbackContext, String callBackFor) throws WorkFlowServiceException, CallbackException {
        if (callbackClass != null && !callbackClass.isEmpty()) {
            if (callBackFor.equals("ON_STATUS_ARRIVAL")) {
                StatusArrivalCallback statusArrivalCallback = applicationContext.getBean(callbackClass,StatusArrivalCallback.class);
                return statusArrivalCallback.executeOnStatusArrival(callbackContext);
            }
            if (callBackFor.equals("ON_STATUS_DEPARTURE")) {
                StatusDepartureCallback statusDepartureCallback = applicationContext.getBean(callbackClass,StatusDepartureCallback.class);;
                return statusDepartureCallback.executeStatusDepartureCallback(callbackContext);
            }
        }
        return Boolean.FALSE;
    }

    private CheckHistory handleCheckHistory(Check check, Map<String, Object> userData, CheckStatus targetCheckStatus, Action action) {
	WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setCheckAmount(check.getIssuedAmount() == null ? check.getVoidAmount() : check.getIssuedAmount());
        if(checkHistory.getCheckAmount()==null) /** Special cases for paidNotIssued where no issuedAmount/voidAmount is present **/
        {
            ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
            checkHistory.setCheckAmount(referenceData.getAmount());
        }
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setAction(action);
        //TODO: Giridhar remove it when we make this column nullable in check_detail_history
        checkHistory.setMatchStatus(check.getMatchStatus() == null ? Constants.UNMATCHED : check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setAuditInfo(new AuditInfo());
        if (userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()) == null || ((String) userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name())).isEmpty()) {
            /*if (action.getName().equals(Action.ACTION_NAME.CHANGE_CHECK_NUMBER)) {
                checkHistory.setSystemComment(String.format("Check number changed to \"%s\"", check.getCheckNumber()));
            } else if (action.getName().equals(Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER)) {
                checkHistory.setSystemComment(String.format("Account number changed to \"%s\"", check.getAccount().getNumber()));
            } else */if (targetCheckStatus.getName().equals(CheckStatus.STALE_STATUS_NAME) && PositivePayThreadLocal.getSource().equals(PositivePayThreadLocal.SOURCE.Batch.name())) {
                checkHistory.setSystemComment(String.format("Payment became stale"));
            } else if (targetCheckStatus.getExceptionalStatus()) {
                checkHistory.setSystemComment(String.format("\"%s\" Exception on Check \"%s\"", targetCheckStatus.getDescription(), check.getCheckNumber()));
                checkHistory.setExceptionCreationDate(new Date());
            } else if (!targetCheckStatus.getExceptionalStatus() && check.getCheckStatus().getExceptionalStatus()) {
                checkHistory.setSystemComment(String.format("\"%s\" Exception resolved : \"%s\"", check.getCheckStatus().getDescription(), action.getDescription()));
                checkHistory.setExceptionResolvedDate(new Date());
            } else {
                checkHistory.setSystemComment(String.format("Payment status has been moved from \"%s\" to \"%s\" by taking the action \"%s\"", checkStatus.getDescription(), targetCheckStatus.getDescription(), action.getDescription()));
            }
        } else {
            checkHistory.setSystemComment((String) userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()));
            userData.remove(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name());
        }
        if (userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name()) == null || ((String) userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name())).isEmpty()) {
            checkHistory.setUserComment("None");
        } else {
            checkHistory.setUserComment((String) userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name()));
            userData.remove(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name());
        }
        checkHistory.setPaymentStatus(workflowManager.getPaymentStatus(action.getName(), checkStatus.getName()));
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }

    private CheckHistory handleCheckHistory(Check check, Map<String, Object> userData, CheckStatus targetCheckStatus, String actionName, String actionDescription) {
	WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        BeanUtils.copyProperties(check, checkHistory);
        Action action = createOrRetrieveAction(actionName, actionDescription, checkStatus.getVersion(), workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId()), checkStatus);
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(check.getCheckStatus());
        checkHistory.setTargetCheckStatus(targetCheckStatus);
        checkHistory.setAction(action);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        //TODO: Giridhar remove it when we make this column nullable in check_detail_history
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setAuditInfo(new AuditInfo());
        if (userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()) == null || ((String) userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name())).isEmpty()) {
            checkHistory.setSystemComment(String.format("Payment status has been moved from \"%s\" to \"%s\" by taking the action \"%s\"", checkStatus.getDescription(), targetCheckStatus.getDescription(), actionDescription));
        } else {
            checkHistory.setSystemComment((String) userData.get(STANDARD_MAP_KEYS.SYSTEM_COMMENT.name()));
        }
        if (userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name()) == null || ((String) userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name())).isEmpty()) {
            checkHistory.setUserComment("None");
        } else {
            checkHistory.setUserComment((String) userData.get(STANDARD_MAP_KEYS.USER_COMMENT.name()));
        }
        checkHistory.setPaymentStatus(workflowManager.getPaymentStatus(action.getName(), checkStatus.getName()));
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }

    private Action createOrRetrieveAction(String actionName, String actionDescription, Integer version, WorkflowManager workflowManager, CheckStatus checkStatus) {
        Action action = null;
        action = actionDao.findByNameAndVersion(actionName, version, Action.ACTION_TYPE.WORK_FLOW_ACTION);
        if(action == null) {
            action = new Action();
            action.setActionType(Action.ACTION_TYPE.WORK_FLOW_ACTION);
            action.setDescription(actionDescription);
            action.setName(actionName);
            action.setVersion(version);
            action.isAdminAction(workflowManager.isAnActionAction(actionName, checkStatus.getName()));
            actionDao.save(action);
        }else{
            if(!action.getDescription().equals(actionDescription)){
               action.setDescription(actionDescription);
               actionDao.update(action);
            }
        }
        return action;
    }

    private CheckStatus retrieveOrCreateCheckStatus(WorkflowManager workflowManager, String targetStatusName) {
        CheckStatus checkStatus = null;
        checkStatus = checkStatusDao.findByNameAndVersion(targetStatusName, workflowManager.getSupportedVersion());
        if (checkStatus == null) {
            checkStatus = new CheckStatus();
            checkStatus.setName(targetStatusName);
            checkStatus.setVersion(workflowManager.getSupportedVersion());
            checkStatus.setDescription(workflowManager.getStatusDescription(targetStatusName));
            checkStatus.setExceptionalStatus(workflowManager.isExceptionalStatus(targetStatusName));
            checkStatusDao.save(checkStatus);
        }
        return checkStatus;
    }

    private void checkEntryHistoryforStart(long checkId) {
        String source = PositivePayThreadLocal.getInputMode() == null ? PositivePayThreadLocal.getSource() : PositivePayThreadLocal.getInputMode();
        checkService.addHistoryEntryForNewCheck(checkId, source);
       // PositivePayThreadLocal.removeInputMode();
    }

    public boolean canPerformAction(Check check, String actionNameToPerform, WorkflowManager workflowManager) {
    	Map<String, String> actionNamesToDescriptionMap = workflowManager.getActionsForStatus(check.getCheckStatus().getName());
        return actionNamesToDescriptionMap.containsKey(actionNameToPerform);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
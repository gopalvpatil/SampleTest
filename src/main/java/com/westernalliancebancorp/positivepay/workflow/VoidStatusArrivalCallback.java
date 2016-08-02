package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * This class is configured to call whenever a check arrives into this status.
 * When a check arrives into this status it will first check if the issued_date is less than todays date.. if it is less than todays date then
 * it will be considered for further processing.
 * <p/>
 * Reference_data table will be checked with the check number and the account_id and referance_data status as "NOT_PROCESSED"
 * If no reference data is found then return with boolean true.
 * If more than one is found then throw callback exception as no two entries in reference data can existing with the same check number and the account id.
 * If only one is found then.
 * If item type is "Paid" then move the check into "Viod, Paid" status by taking the action "voidPaid"
 * If item type is "Stop" then move the check into "Void, Stop" status by taking the action "voidStop"
 * <p/>
 * Created with IntelliJ IDEA.
 * User: Moumita Ghosh
 * Date: 1/4/14
 * Time: 9:15 PM
 */
@Service("voidStatusArrivalCallback")
public class VoidStatusArrivalCallback implements StatusArrivalCallback {
    @Loggable
    Logger logger;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckDao checkDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Autowired
    WorkflowDao workflowDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        boolean canCheckWithReferenceData = false;
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        Map<String, Object> userData = callbackContext.getUserData();
        handleVoidNotIssued(check, userData);
        try {
            canCheckWithReferenceData = DateUtils.isDateOlderThanToday(check.getVoidDate()==null?check.getIssueDate():check.getVoidDate());
        } catch (ParseException e) {
            throw new CallbackException("Exception while processing the Date in VoidStatusArrivalCallback", e);
        }
        if (!canCheckWithReferenceData) {
            return Boolean.TRUE;
        }
        ReferenceData referenceData = check.getReferenceData();
        if (referenceData == null) {
            logger.info("No Reference Data found check might either has arrived from UI or a fresh one.");
            List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAndAccountIdByStatus(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.STATUS.NOT_PROCESSED);
            if (referenceDataList.isEmpty()) {
                logger.info(String.format("No records found in reference data table, matching the check number %s and the account id %d", check.getCheckNumber(), check.getAccount().getId()));
                //If no item in reference data is found that means we are good.. so just return.
                return Boolean.TRUE;
            } else if (referenceDataList.size() > 1) {
                logger.error(String.format("Functionality error cannot expect more than one check matching the criteria of check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                throw new CallbackException(String.format("More than one item in reference data is found with the status not processed and matching check id %d and account id %d functionality error?", check.getId(), check.getAccount().getId()));
            } else if (referenceDataList.size() == 1) {
                referenceData = referenceDataList.get(0);
            }
        } else {
            referenceData = referenceDataDao.findById(referenceData.getId());
        }
        ReferenceData.ITEM_TYPE item_type = referenceData.getItemType();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
        if (item_type.equals(ReferenceData.ITEM_TYPE.PAID)) {
            try {
                workflowService.performAction(check, "voidPaid", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if (item_type.equals(ReferenceData.ITEM_TYPE.STOP)) {
            try {
                workflowService.performAction(check, "stopAfterVoid", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        }
        return Boolean.TRUE;
    }

    private void handleVoidNotIssued(Check check, Map<String, Object> userData) throws CallbackException {
        ItemType itemType = itemTypeDao.findById(check.getItemType().getId());
        if (itemType.getName().equalsIgnoreCase(CheckStatus.VOID_STATUS_NAME)) {
            if (userData.get(WorkflowService.STANDARD_MAP_KEYS.CALLED_BY.name()) != null &&
                    !userData.get(WorkflowService.STANDARD_MAP_KEYS.CALLED_BY.name()).equals("VoidStatusArrivalCallback") &&
                    userData.get(WorkflowService.STANDARD_MAP_KEYS.CALLED_FOR_ACTION.name()) != null &&
                    !userData.get(WorkflowService.STANDARD_MAP_KEYS.CALLED_FOR_ACTION.name()).equals("voidNotIssued")) {
                if (!isInVoidNotIssuedAtleastOnce(check) || !isInIssuedAtleastOnce(check)) {
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.CALLED_BY.name(), "VoidStatusArrivalCallback");
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.CALLED_FOR_ACTION.name(), "voidNotIssued");
                    try {
                    	// Fix for WALPP-35
                        workflowService.performAction(check, "voidNotIssued", userData);
                        } catch (WorkFlowServiceException e) {
                        throw new CallbackException(e);
                    }
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private boolean isInVoidNotIssuedAtleastOnce(Check check) {
        Check myCheck = checkDao.findById(check.getId());
        Workflow workflow = workflowDao.findById(myCheck.getWorkflow().getId());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), CheckStatus.VOID_NOT_ISSUED, checkStatusDao);
        //)checkStatusDao.findByNameAndVersion(CheckStatus.VOID_NOT_ISSUED, workflow.getVersion());
        List<CheckHistory> checkHistoryList = checkHistoryDao.findByCheckIdandStatusId(myCheck.getId(), checkStatus.getId());
        if (checkHistoryList == null || checkHistoryList.isEmpty()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    private boolean isInIssuedAtleastOnce(Check check) {
        Check myCheck = checkDao.findById(check.getId());
        Workflow workflow = workflowDao.findById(myCheck.getWorkflow().getId());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), CheckStatus.ISSUED_STATUS_NAME, checkStatusDao);
        List<CheckHistory> checkHistoryList = checkHistoryDao.findByCheckIdandStatusId(myCheck.getId(), checkStatus.getId());
        if (checkHistoryList == null || checkHistoryList.isEmpty()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }
}

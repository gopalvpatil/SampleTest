package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import java.text.ParseException;

/**
 * This class is configured to become active when a check has arrived into the status "voidStopAccountNumberChanged".
 * There will be two checks.
 * 1. Original check which has been moved to "Void, Paid" status because another check with the same checknumber and the account number is appeared in the "Paid" list.
 * 2. A check which has been created as a result of entering the new check number
 * <p/>
 * Check 1 will moved back to its original status "Void", during this process of moving it back to its original status the following should be done.
 * Reference id should be set to null as it is moved to "Void, paid" against some reference_id
 * <p/>
 * Check 2 will take a different route.
 * First we check if the check is already existing in the check_detail table with the status "Issued"
 * if found and a reference item is existing in reference_data table with the status "NOT_PROCESSED" then move it to paid status. During this move make sure that the check will have the reference_id populated
 * If check 2 is not found then moved it to "Paid, not issued" status, during this move make sure that the check will have the reference_id populated.
 *
 * There is one more method which will try to correct the reference_id the reason..
 * When a check image is read by software it might have misread the check number, so when the customer looks at the image and when he corrects the check number the data in the
 * reference_data should also be corrected.
 *
 * So the method "getCorrectReferenceData" will try to correct the referenceData record.
 *
 * TODO:Security check should me made before making this change. If new check is already existing then user should have rights on this new check.
 * <p/>
 * User: gduggirala
 * Date: 3/4/14
 * Time: 12:38 PM
 */
@Component("voidStopAccountNumberChanged")
@Deprecated
public class VoidStopAccountNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
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
    LinkageTypeDao linkageTypeDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ReferenceDataService referenceDataService;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        if (check.getReferenceData()==null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into void Paid there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        
        //This method will process the records in ExceptionalReferenceData, these will check if
        String oldCheckNumber = referenceData.getCheckNumber();
        Long oldAccountId = referenceData.getAccount().getId();
        
        Map<String, Object> userData = callbackContext.getUserData();
        String accountNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name());
        referenceData = referenceDataService.correctAccountNumber(referenceData.getId(), accountNumber);
        Account newAccount = accountDao.findById(referenceData.getAccount().getId());
        try {
            //As the checks earlier status is "Void" and void checks will not have any reference id's so we have to set the referenceid back to null.
            //Making the reference id null is taken care by "ReferenceDataStatusUpdate"
            userData.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
            workflowService.performAction(check, "misreadAccountNumber", callbackContext.getUserData());
            try {
                exceptionalReferenceDataService.processDuplicatesInExceptionsReferneceDataWith(oldCheckNumber, oldAccountId);
            } catch (ParseException e) {
                throw new CallbackException(e);
            } catch (AccountNotFoundException e) {
                throw new CallbackException(e);
            }
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }

        Check newCheck = checkDao.findCheckBy(newAccount.getNumber(), check.getCheckNumber());
        CheckStatus checkStatus = null;
        if (newCheck != null) {
            checkStatus = newCheck.getCheckStatus();
        }
        //The check number and account number that has been entered by customer is already
        // existing. The check which is existing is currently in ISSUED status, so as the check initial status is in "Void, paid" we move it to "Paid" status
        //Please refer to https://intraedge.atlassian.net/wiki/display/WALPP/Status%3A+Void%2C+Stop
        if (newCheck != null && checkStatus.getName().equals(CheckStatus.ISSUED_STATUS_NAME)) {
            establishLinkage(check, newCheck, checkDao, linkageTypeDao);
            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
            try {
                workflowService.performAction(newCheck, "stop", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if (newCheck == null) {
            Workflow workflow = workflowDao.findById(check.getWorkflow().getId());
            WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(workflow.getId());
            CheckStatus startStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, "start", checkStatusDao);
            newCheck = new Check();
            BeanUtils.copyProperties(check, newCheck);
            newCheck.setId(null);
            newCheck.setAccount(newAccount);
            newCheck.setCheckStatus(startStatus);
            newCheck.setCheckHistorySet(Collections.<CheckHistory>emptySet());
            newCheck.setComments(Collections.<CheckDetailComment>emptySet());
            newCheck.setChildChecks(Collections.<Check>emptySet());
            AuditInfo auditInfo = new AuditInfo();
            newCheck.setAuditInfo(auditInfo);
            newCheck.setDigest(fileUploadUtils.getDigest(newAccount.getNumber(),check.getCheckNumber()));
            checkDao.save(newCheck);
            establishLinkage(check, newCheck, checkDao, linkageTypeDao);
            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
            try {
                workflowService.performAction(newCheck, "stopNotIssued", userData);
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        }
        return Boolean.TRUE;
    }

    private void establishLinkage(Check parentCheck, Check childCheck, CheckDao checkDao, LinkageTypeDao linkageTypeDao) {
        childCheck.setParentCheck(parentCheck);
        childCheck.setLinkageType(ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ACCOUNT_NUMBER_CHANGED, linkageTypeDao));
        checkDao.save(childCheck);
    }
}

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
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 11/4/14
 * Time: 9:29 PM
 */
@Deprecated
@Component("duplicateStopCheckNumberChanged")
public class DuplicateStopCheckNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
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
    CheckHistoryDao checkHistoryDao;
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
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into void Paid there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        //When the check number has been changed that means the check number of the reference data also needs to be
        //Corrected.
        Map<String, Object> userData = callbackContext.getUserData();
        String checkNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name());
        //We are in the process of correcting the Check number, but before that lets correct the
        //Reference data.
        //ModelUtils.getCorrectedReferenceData(referenceData, checkNumber, referenceDataDao);
        //This method will process the recrds in ExceptionalReferenceData, these will check if
        String oldCheckNumber = referenceData.getCheckNumber();
        Long oldAccountId = referenceData.getAccount().getId();
        referenceDataService.correctCheckNumber(referenceData.getId(), checkNumber);
        ReferenceData historicalReferenceData = ModelUtils.getReferenceDataFromHistory(check, CheckStatus.STOP_STATUS_NAME, checkStatusDao, checkHistoryDao, workflowDao);
        try {
            //As the checks earlier status is "Void" and void checks will not have any reference id's so we have to set the referenceid back to null.
            //Making the reference id null is taken care by "ReferenceDataStatusUpdate"
            if (historicalReferenceData != null) {
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_ID.name(), historicalReferenceData.getId());
            } else {
                throw new CallbackException("There is no reference id available for the check which has been moved into stop status. Functionality error for checkId " + check.getId());
            }
            workflowService.performAction(check, "misreadCheckNumber", callbackContext.getUserData());
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

        Check newCheck = checkDao.findCheckBy(check.getAccount().getNumber(), checkNumber);
        CheckStatus checkStatus = null;
        if (newCheck != null) {
            checkStatus = newCheck.getCheckStatus();
        }
        //The check number and account number that has been entered by customer is already
        // existing. The check which is existing is currently in ISSUED status, so as the check initial status is in "Duplicate stop" we move it to "Stop" status
        //Please refer to https://intraedge.atlassian.net/wiki/display/WALPP/Exception%3A+Duplicate+Stop
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
            newCheck.setCheckNumber(checkNumber);
            newCheck.setDigest(fileUploadUtils.getDigest(accountDao.findById(referenceData.getAccount().getId()).getNumber() , referenceData.getCheckNumber()));
            newCheck.setCheckStatus(startStatus);
            newCheck.setCheckHistorySet(Collections.<CheckHistory>emptySet());
            newCheck.setChildChecks(Collections.<Check>emptySet());
            AuditInfo auditInfo = new AuditInfo();
            newCheck.setAuditInfo(auditInfo);
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
        childCheck.setLinkageType(ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.CHECK_NUMBER_CHANGED, linkageTypeDao));
        checkDao.save(childCheck);
    }
}

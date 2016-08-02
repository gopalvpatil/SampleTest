package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import java.text.ParseException;

/**
 * User: gduggirala
 * Date: 14/4/14
 * Time: 2:39 PM
 */
@Component("stopPaidAccountNumberChanged")
public class StopPaidAccountNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
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
    ReferenceDataProcessorService referenceDataProcessorService;
    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into 'Stop' there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        
        //This method will process the records in ExceptionalReferenceData, these will check if
        String oldCheckNumber = referenceData.getCheckNumber();
        Long oldAccountId = referenceData.getAccount().getId();
        String oldAccountNumber = referenceData.getAccount().getNumber();
        try {
            ReferenceData historicalReferenceData = ModelUtils.getReferenceDataFromHistory(check, CheckStatus.STOP_STATUS_NAME, checkStatusDao, checkHistoryDao, workflowDao);
            if (historicalReferenceData == null) {
                throw new CallbackException("Functionality error: Check is: " + check.getId() + " is in Stop status once.. but unable to find the reference id of that");
            }
            Map<String, Object> userData = callbackContext.getUserData();
            //As the checks earlier status is "Stop" and paid checks do have reference id associated to them, we have to set the referenceId back to original referenceId from History table.
            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), historicalReferenceData);
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

        //When the check number has been changed that means the check number of the reference data also needs to be
        //Corrected.
        Map<String, Object> userData = callbackContext.getUserData();
        String accountNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name());
        referenceData = referenceDataService.correctAccountNumber(referenceData.getId(), accountNumber);
        try {
            callbackContext.getUserData().put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_OLD.name(),oldAccountNumber);
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData,callbackContext);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    private void establishLinkage(Check parentCheck, Check childCheck, CheckDao checkDao, LinkageTypeDao linkageTypeDao) {
        childCheck.setParentCheck(parentCheck);
        childCheck.setLinkageType(ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ACCOUNT_NUMBER_CHANGED, linkageTypeDao));
        checkDao.save(childCheck);
    }
}

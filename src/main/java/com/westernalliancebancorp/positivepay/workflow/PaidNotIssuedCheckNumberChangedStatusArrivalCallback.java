package com.westernalliancebancorp.positivepay.workflow;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckLinkageDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import javax.security.auth.login.AccountNotFoundException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.PaidNotIssuedCheckNumberChangedStatusArrivalCallback
 * User: gduggirala
 * Date: 13/4/14
 * Time: 3:30 PM
 */
@Component("paidNotIssuedCheckNumberChanged")
public class PaidNotIssuedCheckNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
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
    ActionDao actionDao;
    @Autowired
    ItemTypeDao itemTypeDao;
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
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into 'Paid not issued' there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        check.setIssuedAmount(referenceData.getAmount());
        check.setIssueDate(referenceData.getPaidDate());
        //When the check number has been changed that means the check number of the reference data also needs to be
        //Corrected.
        Map<String, Object> userData = callbackContext.getUserData();
        String checkNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name());

        //This method will process the recrds in ExceptionalReferenceData, these will check if
        String oldCheckNumber = referenceData.getCheckNumber();
        Long oldAccountId = referenceData.getAccount().getId();
               
        //We are in the process of correcting the Check number, but before that lets correct the
        //Reference data.
        //ModelUtils.getCorrectedReferenceData(referenceData, checkNumber, referenceDataDao);
        referenceData = referenceDataService.correctCheckNumber(referenceData.getId(), checkNumber);
        //Delete the existing check... because its been created by the system
        //and now admin has made the decision that check number in the reference data is wrong
        //So whatever the check detail that we have created against the reference data is wrong.. so delete it.
        try {
            callbackContext.getUserData().put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
            workflowService.performAction(check, "misreadCheckNumber", callbackContext.getUserData());
            try {
                exceptionalReferenceDataService.processDuplicatesInExceptionsReferneceDataWith(oldCheckNumber, oldAccountId);
            } catch (ParseException e) {
                throw new CallbackException(e);
            } catch (AccountNotFoundException e) {
                throw new CallbackException(e);
            }
        } catch (WorkFlowServiceException e) {
            e.printStackTrace();
            throw new CallbackException(e);
        }
        //ModelUtils.markCheckDeleted(check, userData, checkStatusDao, actionDao, checkHistoryDao, workflowManagerFactory);
        //checkDao.update(check);
        //After deleting the data lets search the check_detail table with the new check number and the account number.
        try {
            callbackContext.getUserData().put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_OLD.name(),oldCheckNumber);
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData,callbackContext);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    private void establishLinkage(Check parentCheck, Check childCheck, CheckDao checkDao, LinkageTypeDao linkageTypeDao) {
        childCheck.setParentCheck(parentCheck);
        childCheck.setLinkageType(ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.CHECK_NUMBER_CHANGED, linkageTypeDao));
        checkDao.save(childCheck);
    }
}

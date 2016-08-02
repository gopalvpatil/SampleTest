package com.westernalliancebancorp.positivepay.workflow;

import java.text.ParseException;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckLinkageDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 14/4/14
 * Time: 2:40 PM
 */
@Component("stopPaidCheckNumberChanged")
public class StopPaidCheckNumberChangedStatusArrivalCallback  implements StatusArrivalCallback{
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
    FileUploadUtils fileUploadUtils;
    @Autowired
    ReferenceDataService referenceDataService;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;
    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        if (referenceData == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into Duplicate Paid there must be reference data set, check id :%d", check.getId()));
        }
        //This method will process the records in ExceptionalReferenceData, these will check if
        String oldCheckNumber = referenceData.getCheckNumber();
        Long oldAccountId = referenceData.getAccount().getId();
        try {
            ReferenceData historicalReferenceData = ModelUtils.getReferenceDataFromHistory(check, CheckStatus.STOP_STATUS_NAME, checkStatusDao, checkHistoryDao, workflowDao);
            Map<String, Object> userData = callbackContext.getUserData();
            //As the checks earlier status is "Paid" and paid checks do have reference id associated to them, we have to set the referenceId back to original referenceId from History table.
            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), historicalReferenceData);
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

        Map<String, Object> userData = callbackContext.getUserData();
        String checkNumber = (String) userData.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name());
        referenceData = referenceDataService.correctCheckNumber(referenceData.getId(), checkNumber);
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

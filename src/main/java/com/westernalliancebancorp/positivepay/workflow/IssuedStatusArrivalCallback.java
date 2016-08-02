package com.westernalliancebancorp.positivepay.workflow;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * When a check is moved into "Issued" status by taking appropriate workflow action this class will be called.
 * When this is called the following is the logic that will be executed.
 * 1. First check if the issuedDate is one day older than today, if it is older then proceed else return.
 * 2. Check if the check has any reference data associated with it. When the check has arrived in this status it should not have any reference data associated with it.
 * <p/>
 * User: gduggirala
 * Date: 1/4/14
 * Time: 9:15 PM
 */
@Service("issuedStatusArrivalCallback")
public class IssuedStatusArrivalCallback implements StatusArrivalCallback {
    @Loggable
    Logger logger;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckDao checkDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    AccountDao accountDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        boolean canCheckWithReferenceData = false;
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        try {
            canCheckWithReferenceData = DateUtils.isDateOlderThanToday(check.getIssueDate());
        } catch (ParseException e) {
            throw new CallbackException("Exception while processing the Date in IssuedStatusArrivalCallback", e);
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
                return Boolean.TRUE;
            } else if (referenceDataList.size() > 1) {
                logger.error(String.format("Functionality error cannot expect more than one check matching the criteria of check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                throw new CallbackException("Check with id :" + check.getId() + " and the account id " + check.getAccount().getId() + " Is present more than once on Reference Data, pleae check");
            } else if (referenceDataList.size() == 1) {
                referenceData = referenceDataList.get(0);
                if(!referenceData.getAccount().getId().equals(check.getAccount().getId())) {
                    //Some bug..
                    return Boolean.TRUE;
                }
            }
        } else {
            throw new CallbackException("Functionality Error: When the status is in issued there should not be any reference data associated with the check");
        }
        //We reached here means we should have the reference data in place.
        //As we know that we are going to take either paid / stop status on this check against the reference data we have to set that reference data into userdata map  so that
        //ReferenceDataStatusUpdate post action will use it to set this into check record.

        ReferenceData.ITEM_TYPE item_type = referenceData.getItemType();
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
        if (item_type.equals(ReferenceData.ITEM_TYPE.PAID)) {
            try {
                if (referenceData.getAmount().doubleValue()==check.getIssuedAmount().doubleValue()) {
                    workflowService.performAction(check, "matched", callbackContext.getUserData());
                } else {
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidAmountException);
                    logger.debug("Started Performing workflow action invalidAmountPaid for check id " + check.getId());
                    workflowService.performAction(check, "invalidAmountPaid", callbackContext.getUserData());
                    logger.debug("Completed Performing workflow action invalidAmountStop for check id " + check.getId());
                }
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if (item_type.equals(ReferenceData.ITEM_TYPE.STOP)) {
            try {
            	if (referenceData.getAmount().equals(check.getIssuedAmount())) {
                workflowService.performAction(check, "stop", callbackContext.getUserData());
             } else {
            	 userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidStopAmountException);
                 logger.debug("Started Performing workflow action invalidAmountStop for check id " + check.getId());
                workflowService.performAction(check, "invalidAmountStop", callbackContext.getUserData());
                logger.debug("Completed Performing workflow action invalidAmountStop for check id " + check.getId());
            }
            }
            	catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            	}
        }
        return Boolean.TRUE;
    }
}

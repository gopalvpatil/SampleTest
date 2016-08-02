package com.westernalliancebancorp.positivepay.workflow;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 14/4/14
 * Time: 8:41 AM
 */
@Component("staleStatusArrivalCallback")
public class StaleStatusArrivalCallback implements StatusArrivalCallback {
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
            check.setStaleDate(DateUtils.getWALFormatDate(new Date()));
            checkDao.update(check);
        } catch (ParseException e) {
            throw new CallbackException(e);
        }
        try {
            canCheckWithReferenceData = DateUtils.isDateOlderThanToday(check.getIssueDate());
        } catch (ParseException e) {
            throw new CallbackException("Exception while processing the Date in StaleStatusArrivalCallback", e);
        }
        if (!canCheckWithReferenceData) {
            return Boolean.TRUE;
        }
        ReferenceData referenceData = check.getReferenceData();
        if(referenceData != null) {
            throw new CallbackException(String.format("Functionality error, when check is in this state there should not be any reference id associated with it check id :%d and reference data id %d", check.getId(), referenceData.getId()));
        }
        if (referenceData == null) {
            List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAndAccountIdByStatus(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.STATUS.NOT_PROCESSED);
            if (referenceDataList.isEmpty()) {
                logger.info(String.format("No records found in reference data table, matching the check number %s and the account id %d", check.getCheckNumber(), check.getAccount().getId()));
                return Boolean.TRUE;
            } else if (referenceDataList.size() > 1) {
                logger.error(String.format("Functionality error cannot expect more than one check matching the criteria of check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                throw new CallbackException("Check with id :" + check.getId() + " and the account id " + check.getAccount().getId() + " Is present more than once on Reference Data, pleae check");
            } else if (referenceDataList.size() == 1) {
                referenceData = referenceDataList.get(0);
                if (!referenceData.getAccount().getId().equals(check.getAccount().getId())) {
                    //Some bug..
                    return Boolean.TRUE;
                }
            }
        } else {
            throw new CallbackException("Functionality Error: When the status is in Stale there should not be any reference data associated with the check");
        }
        //We reached here means we should have the reference data in place.
        //As we know that we are going to take either paid / stop status on this check against the reference data we have to set that reference data into userdata map  so that
        //ReferenceDataStatusUpdate post action will use it to set this into check record.

        ReferenceData.ITEM_TYPE item_type = referenceData.getItemType();
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
        if (item_type.equals(ReferenceData.ITEM_TYPE.PAID)) {
            try {
                workflowService.performAction(check, "stalePaid", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if (item_type.equals(ReferenceData.ITEM_TYPE.STOP)) {
            try {
                workflowService.performAction(check, "staleStop", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        }
        return Boolean.TRUE;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.service.ReferenceDataCreationService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * This class is configured to call when a check has arrived into this "Stop" status.
 * When a check arrives into this status it is assumed that referece_data_id of this check is populated if it is not its a functionality error.
 * Whenever a check arrives into this status first the issue date will be checked if the issue data is older than today then we will go check the reference data.
 * Incase we don't find any thing in ther refernce data with check number and the account id then we return true with the assumtion that there are not misread checks.
 * If we find more than one reference_data ids then it is a functionality error as it is assumed that duplicate checks are not present in the then reference_data table.
 * If we find only one check with the same check number and the account id then
 *  if the reference data item type is "Paid" then we move it into new status called "Stop, Paid" by taking the action "paid"
 *  if the reference data item type is "Stop" then we move it into new status called "Duplicate Stop" by taking the action "duplicateStop".
 * Once it has bee
 * User: Moumita Ghosh
 * Date: 1/4/14
 * Time: 9:15 PM
 */
@Service("stopStatusArrivalCallback")
public class StopStatusArrivalCallback implements StatusArrivalCallback {
    @Loggable
    Logger logger;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckDao checkDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        boolean canCheckWithReferenceData = false;
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        try {
            canCheckWithReferenceData = DateUtils.isDateOlderThanToday(check.getIssueDate()==null?check.getVoidDate():check.getIssueDate());
        } catch (ParseException e) {
            throw new CallbackException("Exception while processing the Date in StopStatusArrivalCallback",e);
        }
        if (!canCheckWithReferenceData) {
            return Boolean.TRUE;
        }
        ReferenceData referenceData = check.getReferenceData();
        if (referenceData == null) {
            /** Query the database to find if a matching reference data exists with same account and check number for STOP item type having NOT_PROCESSED status **/
            List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP, ReferenceData.STATUS.NOT_PROCESSED);
            if (matchedReferenceDataList.isEmpty()) {
                /** if the current status is 'issued' or 'void' or 'stale' or 'stopAfterVoid' create  the reference data and attach to check : WALPP-259 **/
                if ((callbackContext.getCurrenStatusName().equals(CheckStatus.STALE_STATUS_NAME)) ||
                        (callbackContext.getCurrenStatusName().equals(CheckStatus.VOID_STATUS_NAME)) ||
                        (callbackContext.getCurrenStatusName().equals(CheckStatus.ISSUED_STATUS_NAME) ||
                                (callbackContext.getCurrenStatusName().equals(CheckStatus.STOP_AFTER_VOID)))) {
                    referenceData = referenceDataCreationService.createNewReferenceDataForCheck(check);
                    check.setReferenceData(referenceData);
                    callbackContext.getCheckHistory().setReferenceData(referenceData);
                    checkHistoryDao.update(callbackContext.getCheckHistory());
                    checkDao.update(check);
                } else {
                    logger.error(String.format("Functionality error expected at least one reference data object for check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                    throw new CallbackException("Check with id :" + check.getId() + " is in the 'Stop' status without reference id, please check");
                }
            } else {
                referenceData = matchedReferenceDataList.get(0);
                referenceData.setStatus(ReferenceData.STATUS.PROCESSED);
                referenceDataDao.update(referenceData);
                check.setReferenceData(referenceData);
                checkDao.update(check);
            }
        }
        List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAndAccountIdByStatus(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.STATUS.NOT_PROCESSED);
        if (referenceDataList.isEmpty()) {
            logger.info(String.format("No records found in reference data table, matching the check number %s and the account id %d", check.getCheckNumber(), check.getAccount().getId()));
            return Boolean.TRUE;
        } else if (referenceDataList.size() > 1) {
            logger.error(String.format("Functionality error cannot expect more than one check matching the criteria of check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
            throw new CallbackException(String.format("More than one item in reference data is found with the status not processed and matching check id %d and account id %d functionality error?", check.getId(), check.getAccount().getId()));
        } else if (referenceDataList.size() == 1) {
            referenceData = referenceDataList.get(0);
            if (!referenceData.getAccount().getId().equals(check.getAccount().getId())) {
                //Some bug..
                return Boolean.TRUE;
            }
        }
        referenceData = referenceDataDao.findById(referenceData.getId());
        ReferenceData.ITEM_TYPE item_type = referenceData.getItemType();
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
        if (item_type.equals(ReferenceData.ITEM_TYPE.PAID)) {
            try {
                workflowService.performAction(check, "stopPaid", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if(item_type.equals(ReferenceData.ITEM_TYPE.STOP)) {
            try {
                workflowService.performAction(check, "duplicateStop", callbackContext.getUserData());
                 } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        }
        return Boolean.TRUE;
    }
}

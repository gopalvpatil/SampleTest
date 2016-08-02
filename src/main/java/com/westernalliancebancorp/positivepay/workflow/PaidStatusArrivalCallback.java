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
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.Workflow;
import com.westernalliancebancorp.positivepay.service.ReferenceDataCreationService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * This class will be called when a check has arrived to the status "Paid" this class will check if there is any other check existing in reference data
 * with the same checkid and the account id and the status as not processed.
 *
 * The intention of this class is to move the check into duplicate paid exception incase if there are any check's existing in the reference data table with status "Not Processed"
 * User: Moumita Ghosh
 * Date: 1/4/14
 * Time: 9:15 PM
 */
@Service("paidStatusArrivalCallback")
public class PaidStatusArrivalCallback implements StatusArrivalCallback {
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
    WorkflowDao workflowDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        boolean canCheckWithReferenceData = false;
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        Workflow workflow = workflowDao.findById(check.getWorkflow().getId());
        CheckStatus currentStatus = checkStatusDao.findByNameAndVersion(callbackContext.getCurrenStatusName(), workflow.getVersion());
        String action = callbackContext.getActionNameToPerform();
        if (!currentStatus.getExceptionalStatus()||currentStatus.getName().equalsIgnoreCase(CheckStatus.STALE_PAID)||
        	(currentStatus.getName().equalsIgnoreCase(CheckStatus.INVALID_AMOUNT_PAID) && action.equals(Action.ACTION_NAME.ADJUST_AMOUNT_PAID.getName()))||
        	(currentStatus.getName().equalsIgnoreCase(CheckStatus.INVALID_AMOUNT_PAID) && action.equals(Action.ACTION_NAME.ADJUST_AMOUNT_ISSUED.getName()))) {
            check.setMatchStatus("MATCHED");
            checkDao.update(check);
        }else{
            check.setMatchStatus("UNMATCHED");
            checkDao.update(check);
        }
        if(check.getIssueDate()==null)/**Special case for paidNotIssued */
        {
            check.setIssuedAmount(check.getReferenceData().getAmount()); 
            check.setIssueDate(check.getReferenceData().getPaidDate());
            checkDao.update(check);
        }
        try {
            canCheckWithReferenceData = DateUtils.isDateOlderThanToday(check.getIssueDate());
        } catch (ParseException e) {
            throw new CallbackException("Exception while processing the Date in PaidStatusArrivalCallback", e);
        }
        if (!canCheckWithReferenceData) {
            return Boolean.TRUE;
        }
        ReferenceData referenceData = check.getReferenceData();
        if (referenceData == null) {
            /** Query the database to find if a matching reference data exists with same account and check number for STOP item type having NOT_PROCESSED status **/
            List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.ITEM_TYPE.PAID, ReferenceData.STATUS.NOT_PROCESSED);
            if (matchedReferenceDataList.isEmpty()) {
                /** if the current status is 'issued' or 'void' create  the reference data and attach to check : WALPP-259 **/
                if ((callbackContext.getCurrenStatusName().equals(CheckStatus.VOID_STATUS_NAME)) || (callbackContext.getCurrenStatusName().equals(CheckStatus.ISSUED_STATUS_NAME))) {
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
            logger.info(String.format("No records found in reference data table, matching the check number %s and the account id %d to move the check into exceptional state namely \"Duplicate Paid \" ", check.getCheckNumber(), check.getAccount().getId()));
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
                workflowService.performAction(check, "duplicatePaid", callbackContext.getUserData());
            } catch (WorkFlowServiceException e) {
                throw new CallbackException(e);
            }
        } else if (item_type.equals(ReferenceData.ITEM_TYPE.STOP)) {
        	 try {
                 workflowService.performAction(check, "stopAfterPaid", callbackContext.getUserData());
             } catch (WorkFlowServiceException e) {
                 throw new CallbackException(e);
             }
        }
        return Boolean.TRUE;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * The check will arrive into this status on the following condition.
 * 1. The check has been issued as "Paid" first
 * 2. And then the check has been re-issued as "Void"
 * 3. Now as the per business requirement this will be moved into "voidAfterPaid" status and again the check has
 * to be moved into "void" status automatically - Reference - https://intraedge.atlassian.net/browse/WALPP-40
 *
 * User: Moumita Ghosh
 * Date: 18/6/14
 * Time: 12:45 PM
 */
@Service("voidAfterPaidStatusArrivalAndDepartureCallback")
public class VoidAfterPaidStatusArrivalAndDepartureCallback implements StatusArrivalCallback,StatusDepartureCallback {
    @Loggable
    Logger logger;
    @Autowired
    CheckDao checkDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.SequenceException_VoidAfterPaid);
        check.setExceptionType(exceptionType);
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name(), Boolean.TRUE);
        try {
            workflowService.performAction(check, "noPay", userData);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean executeStatusDepartureCallback(CallbackContext callbackContext)
            throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        Check manualEntryCheck = (Check) callbackContext.getUserData().get(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name());
        if(manualEntryCheck != null)
        {
            check.setVoidDate(manualEntryCheck.getVoidDate());
            check.setVoidAmount(manualEntryCheck.getVoidAmount());
            callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name());
        }
        else /* This exception might be triggered from SequenceExceptionResolver */
        {
            ExceptionalCheck exCheck = (ExceptionalCheck)callbackContext.getUserData().get(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name());
            try {
		check.setVoidDate(DateUtils.getDateFromString(StringUtils.trim(exCheck.getIssueDate())));
	    } catch (ParseException e) {
		throw new CallbackException(e);
	    }
            check.setVoidAmount(new BigDecimal(StringUtils.trim(exCheck.getIssuedAmount())));
            callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name());
        }
        check.setExceptionType(null);
        check.setExceptionResolvedDate(new Date());
        checkDao.update(check);
        return Boolean.TRUE;
    }
}

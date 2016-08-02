package com.westernalliancebancorp.positivepay.workflow;

import java.util.Date;
import java.util.Map;

import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
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
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * The check will arrive into this status on the following condition.
 * 1. The check has been issued as "void" first
 * 2. And then the check has been re-issued as "stop"
 * 3. Now as the per business requirement this will be moved into "StopAfterVoid" status and again the check has
 * to be moved into "stop" status automatically - Reference - https://intraedge.atlassian.net/browse/WALPP-161
 *
 * User: Moumita Ghosh
 * Date: 19/6/14
 * Time: 1:00 PM
 */
@Service("stopAfterVoidStatusArrivalAndDepartureCallback")
public class StopAfterVoidStatusArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {
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
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.SequenceException_StopAfterVoid);
        check.setExceptionType(exceptionType);
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
        Map<String, Object> userData = callbackContext.getUserData();
        try {
            workflowService.performAction(check, "stop", userData);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean executeStatusDepartureCallback(CallbackContext callbackContext)
	    throws CallbackException {
	Check check = checkDao.findById(callbackContext.getCheck().getId());
	 Map<String, Object> map = callbackContext.getUserData();
        ReferenceData manualEntryReferenceData = (ReferenceData) map.get(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_REFERENCE_DATA.name());
        if(manualEntryReferenceData != null)
        {
            check.setIssueDate(manualEntryReferenceData.getStopDate());
            check.setIssuedAmount(manualEntryReferenceData.getAmount());
            callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_REFERENCE_DATA.name());
        }
        check.setExceptionType(null);
        check.setExceptionResolvedDate(new Date());
        checkDao.update(check);
        return Boolean.TRUE;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;

import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The check will arrive into this status on the following condition.
 * 1. The check has been issued as "Stop" first
 * 2. And then the check has been re-issued as "Issued"
 * 3. Now as the per business requirement this will be moved into "issuedAfterStop" status and again the check has
 * to be moved into "issued" status automatically - Reference - https://intraedge.atlassian.net/browse/WALPP-41
 *
 * User: Moumita Ghosh
 * Date: 16/6/14
 * Time: 1:00 PM
 */
@Service("issuedAfterStopStatusArrivalAndDepartureCallback")
public class IssuedAfterStopStatusArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {
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
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name(), Boolean.TRUE);
        ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.SequenceException_IssuedAfterStop);
        check.setExceptionType(exceptionType);
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
        /** IssuedAfterStop needs to be auto resolved to issued ( as per WALPP-38), We are setting MARK_PROCESSED, to avoid the reference data record getting picked up by 
         * IssuedStatusArrivalCallback and resulting in stop sate
         */
        try {
            workflowService.performAction(check, "issued", userData);
        } catch (WorkFlowServiceException e) {
            throw new CallbackException(e);
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean executeStatusDepartureCallback(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        check.setExceptionType(null);
        check.setExceptionResolvedDate(new Date());
        checkDao.update(check);
        return Boolean.TRUE;
    }
}

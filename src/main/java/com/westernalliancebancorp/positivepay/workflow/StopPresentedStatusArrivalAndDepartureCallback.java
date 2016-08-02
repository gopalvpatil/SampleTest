package com.westernalliancebancorp.positivepay.workflow;

import java.util.Date;
import java.util.Map;

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
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;

/**
 *
 * User: Moumita Ghosh
 * Date: 18/7/14
 * Time: 12:45 PM
 */
@Service("stopPresentedStatusArrivalAndDepartureCallback")
public class StopPresentedStatusArrivalAndDepartureCallback implements StatusArrivalCallback,StatusDepartureCallback {
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
        ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.StopPresentedException);
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
        check.setExceptionType(null);
        check.setExceptionResolvedDate(new Date());
        checkDao.update(check);
        return Boolean.TRUE;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * User: Moumita Ghosh
 * Date: 16/6/14
 * Time: 5:09 PM
 */
@Component("stopNotIssuedStatusArrivalAndDepartureCallback")
public class StopNotIssuedStatusArrivalAndDepartureCallback implements StatusArrivalCallback,StatusDepartureCallback {
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
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
    	Check check = checkDao.findById(callbackContext.getCheck().getId());
		try {
			ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.StopNotIssuedException);
	        check.setExceptionType(exceptionType);
            check.setExceptionCreationDate(new Date());
	        checkDao.update(check);
			workflowService.performAction(callbackContext.getCheck(), "stop",
					callbackContext.getUserData());
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

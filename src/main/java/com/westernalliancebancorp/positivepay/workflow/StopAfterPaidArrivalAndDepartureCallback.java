package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import java.util.Date;


@Service("stopAfterPaidStatusArrivalAndDepartureCallback")
public class StopAfterPaidArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {
    @Loggable
    Logger logger;
    @Autowired
    CheckDao checkDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.SequenceException_StopAfterPaid);
        check.setExceptionType(exceptionType);
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
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

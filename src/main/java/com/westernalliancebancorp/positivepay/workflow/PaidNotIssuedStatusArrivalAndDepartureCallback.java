package com.westernalliancebancorp.positivepay.workflow;

import java.util.Date;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;

/**
 * User: gduggirala
 * Date: 13/4/14
 * Time: 3:01 PM
 */
@Component("paidNotIssuedStatusArrivalAndDepartureCallback")
public class PaidNotIssuedStatusArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {
    @Loggable
    Logger logger;
    @Autowired
    CheckDao checkDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into 'Paid not issued' there must be reference data set, check id :%d", check.getId()));
        }
        check.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.PaidNotIssuedException));
        check.setExceptionCreationDate(new Date());
        /** check in paidNotIssued state should not have issued date and amount-(WALPP-431)**/
	check.setIssuedAmount(null);    
	check.setIssueDate(null);
        checkDao.update(check);
        return Boolean.TRUE;
    }
	@Override
	public boolean executeStatusDepartureCallback(CallbackContext callbackContext)throws CallbackException {
	Check check = checkDao.findById(callbackContext.getCheck().getId());
	check.setExceptionType(null);
	check.setExceptionResolvedDate(new Date());
	checkDao.update(check);
	return Boolean.TRUE;
	}
}

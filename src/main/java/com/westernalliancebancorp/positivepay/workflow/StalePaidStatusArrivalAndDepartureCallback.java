package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.service.ReferenceDataCreationService;

import java.util.Date;

/**
 * User: moumita
 * Date: 24/6/14
 * Time: 3:01 PM
 */
@Component("stalePaidStatusArrivalAndDepartureCallback")
public class StalePaidStatusArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {

    @Autowired
    CheckDao checkDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;
    @Autowired
    ExceptionTypeService exceptionTypeService;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        if (check.getReferenceData() == null) {
            ReferenceData referenceData = referenceDataCreationService.createNewReferenceDataForCheck(check);
            check.setReferenceData(referenceData);
            checkDao.update(check);
           // throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into 'stalePaid' there must be reference data set, check id :%d", check.getId()));
        }
        check.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.StalePaidException));
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
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

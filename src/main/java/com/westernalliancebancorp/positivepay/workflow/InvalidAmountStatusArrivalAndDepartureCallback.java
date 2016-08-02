package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * User: gduggirala
 * Date: 20/6/14
 * Time: 5:13 AM
 */
@Component("invalidAmountStatusArrivalAndDepartureCallback")
public class InvalidAmountStatusArrivalAndDepartureCallback implements StatusArrivalCallback, StatusDepartureCallback {
    @Loggable
    Logger logger;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    WorkflowDao workflowDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    CheckLinkageDao checkLinkageDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    LinkageTypeDao linkageTypeDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ReferenceDataService referenceDataService;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        ExceptionType.EXCEPTION_TYPE exceptionTypeToSet = (ExceptionType.EXCEPTION_TYPE)callbackContext.getUserData().get(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name());
        if(exceptionTypeToSet != null){
            ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(exceptionTypeToSet);
            //ModelUtils.retrieveOrCreateExceptionType(exceptionTypeToSet, exceptionTypeDao);
            check.setExceptionType(exceptionType);
            check.setExceptionCreationDate(new Date());
            checkDao.update(check);
            callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name());
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean executeStatusDepartureCallback(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        check.setExceptionType(null);
        check.setExceptionResolvedDate(new Date());
        checkDao.update(check);
        return Boolean.TRUE;
    }
}

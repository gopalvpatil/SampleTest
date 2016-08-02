package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

/**
 * This class is configured to become active when a check has arrived into the status "voidStopCheckNumberChanged".
 * There will be two checks.
 * 1. Original check which has been moved to "Void, Stop" status because another check with the same check number and the account number is appeared in the "Stop" list.
 * 2. A check which has been created as a result of entering the new check number
 * <p/>
 * Check 1 will moved back to its original status "Void", during this process of moving it back to its original status the following should be done.
 * Reference id should be set to null as it is moved to "Void, stop" against some reference_id
 * <p/>
 * Check 2 will take a different route.
 * First we check if the check is already existing in the check_detail table with the status "Issued"
 * if found and a reference item is existing in reference_data table with the status "NOT_PROCESSED" then move it to paid status. During this move make sure that the
 * check will have the reference_id populated
 * If check 2 is not found then moved it to "Stop, not issued" status, during this move make sure that the check will have the reference_id populated.
 * <p/>
 * There is one more method which will try to correct the reference_id the reason..
 * When a check image is read by software it might have misread the check, so when the customer looks at the image and when he corrects the check number the data in the
 * reference_data should also be corrected.
 * <p/>
 * So the method "getCorrectReferenceData" will try to correct the referenceData record.
 * <p/>
 * User: gduggirala
 * Date: 3/4/14
 * Time: 12:38 PM
 */
@Deprecated
@Component("voidStopCurrentCheckNumberChanged")
public class VoidStopCurrentCheckNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
    @Loggable
    Logger logger;
    @Autowired
    WorkflowUtil workflowUtil;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        return workflowUtil.changeCurrentCheckNumber(callbackContext);
    }

}

package com.westernalliancebancorp.positivepay.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;

/**
 * User: gduggirala
 * Date: 11/4/14
 * Time: 9:29 PM
 */
@Deprecated
@Component("duplicateStopCurrentCheckNumberChanged")
public class DuplicateStopCurrentCheckNumberChangedStatusArrivalCallback implements StatusArrivalCallback {
    @Autowired
    WorkflowUtil workflowUtil;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        return workflowUtil.changeCurrentCheckNumber(callbackContext);
    }
}

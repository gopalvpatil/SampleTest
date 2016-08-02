package com.westernalliancebancorp.positivepay.workflow;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.log.Loggable;

/**
 * User: gduggirala
 * Date: 10/4/14
 * Time: 2:54 PM
 */
@Component("invalidAmountCurrentAccountNumberChanged")
public class InvalidAmountCurrentAccountNumberChangedStatusArrival implements StatusArrivalCallback {
    @Loggable
    Logger logger;

    @Autowired
    WorkflowUtil workflowUtil;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        return workflowUtil.changeCurrentAccountNumber(callbackContext);
    }
}

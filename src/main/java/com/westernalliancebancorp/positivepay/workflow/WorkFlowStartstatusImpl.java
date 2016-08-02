package com.westernalliancebancorp.positivepay.workflow;

import org.springframework.stereotype.Service;

/**
 * WorkFlowStartstatusImpl is
 *
 * @author Giridhar Duggirala
 */

@Service("workFlowStartstatusImpl")
public class WorkFlowStartstatusImpl implements StatusArrivalCallback {
    @Override
    public boolean executeOnStatusArrival(CallbackContext callbackContext) {
        System.out.println("This is executed");
        return Boolean.TRUE;
    }
}

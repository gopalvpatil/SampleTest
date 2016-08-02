package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * In case of "VoidAfterPaid" status and the customer has taken the decision to nopay then we will move the check back into
 * "Void" status while creating the "adjustment amount" and when the check is in the void status there shouldn't be any
 * reference data associated with it.
 * User: gduggirala
 * Date: 14/5/14
 * Time: 11:46 AM
 */
@Component("adjustAmountNotMatchingAndReferenceDataNullifier")
public class AdjustAmountNotMatchingAndReferenceDataNullifierActionCallback implements PreActionCallback, PostActionCallback {
    @Autowired
    PreActionCallback adjustAmountNotMatching;

    @Autowired
    PreActionCallback referenceDataStatusUpdate;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean executePostActionCallback(CallbackContext callbackContext) throws CallbackException {
        return executePreActionCallback(callbackContext);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean executePreActionCallback(CallbackContext callbackContext) throws CallbackException {
        adjustAmountNotMatching.executePreActionCallback(callbackContext);
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
        userData.put(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name(), Boolean.TRUE);
        return referenceDataStatusUpdate.executePreActionCallback(callbackContext);
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Special cases VoidStop and VoidPaid. Check is issued as void but it either got Paid or Stopped customer can either take
 * "Pay" or "No Pay" and "Stop" or "Ignore" upon taking the action "No pay" or "Ignore" the check will be moved into "Void" status and
 * as checks in "Void" status should not have any reference data associated we will nullify the reference data column.
 * User: gduggirala
 * Date: 14/5/14
 * Time: 11:46 AM
 */
@Service("referenceDataNullifierAndMarkProcessed")
public class ReferenceDataNullifierAndMarkProcessedActionCallback implements PreActionCallback, PostActionCallback {

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
        Map<String, Object> userData = callbackContext.getUserData();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.SET_NULL_REFERENCE_ID.name(), Boolean.TRUE);
        userData.put(WorkflowService.STANDARD_MAP_KEYS.MARK_PROCESSED.name(), Boolean.TRUE);
        return referenceDataStatusUpdate.executePreActionCallback(callbackContext);
    }
}

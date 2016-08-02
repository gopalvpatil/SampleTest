package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;

import java.util.Map;

/**
 * WorkflowService is
 *
 * @author Giridhar Duggirala
 */

public interface WorkflowService {
    String performAction(Long checkId, String actionNameToPerform, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException;
    String performAction(Check check, String actionNameToPerform, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException;
    String forceStatusChange(Long checkId, String targetStatusName, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException;
    String forceStatusChange(Check check, String targetStatusName, Map<String, Object> userData) throws WorkFlowServiceException, CallbackException;
    boolean canPerformAction(Check check, String actionNameToPerform, WorkflowManager workflowManager);
    enum STANDARD_MAP_KEYS {
        USER_COMMENT, SYSTEM_COMMENT, ACCOUNT_NUMBER_NEW,CHECK_NUMBER_NEW,
        BANK_ID, FORCE_STATUS_CHANGE, REFERENCE_ID, REFERENCE_DATA,
        SET_NULL_REFERENCE_ID, CHECK_DTO,EXECUTION_SEQUENCE, CALLED_BY, CALLED_FOR_ACTION,MARK_PROCESSED, INVALID_AMOUNT_EXCEPTION_TO_SET, MANUAL_ENTRY_ISSUED_CHECK,
        MANUAL_ENTRY_REFERENCE_DATA, EXCEPTION_CHECK, CHECK_NUMBER_OLD, ACCOUNT_NUMBER_OLD;
    }
}

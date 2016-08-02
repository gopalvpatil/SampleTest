package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.WorkflowService;

import java.util.Map;

/**
 * CallbackContext is
 *
 * @author Giridhar Duggirala
 */

public final class CallbackContext {
    private final Check check;
    private final String actionNameToPerform;
    private final Map<String, Object> userData;
    private final WorkflowManager workflowManager;
    private final String targetStatusName;
    private String executionSequence = "";
    private final String currenStatusName;
    private CheckHistory checkHistory;

    public CallbackContext(Check check, String actionNameToPerform, Map<String, Object> userData, WorkflowManager workflowManager, String currenStatusName) {
        this.check = check;
        this.actionNameToPerform = actionNameToPerform;
        this.userData = userData;
        this.workflowManager = workflowManager;
        this.currenStatusName = currenStatusName;
        this.targetStatusName = null;
    }

    public CallbackContext(Check check, Map<String, Object> userData, WorkflowManager workflowManager, String targetStatusName, String currenStatusName) {
        this.check = check;
        this.targetStatusName = targetStatusName;
        this.userData = userData;
        this.workflowManager = workflowManager;
        this.currenStatusName = currenStatusName;
        this.actionNameToPerform = null;
    }

    public void addToExecutionSequence(String className) {
        executionSequence = executionSequence + className + ", ";
    }

    public String getExecutionSequence() {
        return executionSequence;
    }

    public Check getCheck() {
        return check;
    }

    public String getActionNameToPerform() {
        return actionNameToPerform;
    }

    public Map<String, Object> getUserData() {
        return userData;
    }

    public String getCurrenStatusName() {
        return currenStatusName;
    }

    public String getTargetStatusName() {
        return targetStatusName;
    }

    public CheckHistory getCheckHistory() {
        return checkHistory;
    }

    public void setCheckHistory(CheckHistory checkHistory) {
        this.checkHistory = checkHistory;
    }

    @Override
    public String toString() {
        return "CallbackContext{" +
                "check=" + check.getId() +
                ", accountId=" + check.getAccount().getId() +
                ", actionNameToPerform='" + actionNameToPerform + '\'' +
                ", workflowManager=" + workflowManager +
                ", targetStatusName='" + targetStatusName + '\'' +
                ", executionSequence='" + executionSequence + '\'' +
                ", userData [" + userDataToString() + " ]" +
                '}';
    }

    public String userDataToString() {
        String toReturn = "{";
        if (userData != null) {
            for (String key : userData.keySet()) {
                Object value = key;
                if (key.equals(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name()) && value != null) {
                    toReturn = toReturn + "\'" + key + "\'" + ":" + "\'" + ((ReferenceData) userData.get(key)).getId() + "\'";
                } else {
                    toReturn = toReturn + "\'" + key + "\'" + ":" + "\'" + userData.get(key) + "\'";
                }
            }
            toReturn = toReturn + "}";
        }
        return toReturn;
    }
}

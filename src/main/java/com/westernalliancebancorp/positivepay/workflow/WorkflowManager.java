package com.westernalliancebancorp.positivepay.workflow;

import java.util.List;
import java.util.Map;

/**
 * WorkflowManager is
 *
 * @author Giridhar Duggirala
 */

public interface WorkflowManager {
    Map<String,String> getActionsForStatus(String statusName);
    Map<String,String> getAdminActionsForStatus(String statusName);
    Map<String,String> getNonAdminActionsForStatus(String statusName);
    List<String> getStatusNames();
    String getActionDescription(String statusName, String actionName);
    String getStatusDescription(String statusName);
    boolean isExceptionalStatus(String statusName);
    List<String> getExceptionStatusNames();
    String getTargetStatusName(String action, String statusName);
    Integer getSupportedVersion();
    String getOnStatusArrivalCallback(String statusName);
    String getOnStatusDepatureCallbak(String statusName);
    String getPreActionExecutionCallback(String actionName, String statusName);
    String getPostActionExecutionCallback(String actionName, String statusName);
    boolean isAnActionAction(String actionName, String statusName);

    String getPaymentStatus(String actionName, String statusName);

    Map<String, String> getPresentableActionsForStatus(String statusName);
    public boolean isAutoResolvingStatus(String statusName);
}

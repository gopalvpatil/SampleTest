package com.westernalliancebancorp.positivepay.workflow;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import ch.lambdaj.function.matcher.LambdaJMatcher;

import com.westernalliancebancorp.positivepay.jaxb.v1.Action;
import com.westernalliancebancorp.positivepay.jaxb.v1.Status;
import com.westernalliancebancorp.positivepay.model.Workflow;

/**
 * WorkflowManagerImpl is
 *
 * @author Giridhar Duggirala
 */
public class WorkflowManagerImpl implements WorkflowManager {
    private Workflow workflowModel;
    public static final Integer supportedVersion = new Integer(1);
    private Jaxb2Marshaller jaxb2Marshaller;
    private List<String> errorsList = null;
    private Set<String> errorStatus = null;
    private com.westernalliancebancorp.positivepay.jaxb.v1.Workflow workflow;

    public WorkflowManagerImpl(Workflow workflow, Jaxb2Marshaller jaxb2Marshaller) {
        this.workflowModel = workflow;
        this.jaxb2Marshaller = jaxb2Marshaller;
        if (workflow.getVersion().equals(supportedVersion)) {
            initiate();
        }
    }

    @Override
    public List<String> getStatusNames() {
        List<Status> statusList = workflow.getStatus();
        return extract(statusList, on(Status.class).getName());
    }

    public boolean isValid() {
        List<Status> statusList = workflow.getStatus();
        List<Status> sList = new ArrayList<Status>(statusList);
        errorsList = new ArrayList<String>();
        for (Status status : statusList) {
            System.out.println("-Status description : " + status.getDescription() + " name : " + status.getName());
            for (Action action : status.getAction()) {
                System.out.println("--Action description: " + action.getDescription() + " name : " + action.getName() + " target status name: " + action.getTargetStatusName());
                String error = checkTargetStatusIsStatus(sList, action.getTargetStatusName());
                if (error != null && !error.isEmpty()) {
                    errorsList.add(error);
                }
            }
        }
        errorStatus = checkUniqueStatusNames(statusList);
        if (!errorStatus.isEmpty() || !errorsList.isEmpty()) {
            for (String error : errorsList) {
                System.out.println(error);
            }
            for (String error : errorStatus) {
                System.out.println(error);
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Set<String> checkUniqueStatusNames(List<Status> statusList) {
        Set<String> statuses = new HashSet<String>();
        Set<String> duplicateStatus = new HashSet<String>();
        for (Status status : statusList) {
            if (!statuses.add(status.getName())) {
                duplicateStatus.add("Status tag with name " + status.getName() + " is already existing");
            }
        }
        return duplicateStatus;
    }

    private String checkTargetStatusIsStatus(List<Status> sList, String targetStatusName) {
        for (Status status : sList) {
            if (targetStatusName.equals(status.getName())) {
                return null;
            }
        }
        return "Target Status name " + targetStatusName + " is missing in the status tag";
    }

    @Override
    public String getActionDescription(String statusName, String actionName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        return action.getDescription();
    }

    @Override
    public String getStatusDescription(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        return status.getDescription();
    }

    private void initiate() {
        String workflowXml = workflowModel.getXml();
        InputStream inputStream = new ByteArrayInputStream(workflowXml.getBytes());
        workflow = (com.westernalliancebancorp.positivepay.jaxb.v1.Workflow) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
        if (!isValid()) {
            StringBuffer stringBuffer = new StringBuffer();
            if (errorsList != null) {
                for (String errorString : errorsList) {
                    stringBuffer.append(errorString);
                    stringBuffer.append("\n");
                }
            }

            if (errorStatus != null) {
                for (String errorString : errorStatus) {
                    stringBuffer.append(errorString);
                    stringBuffer.append("\n");
                }
            }
            throw new InvalidWorkflowException(stringBuffer.toString());
        }
    }

    /**
     * Returns the Map with name as key's and description as values.
     * statusName: name of the status and not description.
     *
     * @param statusName
     * @return
     */
    @Override
    public Map<String, String> getActionsForStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Map<String, String> actionsToReturn = new HashMap<String, String>();
        for (Status status : statuses) {
            if (status.getName().equals(statusName)) {
                List<Action> actionsList = status.getAction();
                for (Action action : actionsList) {
                    actionsToReturn.put(action.getName(), action.getDescription());
                }
                break;
            }
        }
        return actionsToReturn;
    }

    @Override
    public Map<String, String> getPresentableActionsForStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Map<String, String> actionsToReturn = new HashMap<String, String>();
        for (Status status : statuses) {
            if (status.getName().equals(statusName)) {
                List<Action> actionsList = status.getAction();
                for (Action action : actionsList) {
                    if (action.isIsPresentable()) {
                        actionsToReturn.put(action.getName(), action.getDescription());
                    }
                }
                break;
            }
        }
        return actionsToReturn;
    }

    @Override
    public Map<String, String> getAdminActionsForStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Map<String, String> actionsToReturn = new HashMap<String, String>();
        for (Status status : statuses) {
            if (status.getName().equals(statusName)) {
                List<Action> actionsList = status.getAction();
                for (Action action : actionsList) {
                    if (action.isIsAdminAction() != null && action.isIsAdminAction()) {
                        if (action.isIsPresentable()) {
                            actionsToReturn.put(action.getName(), action.getDescription());
                        }
                    }
                }
                break;
            }
        }
        return actionsToReturn;
    }

    @Override
    public Map<String, String> getNonAdminActionsForStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Map<String, String> actionsToReturn = new HashMap<String, String>();
        for (Status status : statuses) {
            if (status.getName().equals(statusName)) {
                List<Action> actionsList = status.getAction();
                for (Action action : actionsList) {
                    if (action.isIsAdminAction() == null || !action.isIsAdminAction()) {
                        if (action.isIsPresentable()) {
                            actionsToReturn.put(action.getName(), action.getDescription());
                        }
                    }
                }
                break;
            }
        }
        return actionsToReturn;
    }

    @Override
    public boolean isExceptionalStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        for (Status status : statuses) {
            if (statusName.equals(status.getName())) {
                if (status.isIsExceptionalStatus() == null) {
                    return Boolean.FALSE;
                } else {
                    return status.isIsExceptionalStatus();
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public List<String> getExceptionStatusNames() {
        List<Status> statuses = workflow.getStatus();
        return extract(
                select(statuses, having(on(Status.class).isIsExceptionalStatus(), new ExceptionalStateMatcher(Boolean.TRUE))),
                on(Status.class).getName());
    }

    @Override
    public String getTargetStatusName(String actionName, String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        return action.getTargetStatusName();
    }

    @Override
    public String getOnStatusArrivalCallback(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        return status.getOnArrivalCallback();
    }

    @Override
    public String getOnStatusDepatureCallbak(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        return status.getOnDepartureCallback();
    }

    @Override
    public String getPreActionExecutionCallback(String actionName, String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        if (action != null) {
            return action.getPreExecutionCallback();
        }
        return null;
    }

    @Override
    public String getPaymentStatus(String actionName, String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        if (action != null) {
            return action.getPaymentStatus();
        }
        return null;
    }

    @Override
    public boolean isAutoResolvingStatus(String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses,having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        List<Action> actions = status.getAction();
        if(actions.isEmpty() || actions.size() <=1){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    @Override
    public String getPostActionExecutionCallback(String actionName, String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        return action.getPostExecutionCallback();
    }

    @Override
    public Integer getSupportedVersion() {
        return supportedVersion;
    }

    @Override
    public boolean isAnActionAction(String actionName, String statusName) {
        List<Status> statuses = workflow.getStatus();
        Status status = selectFirst(statuses, having(on(Status.class).getName(), new StatusNameMatcher(statusName)));
        Action action = selectFirst(status.getAction(), having(on(Action.class).getName(), new ActionNameMatcher(actionName)));
        if (action.isIsAdminAction() == null) {
            return Boolean.FALSE;
        } else {
            return action.isIsAdminAction();
        }
    }

    class ExceptionalStateMatcher extends LambdaJMatcher<Status> {
        private boolean exceptionState;

        public ExceptionalStateMatcher(boolean exceptionState) {
            this.exceptionState = exceptionState;
        }

        @Override
        public boolean matches(Object object) {
            return (exceptionState == ((Status) object).isIsExceptionalStatus());
        }
    }

    class ActionNameMatcher extends LambdaJMatcher<Action> {
        private String actionName;

        public ActionNameMatcher(String actionName) {
            this.actionName = actionName;
        }

        @Override
        public boolean matches(Object object) {
            return object.equals(this.actionName);
        }
    }

    class StatusNameMatcher extends LambdaJMatcher<Status> {
        private String statusName;

        public StatusNameMatcher(String statusName) {
            this.statusName = statusName;
        }

        @Override
        public boolean matches(Object object) {
            return object.equals(this.statusName);
        }
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.jaxb.v1.Status;
import com.westernalliancebancorp.positivepay.model.Workflow;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * WorkflowManagerFactory is
 *
 * @author Giridhar Duggirala
 */

@Component("workflowManagerFactory")
public class WorkflowManagerFactory implements InitializingBean {
    @Autowired
    WorkflowDao workflowDao;
    List<Workflow> workflowList = null;
    Map<Long, WorkflowManager> workflowManagerMap = new HashMap<Long, WorkflowManager>();
    WorkflowManager workflowManagerV1;
    @Autowired
    Jaxb2Marshaller jaxb2Marshaller;
    Workflow latestWorkFlow = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        workflowList = workflowDao.findAll();
        for (Workflow workflow : workflowList) {
            //For now there is only one version, when the versions increase then we come up with much solid solution.
            workflowManagerV1 = new WorkflowManagerImpl(workflow, jaxb2Marshaller);
            if (workflow.getVersion().equals(workflowManagerV1.getSupportedVersion())) {
                    workflowManagerMap.put(workflow.getId(), new WorkflowManagerImpl(workflow, jaxb2Marshaller));
            }
            if (latestWorkFlow == null) {
                latestWorkFlow = workflow;
            } else if (latestWorkFlow.getVersion() < workflow.getVersion()) {
                latestWorkFlow = workflow;
            }
        }
    }

    public WorkflowManager getWorkflowManagerById(Long workflowId) {
        return workflowManagerMap.get(workflowId);
    }

    public Workflow getLatestWorkflow() {
        return latestWorkFlow;
    }
}

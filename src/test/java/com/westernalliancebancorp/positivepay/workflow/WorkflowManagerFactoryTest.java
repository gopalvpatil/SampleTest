package com.westernalliancebancorp.positivepay.workflow;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 10/4/14
 * Time: 7:17 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class WorkflowManagerFactoryTest {

    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    Jaxb2Marshaller jaxb2Marshaller;

    @Test
    public void retrieveWorkflowDetails(){
    }
}

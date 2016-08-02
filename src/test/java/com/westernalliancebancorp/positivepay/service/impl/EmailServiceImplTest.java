package com.westernalliancebancorp.positivepay.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import ch.lambdaj.Lambda;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.westernalliancebancorp.positivepay.dto.PaidExceptionsEmailDto;
import com.westernalliancebancorp.positivepay.jaxb.v1.Action;
import com.westernalliancebancorp.positivepay.jaxb.v1.Status;
import com.westernalliancebancorp.positivepay.jaxb.v1.Workflow;
import com.westernalliancebancorp.positivepay.service.EmailService;

/**
 * EmailServiceImplTest is
 *
 * @author Giridhar Duggirala
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
@WebAppConfiguration
public class EmailServiceImplTest {
    @Autowired
    EmailService emailService;

    @Autowired
    Jaxb2Marshaller jaxb2Marshaller;
    

    @Test
    public void testSendEmail() throws Exception {
        Map<String,Object> myObj = new HashMap<String, Object>();
        myObj.put("PaidExceptionsEmailDto", new PaidExceptionsEmailDto("firstName","lastName"));
        List<String> myStrings= new ArrayList<String>();
        myStrings.add("moumitaaghosh@gmail.com");
        emailService.sendEmail("paidExceptionsEmail.ftl", myObj, myStrings, myStrings, Boolean.TRUE, "webmaster.test999@gmail.com","Positive Pay  Paid Exceptions");
    }


    @Test
    @Ignore //Ignored as this test case contains hard coded file location and name, can be run with machine specific file name and location
    public void testSendEmailWithAttachment() throws Exception {
    	Map<String,Object> myObj = new HashMap<String, Object>();
        myObj.put("PaidExceptionsEmailDto", new PaidExceptionsEmailDto("firstName","lastName"));
        List<String> myStrings= new ArrayList<String>();
        myStrings.add("moumitaaghosh@gmail.com");
        emailService.sendEmail("paidExceptionsEmail.ftl", myObj, myStrings, myStrings, Boolean.TRUE, "webmaster.test999@gmail.com","D:\\","log.txt","Positive Pay  Paid Exceptions");
    }

    @Test
    public void testJaxb() {
        String fileName = "checkWorkflowStatus.xml";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Workflow workflow = (Workflow) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
        List<Status> statusList = workflow.getStatus();
        List<Status> sList = new ArrayList<Status>(statusList);
        System.out.println("Version of the workflow is : " + workflow.getVersion());
        System.out.println("The length of the status is : " + statusList.size());
        List<String> errorsList = new ArrayList<String>();
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
        Set<String> status = checkUniqueStatusNames(statusList);
        for (String error : errorsList) {
            System.out.println(error);
        }
        for (String error : status) {
            System.out.println(error);
        }
    }

    @Test
    public void testJaxbForUser() {
        String fileName = "checkWorkflowStatus.xml";
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
        Workflow workflow = (Workflow) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
        List<Status> statusList = workflow.getStatus();
        List<Status> sList = new ArrayList<Status>(statusList);
        System.out.println("Version of the workflow is : " + workflow.getVersion());
        System.out.println("The length of the status is : " + statusList.size());
        List<String> errorsList = new ArrayList<String>();
        for (Status status : statusList) {
            for (Action action : status.getAction()) {
                System.out.println(status.getDescription()+"--" + action.getDescription() + "--" + getStatusDescription(statusList, action.getTargetStatusName())+"--"+isExceptionalStatus(statusList, action.getTargetStatusName()));
                String error = checkTargetStatusIsStatus(sList, action.getTargetStatusName());
                if (error != null && !error.isEmpty()) {
                    errorsList.add(error);
                }
            }
        }
        Set<String> status = checkUniqueStatusNames(statusList);
        for (String error : errorsList) {
            System.out.println(error);
        }
        for (String error : status) {
            System.out.println(error);
        }
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

    private String getStatusDescription(List<Status> sList, String targetStatusName) {
        for (Status status : sList) {
            if (targetStatusName.equals(status.getName())) {
                return status.getDescription();
            }
        }
        return "Error";
    }

    private boolean isExceptionalStatus(List<Status> sList, String targetStatusName) {
        for (Status status : sList) {
            if (targetStatusName.equals(status.getName())) {
                if(status.isIsExceptionalStatus() == null){
                    return Boolean.FALSE;
                }
                return status.isIsExceptionalStatus();
            }
        }
        return false;
    }
}

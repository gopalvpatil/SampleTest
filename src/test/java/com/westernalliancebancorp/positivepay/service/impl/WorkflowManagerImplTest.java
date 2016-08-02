package com.westernalliancebancorp.positivepay.service.impl;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * WorkflowManagerImplTest is
 *
 * @author Giridhar Duggirala
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class WorkflowManagerImplTest {
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    CheckDao checkDao;

    @Autowired
    BankDao bankDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    ReferenceDataDao referenceDataDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    Jaxb2Marshaller jaxb2Marshaller;

    @Autowired
    ItemTypeDao itemTypeDao;
    
    @Autowired
    FileTypeDao fileTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testWorkFlow() {
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(1l);
        Map<String, String> actionToNameMap = workflowManager.getActionsForStatus("stop");
        for (String action : actionToNameMap.keySet()) {
            System.out.println("Action name : " + actionToNameMap.get(action));
        }
    }

    @Test
    public void retrieveWorkflowDetails() {
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(1l);
        List<String> statusNames = workflowManager.getStatusNames();
        for (String statusName : statusNames) {
            System.out.println("*******************************************");
            System.out.println("Status:  " + workflowManager.getStatusDescription(statusName));
            if(workflowManager.getOnStatusArrivalCallback(statusName) != null){
            System.out.println("Status Arrival Callback: " + workflowManager.getOnStatusArrivalCallback(statusName));
            }else{
                System.out.println("Status Arrival Callback: None ");
            }
            if(workflowManager.getOnStatusDepatureCallbak(statusName) != null){
            System.out.println("Status Departure Callback: " + workflowManager.getOnStatusDepatureCallbak(statusName));
            }else{
                System.out.println("Status Departure Callback: None");
            }
            System.out.println("Available actions ");
            Map<String, String> actionMap = workflowManager.getActionsForStatus(statusName);
            for (String action : actionMap.keySet()) {
                System.out.println("---Name: " + actionMap.get(action));
                if (workflowManager.getPreActionExecutionCallback(action, statusName) == null) {
                    System.out.println("----Pre Action: None");
                } else {
                    System.out.println("----Pre Action: " + workflowManager.getPreActionExecutionCallback(action, statusName));
                }
                if (workflowManager.getPostActionExecutionCallback(action, statusName) == null) {
                    System.out.println("----Post Action: None");
                } else {
                    System.out.println("----Post Action: " + workflowManager.getPostActionExecutionCallback(action, statusName));
                }
            }
            System.out.println("*******************************************");
        }
    }

    @Test
    //Better testing is done at VoidPaidCheckNumberChangedStatusArrivalCallbackTest
    public void testVoidPaidState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);


        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), UUID.randomUUID().toString());
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.
        String series = "void,voidPaid,changeCheckNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    public void testVoidPaidStateChangedCheckNumberExisting() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.V.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        ItemType anotherItemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        Check changedCheckNumber = new Check();
        changedCheckNumber.setAccount(account);
        changedCheckNumber.setCheckStatus(checkStatus);
        changedCheckNumber.setIssuedAmount(BigDecimal.valueOf(1000));
        changedCheckNumber.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        changedCheckNumber.setItemType(anotherItemType);
        changedCheckNumber.setIssueDate(new Date());
        changedCheckNumber.setPayee("Micheal More");
        changedCheckNumber.setRoutingNumber("111000111234");
        changedCheckNumber.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(changedCheckNumber);

        CheckStatus voidPaidStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId()), "voidPaid", checkStatusDao);

        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), changedCheckNumber.getCheckNumber());
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //Move the check with new number into issued status.
        workflowService.performAction(changedCheckNumber.getId(), "created", userData);

        String series = "void,voidPaid,changeCheckNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //No longer valid.. as duplicate reference data is not possible.
    public void testDuplicatePaidNoPayState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created"; //This will move this into "Paid" status
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        //This will create another reference Id with "Paid" item code
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        workflowService.performAction(check.getId(), "duplicatePaid", userData);
        workflowService.performAction(check.getId(), "noPay", userData);
    }

    @Test
    public void testPaidStatusArrivalCallback() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        userData.put("test4", "test4");
 /*       CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);*/
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        createReferenceDataForStopStatus(check).getId();
        workflowService.performAction(check.getId(), "matched", userData);
    }
    
    @Test
    public void testPaidState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check).getId();
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        userData.put("test4", "test4");
 /*       CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);*/
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }


    @Test
    public void testIssuedStatusArrivalCallbackNoReferenceIdState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        userData.put("test4", "test4");
 /*       CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);*/
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //Will be tested when writing the test cases for "DuplicatePaidAccountNumberChangedStatusArrivalCallback"
    public void testDuplicatePaidAccountNumberState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        userData.put("test4", "test4");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,changeAccountNumber,misreadAccountNumber";
        //"matched" is no longer required as it will be executed by issueStatusArrivalCallback
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created,changeAccountNumber,misreadAccountNumber";
        //String series = "created,matched,duplicatePaid,start";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //Will be tested when writing the test cases for "DuplicatePaidCheckNumberChangedStatusArrivalCallback"
    public void testDuplicatePaidCheckNumberChangedState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        userData.put("test4", "test4");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,changeCheckNumber,misreadCheckNumber";
        //"matched" is no longer required as it will be taken care by IssuedStatusArrivalCallback
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        series = "changeCheckNumber,misreadCheckNumber";
        createReferenceData(check).getId();
        workflowService.performAction(check, "duplicatePaid", userData);
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //Will be tested when writing the test cases for "DuplicatePaidCurrentAccountNumberChangedStatusArrivalCallback"
    public void testDuplicatePaidCurrentAccountNumberChangedState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put("test3", "test3");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
        userData.put(WorkflowService.STANDARD_MAP_KEYS.BANK_ID.name(), new Long(1));
        //String series = "created,matched,duplicatePaid,changeCurrentAccountNumber,moveToStart";
        //"matched" is no longer required as it is be taken care by "issueStatusArrivalCallback"
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created,changeCurrentAccountNumber,moveToStart";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //Will be tested when writing the test cases for "DuplicatePaidCurrentCheckNumberChangedStatusArrivalCallback"
    public void testDuplicatePaidCurrentCheckNumberChangedState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put("test1", "test1");
        userData.put("test2", "test2");
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), "23123123123123");
        userData.put("test4", "test4");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,changeCurrentCheckNumber,moveToStart";
        //"matched" is no longer required as it will be taken care by issuedStatusCallback
        //"duplicatePaid" is no longer required as it will be taken care by paidStatusCallback
        String series = "created,changeCurrentCheckNumber,moveToStart";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    /**
     * This case will only be valid if you add CheckDto with reference_data table if
     */
    public void testIssuedToPaidChangedState() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setItemType(itemType);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String result = workflowService.performAction(check.getId(), "created", userData);
        ReferenceData referenceData = checkDao.findById(check.getId()).getReferenceData();
        Assert.notNull(referenceData, "Reference Data cannot be null, it has to be set by the callback");
        System.out.println("Result is " + result);
    }

    @Test
    public void testForceStatusChange() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("issued", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String statusName = workflowService.forceStatusChange(check.getId(), "start", userData);
        System.out.println("statusName :" + statusName);

    }

    @Test
    public void testVoidStatusArrivalCallbacks() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String series = "void,changeCurrentAccountNumber";
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //No longer valid as Duplicate records can no longer be inserted.
    public void testStopStatusArrivalCallbacks() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String series = "stop,changeCurrentAccountNumber,moveToStart";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }
    
    @Test(expected=CallbackException.class)
    public void testStopStatusArrivalCallbacksWithNoReferenceData() throws WorkFlowServiceException,CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        createReferenceData(check);
        String series = "stop";
        workflowService.performAction(check.getId(), series, userData);
    }
    
    @Test
    public void testStopStatusArrivalCallbacksWithNoReferenceDataAttached() throws WorkFlowServiceException,CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        createReferenceDataForStopStatus(check);
        String series = "stop";
        String result = workflowService.performAction(check.getId(), series, userData);
        assertTrue(result.equalsIgnoreCase(series));
    }
    
    @Test
    public void testStaleStatusArrivalCallbacks() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(WorkflowManagerImplTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        /*CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        createReferenceData(check);*/
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        createReferenceData(check).getId();
        series = "stale";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }

    }
    
    private FileMetaData createFileMetaData() {
        return ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);
    }

    private ReferenceData createReferenceData(Check check) {
        FileMetaData fileMetaData = createFileMetaData();
        //fileDao.save(fileMetaData);

        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(check.getIssuedAmount());
        referenceData.setPaidDate(check.getIssueDate());
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    private ReferenceData createReferenceDataForStopStatus(Check check) {
        FileMetaData fileMetaData = createFileMetaData();

        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(check.getIssuedAmount());
        referenceData.setPaidDate(check.getIssueDate());
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    public static String shortUUID() {
  	  UUID uuid = UUID.randomUUID();
  	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
  	  return Long.toString(l, Character.MAX_RADIX);
  	}
}

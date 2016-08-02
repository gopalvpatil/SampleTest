package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * User: Moumita
 * Date: 02/5/14
 * Time: 5:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class AdjustAmountPreExecutionTest {
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
    ItemTypeDao itemTypeDao;

    @Autowired
    FileTypeDao fileTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testAdjustAmountPreExecutionForStopPaid() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(AdjustAmountPreExecutionTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check, ReferenceData.ITEM_TYPE.STOP);

        Map<String, Object> userData = new HashMap<String, Object>();

        //stop(a) -> Stop (s) -> StopStatusArrivalCallback -> stopPaid(a) -> Stop, Paid -> noPay(a)
        // -> AdjustAmountPreExecution (a) -> paid(s)

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        
        ReferenceData stopPaidReferenceData = createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);
        series = "stopPaid,noPay";

        for (String actionToPerform : series.split(",")) {
        	if(actionToPerform.equals("stopPaid")){
                userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_ID.name(),stopPaidReferenceData.getId());
            }
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
     }

    @Test
    public void testAdjustAmountPreExecutionForVoidPaid() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(AdjustAmountPreExecutionTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);

        Map<String, Object> userData = new HashMap<String, Object>();

        //stop(a) -> Stop (s) -> StopStatusArrivalCallback -> stopPaid(a) -> Stop, Paid -> noPay(a)
        // -> AdjustAmountPreExecution (a) -> paid(s)

        String series = "void,noPay";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
     }
    
    @Test
    public void testAdjustAmountPreExecutionForStalePaid() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(AdjustAmountPreExecutionTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);


        Map<String, Object> userData = new HashMap<String, Object>();

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        
        createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);

        series = "stale,noPay";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        
     }

    @Test
    public void testAdjustAmountPreExecutionForPaidNotIssued() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(UUID.randomUUID().toString());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        
        ReferenceData stopReferenceData = createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);
        Map<String, Object> userData  = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(stopReferenceData.getId());
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_DTO.name(), checkDto);
        //Check will end up in stop not issued status after taking this workflow action.

        String series = "paidNotIssued,noPay";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
                 
     }


    private ReferenceData createReferenceData(Check check, ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);

        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(check.getIssuedAmount());
        referenceData.setPaidDate(check.getIssueDate());
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(item_type);
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    public static String shortUUID() {
       return RandomStringUtils.random(5, -10000000, 0, Boolean.FALSE, Boolean.TRUE );
  	}
}

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

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
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 17/4/14
 * Time: 2:37 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class StalePaidCheckNumberChangedStatusArrivalCallbackTest {
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

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testStalePaidCheckNumberChangedStatusArrivalCallbackTestThroughStalePaidStatusArrivalCallback() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(StalePaidCheckNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);


        Map<String, Object> userData = userData = new HashMap<String, Object>();

        //Issued(s)->stale(a) -> Stale (s) -> StaleStatusArrivalCallback -> stalePaid(a) -> Stale, Paid -> changeCheckNumber(a)
        // -> Stale Paid Check number changed (a) -> StalePaidCheckNumberChangedStatusArrivalCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }

        createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);
        workflowService.performAction(check.getId(), "stale", userData);

        series = "changeCheckNumber";

        for (String actionToPerform : series.split(",")) {
            if(actionToPerform.equals("changeCheckNumber")){
                userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), (new Long(System.currentTimeMillis())).toString());
            }
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }

    }

    @Test
    public void testStalePaidCheckNumberChangedTestThroughStalePaidStatusArrivalCallbackCheckExisting() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(StalePaidCheckNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Long checkNumber=new Long(System.currentTimeMillis());
        Check newCheck = new Check();
        newCheck.setAccount(account);
        newCheck.setCheckStatus(checkStatus);
        newCheck.setIssuedAmount(BigDecimal.valueOf(1000));
        newCheck.setCheckNumber(checkNumber.toString());
        newCheck.setItemType(itemType);
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        newCheck.setIssueDate(calendar.getTime());
        newCheck.setPayee("Micheal More");
        newCheck.setRoutingNumber("111000111234");
        newCheck.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        newCheck.setDigest(accountDao.findById(check.getAccount().getId()).getNumber() +""+ check.getCheckNumber());
        checkDao.save(newCheck);


        Map<String, Object> userData = userData = new HashMap<String, Object>();
        workflowService.performAction(newCheck.getId(), "created", userData);
        //Issued(s)->stale(a) -> Stale (s) -> StaleStatusArrivalCallback -> stalePaid(a) -> Stale, Paid -> changeCheckNumber(a)
        // -> Stale Paid Check number changed (a) -> StalePaidCheckNumberChangedStatusArrivalCallback
        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }

        createReferenceData(check, ReferenceData.ITEM_TYPE.PAID);
        workflowService.performAction(check.getId(), "stale", userData);

        series = "changeCheckNumber";


        for (String actionToPerform : series.split(",")) {
            if(actionToPerform.equals("changeCheckNumber")){
                userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), checkNumber.toString());
            }
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }

    }

    private ReferenceData createReferenceData(Check check, ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setUploadDirectory("SomeDir");
        fileMetaData.setChecksum(UUID.randomUUID().toString());
        fileMetaData.setOriginalFileName("OriginalFileName");
        fileMetaData.setFileSize(1l);
        fileMetaData.setItemsReceived(10l);
        fileMetaData.setFileName(UUID.randomUUID().toString());
        fileMetaData.setStatus(FileMetaData.STATUS.PROCESSED);
        fileDao.save(fileMetaData);

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
  	  UUID uuid = UUID.randomUUID();
  	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
  	  return Long.toString(l, Character.MAX_RADIX);
  	}
}

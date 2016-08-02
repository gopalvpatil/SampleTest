package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/9/14
 * Time: 8:51 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class DummyDataInsertTest {
    @Autowired
    private BatchDao batchDao;
    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    CheckDao checkDao;

    @Autowired
    MarkStaleService markStaleService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    FileDao fileDao;

    @Autowired
    ReferenceDataDao referenceDataDao;

    @Autowired
    PaidService paidService;
    
    @Autowired
    DuplicateStopService duplicateStopService;

    @Autowired
    DuplicatePaidService duplicatePaidService;
    
    @Autowired
    InvalidAmountService invalidAmountService;

    @Autowired
    StopPresentedService stopPresentedService;

    @Autowired
    PaidNotIssuedService paidNotIssuedService;
    
    @Autowired
    CheckHistoryDao checkHistoryDao;
    
    @Autowired
    StopStatusService stopStatusService;
    @Autowired
    LinkageTypeDao linkageTypeJpaDao;
    
    @Autowired
    VoidPaidService voidPaidService;
    
    @Autowired
    ExceptionalCheckDao exceptionalCheckDao;
    @Autowired
    StopNotIssuedService stopNotIssuedService;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    FileTypeDao fileTypeDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ExceptionStatusDao exceptionStatusDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        PositivePayThreadLocal.setSource("testCase");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    @Ignore
    public void insertIntoTestReferenceData() throws ParseException, InterruptedException {
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        List<Check> checkList = new ArrayList<Check>();
        List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
        FileMetaData fileMetaData = createFileMetaData();
        UUID uuid = UUID.fromString(UUID.randomUUID().toString());
        Random random =  new Random(System.currentTimeMillis());
        for (int j = 0; j < 120; j++) {
            for (int i = 0; i <= 1000; i++) {
                Check check = new Check();
                check.setAccount(account);
                check.setCheckStatus(checkStatus);
                check.setIssuedAmount(BigDecimal.valueOf(120002));
                Thread.sleep(1l);
                check.setCheckNumber(uuid.randomUUID().toString() + "--" + i + "-" + j);
                check.setItemType(itemType);
                check.setIssueDate(getStaleDate());
                check.setPayee("Micheal More");
                check.setRoutingNumber("1111-1111-111-");
                check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                AuditInfo auditInfo = new AuditInfo();
                auditInfo.setCreatedBy("gduggira");
                auditInfo.setDateCreated(new Date());
                auditInfo.setDateModified(new Date());
                auditInfo.setModifiedBy("gduggira");
                check.setAuditInfo(auditInfo);
                checkList.add(check);

                ReferenceData referenceData = new ReferenceData();
                referenceData.setPaidDate(DateUtils.getWALFormatDate(new Date()));
                referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
                referenceData.setFileMetaData(fileMetaData);
                referenceData.setAuditInfo(auditInfo);
                referenceData.setTraceNumber(UUID.randomUUID().toString());
                referenceData.setTraceNumber("TranceNumber" + i + "-");
                referenceData.setStopDate(new Date());
                int ran = random.nextInt(1000);
                if (ran > 1 && isPrime(ran)) {
                    referenceData.setAccount(check.getAccount());
                    referenceData.setCheckNumber(check.getCheckNumber());
                    referenceData.setAmount(check.getIssuedAmount());
                } else {
                    Account referenceDataAccount = accountDao.findAll().get(1);
                    referenceData.setAccount(referenceDataAccount);
                    referenceData.setAmount(BigDecimal.valueOf(120002));
                    referenceData.setCheckNumber(UUID.randomUUID().toString());
                }
                referenceDataList.add(referenceData);
            }
            System.out.println("Done Generating data");
            batchDao.insertAllChecksNoFileMetadata(checkList);
            batchDao.insertAllReferenceData(referenceDataList);
            /*for (Check check : checkList) {
                try {
                    Check myCheck = checkDao.findCheckBy(check.getAccount().getNumber(), check.getCheckNumber(), check.getIssuedAmount(), true);
                    workflowService.performAction(myCheck.getId(), "created", new HashMap<String, Object>(), Boolean.FALSE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/
        }

    }

    private FileMetaData createFileMetaData() {
        return ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);
    }

    boolean isPrime(int n) {
        //check if n is a multiple of 2
        if (n % 2 == 0) return false;
        //if not, then just check the odds
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    @Test
    public void testMarkChecksStaleByAccountIds() throws CallbackException, WorkFlowServiceException {
    	List<Long> accountIds = new ArrayList<Long>();
    	accountIds.add(1L);
        markStaleService.markChecksStaleByAccountIds(accountIds);
    }

    @Test
    @Ignore
    public void testCheckInsert() {
        Account account = accountDao.findAll().get(0);
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        List<Check> checkList = new ArrayList<Check>(120000);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.name());
        for (int j = 0; j < 120; j++) {
            for (int i = 0; i <= 5; i++) {
                Check check = new Check();
                check.setAccount(account);
                check.setCheckStatus(checkStatus);
                check.setIssuedAmount(BigDecimal.valueOf(120002));
                check.setCheckNumber(UUID.randomUUID().toString());
                check.setItemType(itemType);
                check.setIssueDate(getStaleDate());
                check.setPayee("Micheal More");
                check.setRoutingNumber("1111-1111-111-" + i);
                check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                AuditInfo auditInfo = new AuditInfo();
                auditInfo.setCreatedBy("gduggira");
                auditInfo.setDateCreated(new Date());
                auditInfo.setDateModified(new Date());
                auditInfo.setModifiedBy("gduggira");
                check.setAuditInfo(auditInfo);
                checkList.add(check);
            }
            System.out.println("Done Generating data");
            batchDao.insertAllChecksNoFileMetadata(checkList);
            for (Check check : checkList) {
                try {
                    Check myCheck = checkDao.findCheckBy(check.getAccount().getNumber(), check.getCheckNumber(), check.getIssuedAmount());
                    workflowService.performAction(myCheck.getId(), "created", new HashMap<String, Object>());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // testIn();
        }
    }

    private Date getStaleDate() {
        Integer days = new Integer(185);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.CHECK_ISSUE_DATE_SQL_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        String date = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.CHECK_ISSUE_DATE_SQL_FORMAT);
        ParsePosition position = new ParsePosition(0);
        return formatter.parse(date, position);
    }

    @Test
    public void testDecimalFormat() {
        DecimalFormat moneyFormat = new DecimalFormat("00000000.00");
        System.out.println(moneyFormat.format(123.56));
        System.out.println("Value " + new Float(moneyFormat.format(123.56)));
    }

    @Test
    public void testMarkChecksPaidByBankIds() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued","paid",true,null);
        paidService.markChecksPaidByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        //deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testmarkChecksDuplicateStopByAccountIds() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("stop", "stop", true,null);
        duplicateStopService.markChecksDuplicateStopByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.DuplicateStopException)) ;
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testmarkChecksVoidPaidByAccountIds() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("void", "paid", true,null);
        voidPaidService.markChecksVoidPaidByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.VoidPaidException)) ;
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testmarkChecksInvalidAmountByAccountIds() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued", "paid", false,null);
        invalidAmountService.markChecksInvalidAmountByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.InvalidAmountException));
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }

    @Test
    public void testmarkChecksInvalidAmountNoPayByAccountIds() throws CallbackException, WorkFlowServiceException {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued", "paid", false,null);
        invalidAmountService.markChecksInvalidAmountByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.InvalidAmountException));
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        Map<String, Object> userData = new HashMap<String, Object>();
        workflowService.performAction(check.getId(), "noPay", userData);
//        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testmarkChecksInvalidAmountByAccountIdWithInvalidStopAmountException() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued", "stop", false,null);
        invalidAmountService.markChecksInvalidAmountByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.InvalidStopAmountException));
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    @Ignore
    public void testmarkChecksDuplicatePaidByAccountIds() {
    	List<Long> accountIds = new ArrayList<Long>();
    	accountIds.add(1L);
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("paid","paid",true,null);
        duplicatePaidService.markChecksDuplicatePaidByAccountIds(accountIds);
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException)) ;
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testmarkChecksStopByAccountIds() {
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued","stop",true,null);
        stopStatusService.markChecksStopByAccountIds(Lambda.extract(accountDao.findAll(), on(Account.class).getId()));
        Check check =checkDao.findById(ids.get("checkId"));
        ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getReferenceData().getId().equals(referenceData.getId())) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    @Test
    public void testInsertCheckwithParentandLinkageType() {
        Map<String,Long> ids_parent =insertIntoReferenceDataAndCheckDetail("issued","stop",true,null);
        Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("issued","stop",true,ids_parent.get("checkId"));
        Check check =checkDao.findById(ids.get("checkId"));
        assertNotNull(check);
        assertNotNull(check.getParentCheck());

    }
    
    @Test
    public void testMarkChecksStopNotIssuedByAccountIds() {
        ReferenceData referenceData =createReferenceData(ReferenceData.ITEM_TYPE.STOP);
        List<Long> accountIds = new ArrayList<Long>();
        Long referenceDataId = referenceData.getId();
        accountIds.add(referenceData.getAccount().getId());
        stopNotIssuedService.markChecksStopNotIssuedByAccountIds(accountIds);
        Check check = checkDao.findByReferenceDataId(referenceDataId);
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertNull(check.getExceptionType()) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
    }
    
    
    @Test
    public void testMarkChecksPaidNotIssuedByBankIds() {
        ReferenceData referenceData =createReferenceData(ReferenceData.ITEM_TYPE.PAID);
        List<Long> accountIds = new ArrayList<Long>();
        Long referenceDataId = referenceData.getId();
        accountIds.add(referenceData.getAccount().getId());
        paidNotIssuedService.markChecksPaidNotIssuedByAccountIds(accountIds);
        Check check = checkDao.findByReferenceDataId(referenceDataId);
        List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(check.getId());
        assertNotNull(check.getReferenceData());
        assertNotNull(checkHistory);
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.PaidNotIssuedException)) ;
        deleteReferenceDataAndCheckDetail(check, checkHistory, referenceData);
        
    }

    private Map<String, Long> insertIntoReferenceDataAndCheckDetail(String currentStatus, String referenceDataItemType, boolean amtEqual,Long parentCheckId) {
        Map<String, Long> ids = new HashMap<String, Long>();
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        FileMetaData fileMetaData = createFileMetaData();
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId()), currentStatus, checkStatusDao);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(DummyDataInsertTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(getStaleDate());
        check.setDigest(account.getNumber()+check.getCheckNumber());
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Junit");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("Junit");
        check.setAuditInfo(auditInfo);

        ReferenceData referenceData = new ReferenceData();
        referenceData.setFileMetaData(fileMetaData);
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        if (referenceDataItemType.equalsIgnoreCase("paid")) {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
        } else {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
        }
        referenceData.setAuditInfo(auditInfo);
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setStopDate(new Date());
        referenceData.setAccount(check.getAccount());
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setDigest(account.getNumber()+check.getCheckNumber());
        if (amtEqual) {
            referenceData.setAmount(check.getIssuedAmount());
        } else {
            referenceData.setAmount(BigDecimal.valueOf(120000));
        }
        if(parentCheckId != null)
        {
        Check parentCheck = checkDao.findById(parentCheckId);
        check.setParentCheck(parentCheck);
        LinkageType linkageType = linkageTypeJpaDao.findByName(LinkageType.NAME.ACCOUNT_NUMBER_CHANGED);
        check.setLinkageType(linkageType);
        }
     
        Check checkDB = checkDao.save(check);
        ReferenceData referenceDataDB = referenceDataDao.save(referenceData);
        ids.put("checkId", checkDB.getId());
        ids.put("referenceDataId", referenceDataDB.getId());
        return ids;

    }
    
    private void insertIntoCheckDetail() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.getName());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId()), "start", checkStatusDao);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(DummyDataInsertTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(getStaleDate());
        check.setDigest(account.getNumber()+check.getCheckNumber());
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Junit");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("Junit");
        check.setAuditInfo(auditInfo);
        Check checkDB = checkDao.save(check);
        Map<String, Object> userData =   new HashMap<String, Object>();
		workflowService.performAction(checkDB.getId(),"created" ,userData );
    }
	
    private ReferenceData createReferenceData(ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);
        Account account = accountDao.findAll().get(0);
        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber("222");
        referenceData.setAmount(new BigDecimal("10"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        if(item_type.equals(ReferenceData.ITEM_TYPE.STOP))
        {
        	referenceData.setStopDate(calendar.getTime());
        }
        else if(item_type.equals(ReferenceData.ITEM_TYPE.PAID))
        {
        	referenceData.setPaidDate(calendar.getTime());
        }
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(account);
        referenceData.setItemType(item_type);
        referenceData.setFileMetaData(fileMetaData);
        referenceData.setDigest(account.getNumber()+referenceData.getCheckNumber());
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    private  void deleteReferenceDataAndCheckDetail(Check checkToDelete,List<CheckHistory> checkHistory,ReferenceData referenceData) 
    {
    	for(CheckHistory checkHistorySing : checkHistory)
    	{
    		checkHistoryDao.delete(checkHistorySing);
    	}
    	checkDao.delete(checkToDelete);
    	referenceDataDao.delete(referenceData);
    	
    
    }
    
    public static String shortUUID() {
    	  return RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
    	}


}


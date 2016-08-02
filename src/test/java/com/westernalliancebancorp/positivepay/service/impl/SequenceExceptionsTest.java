package com.westernalliancebancorp.positivepay.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.IssuedAfterStopService;
import com.westernalliancebancorp.positivepay.service.IssuedAfterVoidService;
import com.westernalliancebancorp.positivepay.service.StaleVoidService;
import com.westernalliancebancorp.positivepay.service.StopAfterPaidService;
import com.westernalliancebancorp.positivepay.service.VoidAfterPaidService;
import com.westernalliancebancorp.positivepay.service.VoidAfterStopService;
import com.westernalliancebancorp.positivepay.service.VoidStopService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * User: Moumita
 * Date: 26/6/14
 * Time: 8:51 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class SequenceExceptionsTest {
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
    WorkflowService workflowService;
    @Autowired
    FileDao fileDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    ExceptionalCheckDao expCheckDao;
    @Autowired
    VoidAfterPaidService voidAfterPaidService;
    @Autowired
    VoidAfterStopService voidAfterStopService;
    @Autowired
    IssuedAfterVoidService issuedAfterVoidService;
    @Autowired
    IssuedAfterStopService issuedAfterStopService;
    @Autowired
    StaleVoidService staleVoidService;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    FileTypeDao fileTypeDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    ExceptionStatusDao exceptionStatusDao;
    @Autowired
    StopAfterPaidService stopAfterPaidService;
    @Autowired
    VoidStopService voidStopService;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
        PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Batch.toString());
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
    public void testmarkChecksVoidAfterPaid() throws CallbackException, WorkFlowServiceException {
    	List<Long> accountIds = new ArrayList<Long>();
    	accountIds.add(1L);
        Map<String,Long> ids =insertIntoExceptionCheckDetailAndCheckDetail("start","void");
        
        Check check =checkDao.findById(ids.get("checkId"));
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check,ReferenceData.ITEM_TYPE.PAID).getId());
        userData.put(Constants.CHECK_DTO, checkDto);

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        
        voidAfterPaidService.markChecksVoidAfterPaid();
        check =checkDao.findById(ids.get("checkId"));
        ExceptionalCheck exCheck =expCheckDao.findById(ids.get("exCheckId"));
        
        assertNotNull(check);
        assertNull(exCheck);
        //VoidAfterPaid auto resolves to void
       // assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.SequenceException_VoidAfterPaid)) ;
        
/*        series = "noPay";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }*/
        System.out.println("************testmarkChecksVoidAfterPaid ends*********");
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
    }
        
    @Test
    public void testmarkChecksStaleVoid() throws CallbackException, WorkFlowServiceException {
    	List<Long> accountIds = new ArrayList<Long>();
    	accountIds.add(1L);
        Map<String,Long> ids =insertIntoExceptionCheckDetailAndCheckDetail("start","void");
        
        Check check =checkDao.findById(ids.get("checkId"));
        Map<String, Object> userData = new HashMap<String, Object>();

        String series = "created,stale";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        
        staleVoidService.markChecksStaleVoid();
        check =checkDao.findById(ids.get("checkId"));
       
        assertNotNull(check);
        //Auto resolves to void
        System.out.println("************testmarkChecksStaleVoid ends*********");
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());

    }
    
    @Test
    public void testmarkChecksVoidAfterStop() throws CallbackException, WorkFlowServiceException {
        Map<String,Long> ids =insertIntoExceptionCheckDetailAndCheckDetail("start","void");
        Check check =checkDao.findById(ids.get("checkId"));
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check,ReferenceData.ITEM_TYPE.STOP).getId());
        userData.put(Constants.CHECK_DTO, checkDto);

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        voidAfterStopService.markChecksVoidAfterStop();
        check =checkDao.findById(ids.get("checkId"));
        ExceptionalCheck exCheck =expCheckDao.findById(ids.get("exCheckId"));
        assertNotNull(check);
        assertNull(exCheck);
        assertNull(check.getExceptionType()) ;
        //Auto resolves to Stop
       // assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.SequenceException_VoidAfterStop)) ;
        System.out.println("************testmarkChecksVoidAfterStop ends*********");
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
    }
    
    @Test
    public void testmarkChecksIssuedAfterVoid() throws CallbackException, WorkFlowServiceException{
        Map<String,Long> ids =insertIntoExceptionCheckDetailAndCheckDetail("start","issued");
        Check check =checkDao.findById(ids.get("checkId"));
        check.setVoidAmount(check.getIssuedAmount());
        check.setVoidDate(check.getIssueDate());
        checkDao.update(check);
        Map<String, Object> userData = new HashMap<String, Object>();

        String series = "void";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        issuedAfterVoidService.markChecksIssuedAfterVoid();
        check =checkDao.findById(ids.get("checkId"));
        ExceptionalCheck exCheck =expCheckDao.findById(ids.get("exCheckId"));
        assertNotNull(check);
        assertNull(exCheck);
        //Auto resolves to issued
        //assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.SequenceException_IssuedAfterVoid)) ;
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
        System.out.println("************testmarkChecksIssuedAfterVoid ends*********");
    }
    
    @Test
    public void testmarkChecksIssuedAfterStop() throws CallbackException, WorkFlowServiceException {

        Map<String,Long> ids =insertIntoExceptionCheckDetailAndCheckDetail("start","issued");
        Check check =checkDao.findById(ids.get("checkId"));
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check,ReferenceData.ITEM_TYPE.STOP).getId());
        userData.put(Constants.CHECK_DTO, checkDto);

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        issuedAfterStopService.markChecksIssuedAfterStop();
        check =checkDao.findById(ids.get("checkId"));
        ExceptionalCheck exCheck =expCheckDao.findById(ids.get("exCheckId"));
        assertNotNull(check);
        assertNull(exCheck);
        //Auto resolves to issued
        //assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.SequenceException_IssuedAfterStop)) ;
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
        System.out.println("************testmarkChecksIssuedAfterStop ends*********");
    }
    
    @Test
    public void testmarkChecksStopAfterPaid() throws CallbackException, WorkFlowServiceException {

        Long id =insertIntoCheckDetail("start");
        Check check =checkDao.findById(id);
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check,ReferenceData.ITEM_TYPE.PAID).getId());
        userData.put(Constants.CHECK_DTO, checkDto);

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        ReferenceData refData = createReferenceData(check,ReferenceData.ITEM_TYPE.STOP);
        stopAfterPaidService.markChecksStopAfterPaid();
        check =checkDao.findById(id);
        assertNotNull(check);
        //StopAfterPaid doesn't auto resolve
        assertTrue(check.getExceptionType().getExceptionType().equals(ExceptionType.EXCEPTION_TYPE.SequenceException_StopAfterPaid)) ;
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
        System.out.println("************testmarkChecksStopAfterPaid ends*********");
    }
    
    @Test
    public void testmarkChecksStopAfterVoid() throws CallbackException, WorkFlowServiceException {

        Long id =insertIntoCheckDetail("start");
        Check check =checkDao.findById(id);
        check.setVoidAmount(check.getIssuedAmount());
        check.setVoidDate(check.getIssueDate());
        checkDao.update(check);
        List<Long> accountIds = new ArrayList<Long> ();
        accountIds.add(check.getAccount().getId());
        Map<String, Object> userData = new HashMap<String, Object>();
        String series = "void";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        createReferenceData(check,ReferenceData.ITEM_TYPE.STOP);
        voidStopService.markChecksVoidStopByAccountIds(accountIds);
        check =checkDao.findById(id);
        assertNotNull(check);
        //Auto resolves to stop
        System.out.println("Check id is " + check.getId()+" Check number is "+check.getCheckNumber());
        System.out.println("************testmarkChecksStopAfterVoid ends*********");
    }
       
    private Map<String, Long> insertIntoExceptionCheckDetailAndCheckDetail(String currentStatus, String exceptionCheckStatus) {
        Map<String, Long> ids = new HashMap<String, Long>();
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        FileMetaData fileMetaData = createFileMetaData();
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion(currentStatus, 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(SequenceExceptionsTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(getStaleDate());

        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Junit");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("Junit");
        check.setAuditInfo(auditInfo);

        ExceptionalCheck exCheck = new ExceptionalCheck();
        ExceptionType exceptionType = exceptionTypeDao.findByName(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE);
        if (exceptionCheckStatus.equalsIgnoreCase("void")) {
        	exCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.VOID);
        } else {
        	exCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.ISSUED);
        }
        exCheck.setAuditInfo(auditInfo);
        exCheck.setExceptionStatus(exceptionStatusDao.findByName(ExceptionStatus.STATUS.OPEN.name()));
        exCheck.setExceptionType(exceptionType);
        exCheck.setAccountNumber(check.getAccount().getNumber());
        exCheck.setCheckNumber(check.getCheckNumber());
        exCheck.setIssuedAmount(String.valueOf(BigDecimal.valueOf(120001)));
        exCheck.setIssueCode(check.getItemType().getName());
        try {
			exCheck.setIssueDate(DateUtils.getStringFromDate(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        //TODO:Moumita  please make this change
        //exCheck.setExceptionStatus(ExceptionStatus.STATUS.DUPLICATE_CHECK_IN_DATABASE.name());

        Check checkDB = checkDao.save(check);
        ExceptionalCheck exCheckDB = expCheckDao.save(exCheck);
        ids.put("checkId", checkDB.getId());
        ids.put("exCheckId", exCheckDB.getId());
        return ids;

    }
    
    private ReferenceData createReferenceData(Check check,ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);

        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(check.getIssuedAmount());
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
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(item_type);
        referenceData.setFileMetaData(fileMetaData);
        String digest = check.getAccount().getNumber()+""+check.getCheckNumber();
        referenceData.setDigest(digest);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    private Long insertIntoCheckDetail(String currentStatus) {
        //Map<String, Long> ids = new HashMap<String, Long>();
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        //FileMetaData fileMetaData = createFileMetaData();
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion(currentStatus, 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(SequenceExceptionsTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(getStaleDate());
        String digest = account.getNumber()+""+check.getCheckNumber();
        check.setDigest(digest);

        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Junit");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("Junit");
        check.setAuditInfo(auditInfo);
        Check checkDB = checkDao.save(check);
        return checkDB.getId();

    }
    
    public static String shortUUID() {
    	  UUID uuid = UUID.randomUUID();
    	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    	  return Long.toString(l, Character.MAX_RADIX);
    	}

    private FileMetaData createFileMetaData() {
        return ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);
    }

}


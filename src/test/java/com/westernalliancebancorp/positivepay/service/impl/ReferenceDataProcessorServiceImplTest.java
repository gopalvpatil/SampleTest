package com.westernalliancebancorp.positivepay.service.impl;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * User: moumita
 * Date: 19/6/14
 * Time: 2:05 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class ReferenceDataProcessorServiceImplTest {
    @Autowired
    ItemTypeDao itemTypeDao;
    
    @Autowired
    AccountDao accountDao;
    
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    
    @Autowired
    CheckDao checkDao;
    
    @Autowired
    LinkageTypeDao linkageTypeJpaDao;
    
    @Autowired
    FileDao fileDao;
    
    @Autowired
    ReferenceDataDao referenceDataDao;
    
    @Autowired
    FileTypeDao fileTypeDao;

    @Autowired
    ReferenceDataProcessorService ReferenceDataProcessor;
    
    @Autowired
    CheckStatusDao checkStatusDao;
    
    @Autowired
    CheckHistoryDao checkHistoryDao;
    
    @Autowired
    WorkflowService workflowService;
    
    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    
    @Test
    public void testProcessNonDuplicateReferenceDatawithNoDataInCheckDetail() throws CallbackException, WorkFlowServiceException {
    	 Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("start","paid",true,null);
         Check check =checkDao.findById(ids.get("checkId"));
         ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    }
    
    @Test
    public void testProcessNonDuplicateRefernceDatawithPaidNoCheckFoundInDB() throws CallbackException, WorkFlowServiceException {
    	 ReferenceData referenceData =createReferenceData(ReferenceData.ITEM_TYPE.PAID);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    	 Check check =checkDao.findByReferenceDataId(referenceData.getId());
    	 List<CheckHistory> checkHistoryDataList = checkHistoryDao.findByCheckId(check.getId());
    	 referenceData = referenceDataDao.getReference(referenceData.getId());
    	// deleteReferenceDataAndCheckDetail(check,checkHistoryDataList,referenceData);

    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithPaidandCheckStatusIssued() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.PAID);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);

    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithPaidandCheckStatusStale() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		workflowService.performAction(checkDB.getId(),"stale" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.PAID);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithPaidandCheckStatusStop() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 createReferenceData(checkDB,ReferenceData.ITEM_TYPE.STOP);
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		// workflowService.performAction(checkDB.getId(),"matched" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.PAID);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithPaidandCheckStatusVoid() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"void" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.PAID);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);

    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithStopandCheckStatusIssued() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.STOP);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);

    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithStopandCheckStatusStale() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		workflowService.performAction(checkDB.getId(),"stale" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.STOP);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithStopandCheckStatusPaid() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 createReferenceData(checkDB,ReferenceData.ITEM_TYPE.PAID);
 		 workflowService.performAction(checkDB.getId(),"created" ,userData );
 		// workflowService.performAction(checkDB.getId(),"matched" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.STOP);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithStopandCheckStatusVoid() throws CallbackException, WorkFlowServiceException {
    	 Check checkDB =insertIntoCheckDetail();
         Map<String, Object> userData =   new HashMap<String, Object>();
 		 workflowService.performAction(checkDB.getId(),"void" ,userData );
 		 ReferenceData referenceData = createReferenceData(checkDB,ReferenceData.ITEM_TYPE.STOP);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);

    }
    
    @Test
    public void testProcessNonDuplicateReferenceDatawithStop() throws CallbackException, WorkFlowServiceException {
    	 Map<String,Long> ids =insertIntoReferenceDataAndCheckDetail("start","stop",true,null);
         Check check =checkDao.findById(ids.get("checkId"));
         ReferenceData referenceData =referenceDataDao.findById(ids.get("referenceDataId"));
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);

    }
    
   
    @Test
    public void testProcessNonDuplicateRefernceDatawithStopNoCheckFoundInDB() throws CallbackException, WorkFlowServiceException {
    	 ReferenceData referenceData =createReferenceData(ReferenceData.ITEM_TYPE.STOP);
    	 List <ReferenceData> referenceDataList= new ArrayList<ReferenceData>();
    	 referenceDataList.add(referenceData);
    	 ReferenceDataProcessor.processNonDuplicateReferenceData(referenceDataList);
    	 Check check =checkDao.findByReferenceDataId(referenceData.getId());
    	 List<CheckHistory> checkHistoryDataList = checkHistoryDao.findByCheckId(check.getId());
    	 referenceData = referenceDataDao.getReference(referenceData.getId());
    	// deleteReferenceDataAndCheckDetail(check,checkHistoryDataList,referenceData);

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
        check.setCheckNumber(shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());

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
    
    private ReferenceData createReferenceData(ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);
        Account account = accountDao.findAll().get(0);
        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber("9999");
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
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    private ReferenceData createReferenceData(Check check,ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);

        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(check.getIssuedAmount());
        if(item_type.equals(ReferenceData.ITEM_TYPE.STOP))
        {
        	referenceData.setStopDate(check.getIssueDate());
        }
        else if(item_type.equals(ReferenceData.ITEM_TYPE.PAID))
        {
        	referenceData.setPaidDate(check.getIssueDate());
        }
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(item_type);
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    private Check insertIntoCheckDetail() throws WorkFlowServiceException, CallbackException {
        Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.S.getName());
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId()), "start", checkStatusDao);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(120002));
        check.setCheckNumber(shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());

        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Junit");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("Junit");
        check.setAuditInfo(auditInfo);
        Check checkDB = checkDao.save(check);
        return checkDB;

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

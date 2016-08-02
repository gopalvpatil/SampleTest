package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.StopReturnService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * User: gduggirala
 * Date: 17/4/14
 * Time: 8:55 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class StopReturnServiceImplTest {
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
    StopReturnService stopReturnService;

    @Autowired
    ItemTypeDao itemTypeDao;
    
    @Autowired
    FileTypeDao fileTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testStopReturnServiceHappyPath() throws CallbackException, WorkFlowServiceException {
        List<Check> checkList = createChecks(1);
        createReferenceData(checkList, ReferenceData.ITEM_TYPE.STOP);
        Map<String, Object> userData=new HashMap<String, Object>();
        for(Check check:checkList){
            workflowService.performAction(check,"created",userData);
        }
        List<ReferenceData> referenceDataList = createReferenceData(checkList, ReferenceData.ITEM_TYPE.STOP_PRESENTED);
        List<Long> referenceIds = Lambda.extract(referenceDataList, Lambda.on(ReferenceData.class).getId());
        stopReturnService.processStopReturnReference(referenceIds);

    }
    
    @Test
    public void testStopReturnServicewithNoDataInCheckDetail() throws CallbackException, WorkFlowServiceException {
    	List<ReferenceData> referenceDataList =createReferenceDataWithoutCheckDetail(ReferenceData.ITEM_TYPE.STOP_PRESENTED);
        List<Long> referenceIds = Lambda.extract(referenceDataList, Lambda.on(ReferenceData.class).getId());
        stopReturnService.processStopReturnReference(referenceIds);

    }
    
    public List<Check> createChecks(int count) {
        List<Check> returnList = new ArrayList<Check>();
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        for (int i = 0; i < count; i++) {
            Check check = new Check();
            check.setAccount(account);
            check.setCheckStatus(checkStatus);
            check.setIssuedAmount(BigDecimal.valueOf(1000));
            check.setCheckNumber(StopReturnServiceImplTest.shortUUID());
            check.setItemType(itemType);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            check.setIssueDate(calendar.getTime());
            check.setPayee("Micheal More");
            check.setRoutingNumber("111000111234");
            check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
            checkDao.save(check);
            returnList.add(check);
        }
        return returnList;
    }

    public List<ReferenceData> createReferenceData(List<Check> checkList, ReferenceData.ITEM_TYPE item_type) {
        List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
        for (Check check : checkList) {
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
            referenceDataList.add(referenceData);
        }
        return referenceDataList;
    }
    
    public List<ReferenceData> createReferenceDataWithoutCheckDetail(ReferenceData.ITEM_TYPE item_type) {
        List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
            FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);

            ReferenceData referenceData = new ReferenceData();
            referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
            referenceData.setCheckNumber(StopReturnServiceImplTest.shortUUID());
            referenceData.setAmount(BigDecimal.valueOf(1000));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            referenceData.setPaidDate(calendar.getTime());
            referenceData.setTraceNumber(UUID.randomUUID().toString());
            referenceData.setAccount(accountDao.findById(1l));
            referenceData.setItemType(item_type);
            referenceData.setFileMetaData(fileMetaData);
            referenceDataDao.save(referenceData);
            referenceDataList.add(referenceData);

        return referenceDataList;
    }
    
    public static String shortUUID() {
    	  UUID uuid = UUID.randomUUID();
    	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    	  return Long.toString(l, Character.MAX_RADIX);
    	}
}

package com.westernalliancebancorp.positivepay.workflow;

import static org.junit.Assert.assertTrue;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.service.impl.WorkflowManagerImplTest;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

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
 * Created with IntelliJ IDEA.
 * User: moumita
 * Date: 20/4/14
 * Time: 4:46 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class PaidStatusArrivalCallbackTest {
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

    //@Test(expected=CallbackException.class)
    public void testPaidStatusArrivalCallbacksWithNoReferenceData() throws WorkFlowServiceException,CallbackException {
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
        String series = "created";
        workflowService.performAction(check.getId(), series, userData);
        createReferenceData(check,ReferenceData.ITEM_TYPE.STOP);
        series = "matched";
        workflowService.performAction(check.getId(), series, userData);
    }
    
    @Test
    public void testPaidStatusArrivalCallbacksWithIssuedItemTypeAndNoReferenceDataAttached() throws WorkFlowServiceException,CallbackException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
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
        String series = "created";
        String result = workflowService.performAction(check.getId(), series, userData);
        //createReferenceData(check,ReferenceData.ITEM_TYPE.PAID);
        series = "matched";
        result = workflowService.performAction(check.getId(), series, userData);
        assertTrue(result.equalsIgnoreCase("paid"));
    }
    
    @Test
    public void testPaidStatusArrivalCallbackswithNoReferenceDataAttached() throws WorkFlowServiceException,CallbackException {
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
        String series = "created";
        String result = workflowService.performAction(check.getId(), series, userData);
        createReferenceData(check,ReferenceData.ITEM_TYPE.PAID);
        series = "matched";
        result = workflowService.performAction(check.getId(), series, userData);
        assertTrue(result.equalsIgnoreCase("paid"));
    }

    private FileMetaData createFileMetaData() {
        return ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);
    }


    private ReferenceData createReferenceData(Check check,ReferenceData.ITEM_TYPE item_type) {
        FileMetaData fileMetaData = createFileMetaData();

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

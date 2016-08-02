package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 8/4/14
 * Time: 5:34 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class DuplicatePaidCheckNumberChangedStatusArrivalCallbackTest {
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
    @Ignore
    //No longer valid as duplicate reference data is no longer valid.
    public void testDuplicatePaidCheckNumberChangedStatusArrivalCallback() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(DuplicatePaidCheckNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), (new Long(System.currentTimeMillis())).toString());
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        series = "duplicatePaid,changeCheckNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    @Ignore
    //No longer valid as duplicate reference data is not allowed.
    public void testDuplicatePaidCheckNumberChangedStatusArrivalCallbackChangedCheckNumberExisting() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(DuplicatePaidCheckNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        check.setIssueDate(cal.getTime()); //Yesterday
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), addCheck((new Long(System.currentTimeMillis())).toString(), account).getCheckNumber());
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //String series = "created,matched,duplicatePaid,noPay";
        //"matched" is no longer required as issuedStatusArrivalCallback will take care of it.

        String series = "created";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        series = "duplicatePaid,changeCheckNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    private Check addCheck(String checkNumber, Account account) throws CallbackException, WorkFlowServiceException {
        Check check = new Check();
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(checkNumber);
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        Map<String, Object> userData = new HashMap<String, Object>();
        String result = workflowService.performAction(check.getId(), "created", userData);
        return check;
    }

    private ReferenceData createReferenceData(Check check) {
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
        referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
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

package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;

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
 * User: gduggirala
 * Date: 11/4/14
 * Time: 11:52 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class InvalidAmountChangeAccountNumberStatusArrivalTest {
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
    public void testInvalidAmountChangeAccountNumberStatusArrivalCallBack() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(InvalidAmountChangeAccountNumberStatusArrivalTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check);
        Map<String, Object> userData = new HashMap<String, Object>();

        //Created (a) -> Issued(s) (IssuedStatusArrivalCallback will trigger and move it to invalidAmount status)
        // -> changeAccountNumber(a) -> InvalidAmountAccountNumberChanged(s) (InvalidAmountAccountNumberChangedStatusArrival will trigger and move it back to issued)
        String series = "created,changeAccountNumber";
        for (String actionToPerform : series.split(",")) {
            if(actionToPerform.equals("changeAccountNumber")){
                userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
            }
            else{
                userData = new HashMap<String, Object>();
            }
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    public void testInvalidAmountChangeAccountNumberStatusArrivalCallBackNewCheckNumberExisting() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(InvalidAmountChangeAccountNumberStatusArrivalTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check);
        Map<String, Object> userData = null;
        Account newAccount = accountDao.findByAccountNumberAndBankId("3105978603098422040", account.getBank().getId());
        addCheck(check.getCheckNumber(), newAccount);
        //Created (a) -> Issued(s) (IssuedStatusArrivalCallback will trigger and move it to invalidAmount status)
        // -> changeAccountNumber(a) -> InvalidAmountAccountNumberChanged(s) (InvalidAmountAccountNumberChangedStatusArrival will trigger and move it back to issued)
        String series = "created,changeAccountNumber";
        for (String actionToPerform : series.split(",")) {
            if(actionToPerform.equals("changeAccountNumber")){
                userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
            }
            else{
                userData = new HashMap<String, Object>();
            }
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
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
        referenceData.setAmount(check.getIssuedAmount().add(BigDecimal.TEN));
        referenceData.setPaidDate(check.getIssueDate());
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }

    private Check addCheck(String checkNumber, Account account) throws CallbackException, WorkFlowServiceException {
        Check check = new Check();
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000).add(BigDecimal.valueOf(1000)));
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
    
    public static String shortUUID() {
    	  UUID uuid = UUID.randomUUID();
    	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    	  return Long.toString(l, Character.MAX_RADIX);
    	}
}

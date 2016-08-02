package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
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
 * User: gduggirala
 * Date: 9/4/14
 * Time: 4:46 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class VoidPaidAccountNumberChangedStatusArrivalCallbackTest {
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
    
    @Autowired
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testVoidPaidAccountNumberChangesStatusArrivalCallbackThroughVoidStatusArrivalCallback() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(VoidPaidAccountNumberChangedStatusArrivalCallbackTest.shortUUID());
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
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
        //Void(a) -> Void (s) -> VoidStatusArrivalCallback -> voidPaid(a) -> Void, Paid -> changeAccountNumber(a)
        // -> Void Paid Check number changed (a) -> VoidPaidCheckNumberChangedStatusArrivalCallback
        String series = "void,changeAccountNumber";

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
    public void testVoidPaidAccountNumberChangedStatusArrivalCallback() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(VoidPaidAccountNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        
        addExceptionalReferenceData(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "12345");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //void(a) -> Void(s) -> voidPaid(a) -> Void, Paid(s) -> changeAccountNumber(a)
        // -> Void Paid Check number changed (a) -> VoidPaidCheckNumberChangedStatusArrivalCallback
        String series = "void,voidPaid,changeAccountNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    @Test
    public void testVoidPaidAccountNumberChangesStatusArrivalCallbackThroughVoidStatusArrivalChangedCheckNumberExistingCallback() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(VoidPaidAccountNumberChangedStatusArrivalCallbackTest.shortUUID());
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
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
        //Void(a) -> Void (s) -> VoidStatusArrivalCallback -> voidPaid(a) -> Void, Paid -> changeAccountNumber(a)
        // -> Void Paid Check number changed (a) -> VoidPaidCheckNumberChangedStatusArrivalCallback
        String series = "void,changeAccountNumber";

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
    public void testVoidPaidAccountNumberChangedStatusArrivalCallbackChangedCheckNumberExisting() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(VoidPaidAccountNumberChangedStatusArrivalCallbackTest.shortUUID());
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        Map<String, Object> userData = new HashMap<String, Object>();
        userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(), "3105978603098422040");
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(createReferenceData(check).getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        //void(a) -> Void(s) -> voidPaid(a) -> Void, Paid(s) -> changeAccountNumber(a)
        // -> Void Paid Check number changed (a) -> VoidPaidCheckNumberChangedStatusArrivalCallback
        String series = "void,voidPaid,changeAccountNumber";
        for (String actionToPerform : series.split(",")) {
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);
            System.out.println("Result is " + result);
        }
    }

    private Check addCheck(String checkNumber, Account account) throws CallbackException, WorkFlowServiceException {
        Check check = new Check();
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
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
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);

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
    
    private ExceptionalReferenceData addExceptionalReferenceData(Check check) {
   	ExceptionalReferenceData exRefData = new ExceptionalReferenceData();
   	exRefData.setAccountNumber(check.getAccount().getNumber());
   	exRefData.setAmount(check.getIssuedAmount().toString());
   	exRefData.setCheckNumber(check.getCheckNumber());
   	exRefData.setExceptionStatus(ExceptionalReferenceData.EXCEPTION_STATUS.OPEN);
   	exRefData.setItemType(ReferenceData.ITEM_TYPE.PAID);
   	exRefData.setAssignedBankNumber(check.getAccount().getBank().getAssignedBankNumber());
   	exRefData.setLineNumber("1");
   	exRefData.setPaidDate("07212013");
   	exceptionalReferenceDataDao.save(exRefData);

           return exRefData;

       }
}

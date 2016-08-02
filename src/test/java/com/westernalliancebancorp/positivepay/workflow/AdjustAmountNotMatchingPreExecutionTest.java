package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.dao.*;
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
 * User: gduggirala
 * Date: 24/4/14
 * Time: 5:30 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class AdjustAmountNotMatchingPreExecutionTest {
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
    public void testAdjustmentAmountPaidAmountExceeded() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(AdjustAmountNotMatchingPreExecutionTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check, check.getIssuedAmount().add(BigDecimal.TEN));

        Map<String, Object> userData = new HashMap<String, Object>();

        //Created (a) -> Issued(s) (IssuedStatusArrivalCallback will trigger and move it to invalidAmount status)
        // -> adjustAmount(a) -> AdjustAmountNotMatchingPreExecution(Callback) ->Paid(s) (InvalidAmountCheckNumberChangedStatusArrival will trigger and move it back to issued)
        String series = "created,adjustAmount";
        for (String actionToPerform : series.split(",")) {
            userData = new HashMap<String, Object>();
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);

            System.out.println("Result is " + result);
        }
    }

    @Test
    public void testAdjustmentAmountIssuedAmountExceeded() throws CallbackException, WorkFlowServiceException {
        Account account = accountDao.findById(1l);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        Check check = new Check();
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(1000));
        check.setCheckNumber(AdjustAmountNotMatchingPreExecutionTest.shortUUID());
        check.setItemType(itemType);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        check.setIssueDate(calendar.getTime());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);
        createReferenceData(check, check.getIssuedAmount().subtract(BigDecimal.TEN));

        Map<String, Object> userData = new HashMap<String, Object>();

        //Created (a) -> Issued(s) (IssuedStatusArrivalCallback will trigger and move it to invalidAmount status)
        // -> adjustAmount(a) -> AdjustAmountNotMatchingPreExecution(Callback) ->Paid(s) (InvalidAmountCheckNumberChangedStatusArrival will trigger and move it back to issued)
        String series = "created,adjustAmount";
        for (String actionToPerform : series.split(",")) {
            userData = new HashMap<String, Object>();
            String result = workflowService.performAction(check.getId(), actionToPerform, userData);

            System.out.println("Result is " + result);
        }
    }

    private ReferenceData createReferenceData(Check check, BigDecimal amount) {
        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setAmount(amount);
        referenceData.setPaidDate(check.getIssueDate());
        referenceData.setTraceNumber(UUID.randomUUID().toString());
        referenceData.setAccount(check.getAccount());
        referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
        referenceData.setFileMetaData(ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao));
        referenceDataDao.save(referenceData);
        return referenceData;
    }
    
    public static String shortUUID() {
  	  return RandomStringUtils.random(10, -1000000000, 0,Boolean.FALSE, Boolean.TRUE);
  	}
}

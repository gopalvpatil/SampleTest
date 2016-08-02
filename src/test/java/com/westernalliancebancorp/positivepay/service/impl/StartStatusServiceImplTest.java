package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
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
import java.util.Calendar;
import java.util.UUID;

/**
 * User: gduggirala
 * Date: 22/4/14
 * Time: 9:45 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class StartStatusServiceImplTest {
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
    StartStatusService startStatusService;

    @Autowired
    ItemTypeDao itemTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

    @Test
    public void testStartService() throws CallbackException, WorkFlowServiceException {
        createCheckWithStatus(10, ItemType.CODE.I.name());
        startStatusService.processStartChecks();
        createCheckWithStatus(10, ItemType.CODE.S.name());
        startStatusService.processStartChecks();
        createCheckWithStatus(10, ItemType.CODE.V.name());
        startStatusService.processStartChecks();
    }

    @Test
    public void testStartStusService() throws Exception{
        startStatusService.processStartChecks();
    }

    private void createCheckWithStatus(int totalChecks, String statusCode) {
        Account account = accountDao.findById(1l);
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        ItemType itemType = itemTypeDao.findByCode(statusCode);
        for (int i=0;i<totalChecks;i++){
            Check check = new Check();
            check.setAccount(account);
            check.setCheckStatus(checkStatus);
            check.setIssuedAmount(BigDecimal.valueOf(1000));
            check.setCheckNumber(StartStatusServiceImplTest.shortUUID());
            check.setItemType(itemType);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            check.setIssueDate(cal.getTime()); //Yesterday
            check.setPayee("Micheal More");
            check.setRoutingNumber("111000111234");
            check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
            checkDao.save(check);
        }
    }
    
    public static String shortUUID() {
    	  UUID uuid = UUID.randomUUID();
    	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
    	  return Long.toString(l, Character.MAX_RADIX);
    	}

}

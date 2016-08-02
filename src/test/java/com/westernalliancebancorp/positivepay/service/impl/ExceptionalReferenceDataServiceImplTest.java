package com.westernalliancebancorp.positivepay.service.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class ExceptionalReferenceDataServiceImplTest {
    
    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;
    
    @Autowired
    CheckDao checkDao;
    
    @Autowired
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;
    
    @Autowired
    AccountDao accountDao;
    
    @Autowired
    CheckStatusDao checkStatusDao;
    
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    
    @Autowired
    ItemTypeDao itemTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
    }

       
    @Test
    @Ignore
    public void testfindBy() throws CallbackException, WorkFlowServiceException {
	Check check = addCheck();
	ExceptionalReferenceData exRefData = addExceptionalReferenceData(check);
	ExceptionalReferenceData exRefDataFromDB =exceptionalReferenceDataService.findBy(exRefData.getTraceNumber(), exRefData.getAmount(), exRefData.getAccountNumber());
	assertNotNull(exRefDataFromDB);
	assertTrue(exRefDataFromDB.getAccountNumber().equals(exRefData.getAccountNumber()));

    }
    
    @Test
    @Ignore
    public void testchangeZeroedCheckNumber() throws CallbackException, WorkFlowServiceException, AccountNotFoundException, ParseException {
	Check check = addCheck();
	ExceptionalReferenceData exRefData = addExceptionalReferenceData(check);
	exceptionalReferenceDataService.changeZeroedCheckNumber(exRefData.getId(), "CH001", "userComment");

    }
    
    @Test
    public void testStopReturnServicewithNoDataInCheckDetail() throws CallbackException, WorkFlowServiceException {
	//exceptionalReferenceDataService.deleteExceptionalReferenceDataRecord(30075L, "userComment");

    }
    
    private Check addCheck() {
    	Account account = accountDao.findAll().get(0);
        ItemType itemType = itemTypeDao.findByCode(ItemType.CODE.I.name());
    	Check check = new Check();
        CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", 1);
        check.setAccount(account);
        check.setCheckStatus(checkStatus);
        check.setIssuedAmount(BigDecimal.valueOf(35634646));
        check.setCheckNumber(RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE));
        check.setItemType(itemType);
        check.setIssueDate(new Date());
        check.setPayee("Micheal More");
        check.setRoutingNumber("111000111234");
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        checkDao.save(check);

        return check;
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
	exRefData.setTraceNumber("T001");
	exRefData.setPaidDate("07212013");
	exceptionalReferenceDataDao.save(exRefData);

        return exRefData;

    }
    
   
}

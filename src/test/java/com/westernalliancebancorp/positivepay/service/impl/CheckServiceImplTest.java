package com.westernalliancebancorp.positivepay.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * CheckServiceImplTest is
 *
 * @author Moumita Ghosh
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
@WebAppConfiguration
public class CheckServiceImplTest {
    @Autowired
    CheckService checkService;
    @Autowired
    AccountDao accountDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;
    @Autowired
    ReferenceDataDao referenceDataDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }
  

    @Test
    public void testChangePayee() throws Exception {
    	Check check = addCheck();
    	Check result = checkService.changePayee(check.getId(), "new payee", "payee changed for check :"+check.getId());
    	List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(result.getId());
        assertNotNull(result); 
        assertNotNull(checkHistory);
        assertTrue(result.getPayee().equals("new payee")) ;
        assertTrue(checkHistory.size()>0) ;
    }
    
    @Test
    public void testChangeDate() throws Exception {
    	Check check = addCheck();
    	Check result = checkService.changeDate(check.getId(), "07/21/2013", "date changed for check :"+check.getId());
    	List<CheckHistory> checkHistory = checkHistoryDao.findByCheckId(result.getId());
        assertNotNull(result); 
        assertNotNull(checkHistory);
        assertTrue(result.getIssueDate().equals(new Date("07/21/2013"))) ;
        assertTrue(checkHistory.size()>0) ;
    }
    
    @Test
    public void testpayDuplicateReferenceData() throws Exception 
    {
	Check check = addCheck();
	ExceptionalReferenceData exRefData = addExceptionalReferenceData(check);
    	checkService.payDuplicateReferenceData(exRefData.getId(), "userComment for pay duplicate reference data");
    	
    }
    
    @Test
    public void changeDuplicateReferenceDataCheckNumber() throws Exception 
    {
    	//checkService.changeDuplicateReferenceDataCheckNumber(10168L, "129471918001", "userComment");
    }
    
    @Test
    public void testAddHistoryEntryForNewCheck() throws Exception 
    {
	Check check = addCheck();
    	CheckHistory checkHistory = checkService.addHistoryEntryForNewCheck(check, "Manual Entry", check.getItemType());
    	assertNotNull(checkHistory);
    }
    
    @Test
    public void testcorrectZeroCheckNumber() throws Exception 
    {
	//checkService.correctZeroCheckNumber(30072L, "6666");

    }
    
    @Test
    public void testcorrectZeroCheckNumberByReferenceDataId() throws Exception 
    {
	//checkService.correctZeroCheckNumberByReferenceDataId(94666L, "8765");
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
	exRefData.setPaidDate("07212013");
	exceptionalReferenceDataDao.save(exRefData);

        return exRefData;

    }
    
    public static String shortUUID() {
  	  UUID uuid = UUID.randomUUID();
  	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
  	  return Long.toString(l, Character.MAX_RADIX);
  	}

  
}

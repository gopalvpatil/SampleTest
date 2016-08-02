package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * @author moumita
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class ExceptionalCheckJpaDaoTest {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private ExceptionalCheckDao exceptionalCheckDao;
    @Autowired
    private CheckStatusDao checkStatusDao;
	@Loggable
	private Logger logger;
	
	   @Before
	    public void setup(){
	        PositivePayThreadLocal.set("gduggira");
	    }
	
	@Test
	 public void testFindAllExceptionalChecks() {
		Long id = insertIntoExceptionCheckDetail("issued");
		List<ExceptionalCheck> expCheckList =exceptionalCheckDao.findAllExceptionalChecks(); 
		assertTrue(expCheckList.size()>0);
		ExceptionalCheck exceptionalCheckDB = exceptionalCheckDao.findById(id);
		exceptionalCheckDao.delete(exceptionalCheckDB);
	 }
	
	@Test
	 public void testFindAllExceptionalChecksByLoggedInUser() {
		Long id = insertIntoExceptionCheckDetail("issued");
		List<ExceptionalCheck> expCheckList =exceptionalCheckDao.findAllByUserName("gduggira");
		assertTrue(expCheckList.size()>0);
		ExceptionalCheck exceptionalCheckDB = exceptionalCheckDao.findById(id);
		exceptionalCheckDao.delete(exceptionalCheckDB);
	 }
	
	
	private Long insertIntoExceptionCheckDetail(String exceptionCheckStatus) {
        Account account = accountDao.findAll().get(0);
 
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("gduggira");
        auditInfo.setDateCreated(new Date());
        auditInfo.setDateModified(new Date());
        auditInfo.setModifiedBy("gduggira");

        ExceptionalCheck exCheck = new ExceptionalCheck();
        if (exceptionCheckStatus.equalsIgnoreCase("void")) {
        	exCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.VOID);
        } else {
        	exCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.ISSUED);
        }
        exCheck.setAuditInfo(auditInfo);
        exCheck.setAccountNumber(account.getNumber());
        exCheck.setCheckNumber(ExceptionalCheckJpaDaoTest.shortUUID());
        exCheck.setIssuedAmount("120002");
        exCheck.setIssueCode("1");
        try {
			exCheck.setIssueDate(DateUtils.getStringFromDate(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
       //TODO: Moumitha, please make this change.
       // exCheck.setExceptionStatus(ExceptionalCheck.EXCEPTION_STATUS.DUPLICATE_CHECK_IN_DATABASE);

        ExceptionalCheck exceptionalCheckDB =exceptionalCheckDao.save(exCheck);
        
       return exceptionalCheckDB.getId();


    }
	
	 public static String shortUUID() {
   	  UUID uuid = UUID.randomUUID();
   	  long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
   	  return Long.toString(l, Character.MAX_RADIX);
   	}
}

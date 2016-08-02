package com.westernalliancebancorp.positivepay.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.service.StopAfterPaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.TestUtils;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * Created with IntelliJ IDEA.
 * User: Moumita
 * Date: 14/8/14
 * Time: 11:34 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class StopAfterPaidImplTest {
    @Autowired
    StopAfterPaidService stopAfterPaidService;
    
    @Autowired
    TestUtils testUtils;
    
    @Autowired
    WorkflowService workflowService;
    
    @Autowired
    AccountDao accountDao;
    
    Check check;
    
    Account account;
    
    String checkNumber;
    

    @Before
    public void before() throws CallbackException, WorkFlowServiceException{
	checkNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
        account = accountDao.findAll().get(0); 
	check = testUtils.createCheck(account, checkNumber, "I");
    }
    
    @Test
    public void testStopAfterPaidwithNoPay() throws WorkFlowServiceException, CallbackException  {
	String result = null;
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(testUtils.createReferenceData(account, checkNumber, "P").getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        checkDto.setReferenceDataId(testUtils.createReferenceData(account, checkNumber, "S").getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String series = "noPay";
        for (String actionToPerform : series.split(",")) {
             result = workflowService.performAction(check, actionToPerform, userData);
        }
        assertTrue(result.equals(CheckStatus.STOP_STATUS_NAME));
        
    }
    
    	@Test
   	public void testStopAfterPaidwithPay() throws CallbackException, WorkFlowServiceException {
    	String result = null;
        Map<String, Object> userData = new HashMap<String, Object>();
        CheckDto checkDto = new CheckDto();
        checkDto.setReferenceDataId(testUtils.createReferenceData(account, checkNumber, "P").getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        checkDto.setReferenceDataId(testUtils.createReferenceData(account, checkNumber, "S").getId());
        userData.put(Constants.CHECK_DTO, checkDto);
        String series = "pay";
        for (String actionToPerform : series.split(",")) {
             result = workflowService.performAction(check, actionToPerform, userData);
        }
        assertTrue(result.equals(CheckStatus.PAID_STATUS_NAME));
    }
}

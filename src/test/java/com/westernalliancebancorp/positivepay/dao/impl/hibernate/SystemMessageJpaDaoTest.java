package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.dao.SystemMessageDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * @author moumita
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class SystemMessageJpaDaoTest {
    @Autowired
    private SystemMessageDao systemMessageDao;

	@Loggable
	private Logger logger;
	
	   @Before
	    public void setup(){
	        PositivePayThreadLocal.set("gduggira");
	    }
	
	@Test
	 public void testGetSystemMessage() {
		createSystemMessage();
		List<SystemMessage> messageList =systemMessageDao.getSystemMessages(SystemMessage.TYPE.LOGIN); 
		assertNotNull(messageList);
		assertTrue(messageList.size()>0);

	 }
	
	void createSystemMessage()
	{	
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday =cal.getTime(); 
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date tommorow = cal.getTime();
		SystemMessage message = new SystemMessage();
		message.setMessage("This is a valid message");
		message.setStartDateTime(yesterday);
		message.setEndDateTime(tommorow);
		message.setType(SystemMessage.TYPE.LOGIN);
		systemMessageDao.save(message);
	}

	

}

package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.westernalliancebancorp.positivepay.exception.SftpConnectException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.SftpPollingService;
import com.westernalliancebancorp.positivepay.utility.SFTPUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * @author Gopal Patil
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class SftpPollingServiceImplTest {

	@Loggable
	private Logger logger;
	
	@Autowired
	SftpPollingService sftpPollingService;
	
	String host = "secureftp.westernalliancebancorp.com";
    int port = 22;
    String userName = "ppuser_tpb1";
    String password = "p.L5uW)4";
	
    @Test
    @Ignore
	public void testSftpServerConnection() {
		try {
			Session session = SFTPUtility.connect(host, port, userName, password);
			logger.info(String.valueOf("Does SFTP server connected? :-"+session.isConnected()));
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}
    
    @Test
    public void testPullFilesFromSftp() {
    	try {    		
    		 Map<String, Integer> returnMap = sftpPollingService.pullFiles(Constants.CRS_PAID);
    		 
	        int itemProcessed = (Integer) returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) == null ? 0 : (Integer) returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
	        int itemsInError = (Integer) returnMap.get(Constants.ITEMS_IN_ERROR) == null ? 0 : (Integer) returnMap.get(Constants.ITEMS_IN_ERROR);
	        
	        logger.info("Items processed successfully:" + itemProcessed);	        
	        logger.info("Items in error:" + itemsInError);
    		 
		} catch (SftpConnectException e) {			
			e.printStackTrace();
		}
    }
	
}

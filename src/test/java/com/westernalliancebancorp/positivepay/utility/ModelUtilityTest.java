package com.westernalliancebancorp.positivepay.utility;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * @author Moumita Ghosh
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class ModelUtilityTest {
	
    @Autowired
    CheckHistoryDao checkHistoryDao;
    
    @Autowired
    WorkflowDao workflowDao;
    
    @Autowired
    CheckStatusDao checkStatusDao;
    
    @Autowired
    CheckDao checkDao;

    @Autowired
    AccountDao accountDao;
    
    @Autowired
    FileTypeDao fileTypeDao;
    
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    
    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }
	
    @Test
    @Ignore
    @Transactional(propagation = Propagation.REQUIRED)
    //Ignored as this contains hard coded data
    public void testGetReferenceDataFromHistory(){  
    	Check check = checkDao.findById(73090L);
    	ReferenceData latestReferenceData =ModelUtils.getReferenceDataFromHistory(check, "paid", checkStatusDao, checkHistoryDao, workflowDao);
    	assertTrue(latestReferenceData.getId().equals(59492L)) ;
    }
    
    @Test
    public void testRetrieveOrCreateCheckStatus(){  
    	CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId()),"stopPresented" , checkStatusDao);
    	assertTrue(checkStatus.getExceptionalStatus()) ;
    }
    
    @Test
    public void testRetrieveOrCreateFileType(){  
    	FileType fileType = ModelUtils.retrieveOrCreateFileType(FileType.FILE_TYPE.STOP_PRESENTED, fileTypeDao);
    	assertTrue(fileType.getName().equals(FileType.FILE_TYPE.STOP_PRESENTED)) ;
    }

}

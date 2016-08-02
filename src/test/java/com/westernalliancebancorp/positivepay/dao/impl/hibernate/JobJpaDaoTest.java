package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.JobDao;
import com.westernalliancebancorp.positivepay.dao.JobHistoryDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * @author Gopal Patil
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class JobJpaDaoTest {
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private JobHistoryDao jobHistoryDao;
	@Loggable
	private Logger logger;

    @Before
    public void setup(){
        PositivePayThreadLocal.set("admin");
    }
    @Ignore
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    public void testFindAllJobs() {       
    	try {
    		List<Job> jobList = jobDao.findAllActiveJobs();
    		logger.debug("jobList:"+jobList);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}

    }
    
    @Ignore
    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(value = true)
    public void testFindJobsCreatedBy() {       
    	try {
    		List<JobHistory> jobHistoryList = jobHistoryDao.findJobsCreatedBy("admin");
    		logger.debug("jobHistoryList:"+jobHistoryList);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    @Test
    public void testFormat() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,29);
        calendar.set(Calendar.MONTH, Calendar.MAY);
        calendar.set(Calendar.YEAR, 2014);
        System.out.println("Date received.  "+DateUtils.getFiservDateFormat(calendar.getTime()));
    }
}

package com.westernalliancebancorp.positivepay.utility;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.JobUtils;

/**
 * @author Gopal Patil
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class JobUtilityTest {
	
	@Ignore
    @Test
    public void testJobNextRunDate() throws Exception {
    	
    	Job job = new Job();
    	job.setStartDate(new Date());
    	//job.setIntervalTime("00:05");    	
    	job.setWeekly(true);
    	job.setRunDay("MON,FRI,SAT");
    	    	   	
    }
	
    @Test
    @Ignore
    public void testConfigureRunOnceCronExpression(){    	
    	try {		
			
			CronTriggerBean cronTrigger  = new CronTriggerBean();			
			cronTrigger.setBeanName("One time");	
			cronTrigger.setName("One time");
			cronTrigger.setGroup(Constants.JOB_GROUP_NAME);        
			cronTrigger.setCronExpression(JobUtils.configureRunOnceCronExpression(new Date()));
			// job set to start after 1 minute
			cronTrigger.setStartTime(DateUtils.nextDate(new Date(), 0, 0, 1, 0));
			// Job set to end in next 10 minutes
			cronTrigger.setEndTime(DateUtils.nextDate(new Date(), 0, 0, 10, 0));
					        
			cronTrigger.afterPropertiesSet();				
		} catch (Exception e) {			
			e.printStackTrace();
		}    	
    }

    @Test
    @Ignore
    public void testJob() throws InterruptedException {
        while(true){
            Thread.sleep(1000);
        }
    }

}

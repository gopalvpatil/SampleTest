package com.westernalliancebancorp.positivepay.job.impl;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.NASConnectException;
import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.DailyStopFileJobService;
import com.westernalliancebancorp.positivepay.service.SftpPollingService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * User:	Gopal Patil
 * Date:	Jan 27, 2014
 * Time:	1:02:31 PM
 */
@Component("dailyStopFileJobTask")
public class DailyStopFileJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

    @Autowired
    private DailyStopFileJobService dailyStopFileJobService;
    
    @Autowired
    private SftpPollingService sftpPollingService;

	@Override
	public String processTask(JobExecutionContext executionContext)
			throws SchedulerException {
        try {
            logger.debug("Executing DailyStopFileJobTask");
            JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
            Map<String, Integer> returnMap = new HashMap<String, Integer>();
            boolean isNasConnected = true;            
            try {
            	returnMap = dailyStopFileJobService.pullStopFile();
            } catch(NASConnectException nce) {
            	isNasConnected = false;
            	nce.printStackTrace();
        	}    
            
            if(!isNasConnected) {
            	returnMap = sftpPollingService.pullFiles(Constants.DAILY_STOP);
            }            
            
            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
            if(returnMap.containsKey(Constants.FILE_STATUS_CODE)){
                jobDataMap.put(Constants.FILE_STATUS_CODE, returnMap.get(Constants.FILE_STATUS_CODE));
            }            
        } catch (Exception e) {
            logger.error(String.format("Exception thrown by: %s Exception: %s", executionContext.getJobDetail().getName(), e.getMessage()), e);
            return JobStatus.FAILED.name();
        }
        logger.info(String.format("Job : %s is finished...", executionContext.getJobDetail().getName()));
        return JobStatus.COMPLETED.name();
    }

}

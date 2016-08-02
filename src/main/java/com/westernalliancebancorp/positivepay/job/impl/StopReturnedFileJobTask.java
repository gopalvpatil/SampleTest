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
import com.westernalliancebancorp.positivepay.service.SftpPollingService;
import com.westernalliancebancorp.positivepay.service.StopReturnedFileJobService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * User:	Gopal Patil
 * Date:	Jan 27, 2014
 * Time:	1:02:31 PM
 */
@Component("stopReturnedFileJobTask")
public class StopReturnedFileJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

    @Autowired
    private StopReturnedFileJobService stopReturnedFileJobService;

    @Autowired
    private SftpPollingService sftpPollingService;
    
	@Override
	public String processTask(JobExecutionContext executionContext)
			throws SchedulerException {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();            
        boolean isNasConnected = true;   
        try {
            logger.debug("Executing StopReturnedFileJobTask");
            try {
            	returnMap = stopReturnedFileJobService.pullStopReturnedFile();
            } catch(NASConnectException nce) {
            	isNasConnected = false;
            	nce.printStackTrace();
        	}    
            
            if(!isNasConnected) {
            	returnMap = sftpPollingService.pullFiles(Constants.CRS_PAID);
            }             
            
            jobDataMap.put(Constants.ITEMS_IN_ERROR, returnMap.get(Constants.ITEMS_IN_ERROR));
            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY));
            if(returnMap.containsKey(Constants.FILE_STATUS_CODE)){
                jobDataMap.put(Constants.FILE_STATUS_CODE, returnMap.get(Constants.FILE_STATUS_CODE));
            }  
        } catch (Exception e) {
            logger.error("Exception thrown by: " + executionContext.getJobDetail().getName()
                    + " Exception: " + e.getMessage(), e);
            return JobStatus.FAILED.name();
        }
        logger.info("Job : " + executionContext.getJobDetail().getName() + " is finished...");
        return JobStatus.COMPLETED.name();
    }

}

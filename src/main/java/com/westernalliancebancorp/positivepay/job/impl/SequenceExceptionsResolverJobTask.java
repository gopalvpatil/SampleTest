package com.westernalliancebancorp.positivepay.job.impl;

import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * User:	Moumita Ghosh
 * Date:	May 28, 2014
 * Time:	1:02:51 PM
 */
@Component("sequenceExceptionsResolverJobTask")
public class SequenceExceptionsResolverJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;
    
    @Autowired
    VoidAfterStopService voidAfterStopService;

    @Autowired
    VoidAfterPaidService voidAfterPaidService;

    @Autowired
    IssuedAfterStopService issuedAfterStopService;
    
    @Autowired
    IssuedAfterVoidService issuedAfterVoidService;
    
    @Autowired
    StaleVoidService staleVoidService;

    @Autowired
    StopAfterPaidService stopAfterPaidService;
    
    @Autowired
    VoidStopService voidstopService;

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.job.AbstractProcessTask#processTask(org.quartz.JobExecutionContext)
	 */
	@Override
	public String processTask(JobExecutionContext executionContext)
			throws SchedulerException {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
        int itemsProcessed = 0;
        int itemsInError = 0;
        try {
            logger.debug("Executing SequenceExceptionsResolverJobTask");
            logger.debug("Executing VoidAfterStop service");
            
            returnMap = voidAfterStopService.markChecksVoidAfterStop();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("VoidAfterStop service Completed");
            logger.debug("Executing VoidAfterPaid service");
            
            returnMap = voidAfterPaidService.markChecksVoidAfterPaid();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("VoidAfterPaid service Completed");
            logger.debug("Executing IssuedAfterStop service");
            
            returnMap = issuedAfterStopService.markChecksIssuedAfterStop();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("IssuedAfterStop service Completed");
            logger.debug("Executing IssuedAfterVoid service");
            
            returnMap = issuedAfterVoidService.markChecksIssuedAfterVoid();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("IssuedAfterVoid service Completed");
            logger.debug("Executing StopAfterPaid service");
            
            returnMap = stopAfterPaidService.markChecksStopAfterPaid();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("StopAfterPaid service Completed");
            logger.debug("Executing StaleVoid service");
            
            returnMap = staleVoidService.markChecksStaleVoid();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("StaleVoid service Completed");
            logger.debug("Executing VoidStop service");
            
            //No need to write "stopAfterVoid" as we are dealing with that in "VoidStop" ServiceImpl
            returnMap = voidstopService.markChecksVoidStop();
            if(jobDataMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY) != null) {
                itemsProcessed = itemsProcessed + returnMap.get(Constants.ITEMS_PROCESSED_SUCCESSFULLY);
            }
            if(jobDataMap.get(Constants.ITEMS_IN_ERROR) != null) {
                itemsInError = itemsInError +returnMap.get(Constants.ITEMS_IN_ERROR);
            }
            logger.debug("VoidStop service Completed");
            jobDataMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
            jobDataMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessed);
        } catch (Exception e) {
            logger.error("Exception thrown by: " + executionContext.getJobDetail().getName()
                    + " Exception: " + e.getMessage(), e);
            return JobStatus.FAILED.name();
        }
        logger.info("Job : " + executionContext.getJobDetail().getName() + " is finished...");
        return JobStatus.COMPLETED.name();
    }

}

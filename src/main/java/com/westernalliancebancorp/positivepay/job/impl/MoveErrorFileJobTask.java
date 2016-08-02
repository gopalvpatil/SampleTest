package com.westernalliancebancorp.positivepay.job.impl;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * User:	Gopal Patil
 * Date:	Jan 27, 2014
 * Time:	1:03:15 PM
 */
@Component
public class MoveErrorFileJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.job.AbstractProcessTask#processTask(org.quartz.JobExecutionContext)
	 */
	@Override
	public String processTask(JobExecutionContext executionContext)
			throws SchedulerException {
        try {
            logger.debug("Executing MoveErrorFileJobTask");
            //call CheckService
        } catch (Exception e) {
            logger.error("Exception thrown by: " + executionContext.getJobDetail().getName()
                    + " Exception: " + e.getMessage(), e);
            return JobStatus.FAILED.name();
        }
        logger.info("Job : " + executionContext.getJobDetail().getName() + " is finished...");
        return JobStatus.COMPLETED.name();
    }

}

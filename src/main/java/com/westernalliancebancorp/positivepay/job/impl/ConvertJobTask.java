package com.westernalliancebancorp.positivepay.job.impl;

import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.JobCriteriaDataDao;
import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.AbstractProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.JobStatus;

/**
 * @author gpatil
 *
 */
@Component("convertJobTask")
public class ConvertJobTask extends AbstractProcessTask {

    @Loggable
    private Logger logger;

    @Autowired
    private JobCriteriaDataDao jobCriteriaDataDao;


    @Override
    public String processTask(JobExecutionContext executionContext)
            throws SchedulerException {
        try {
            logger.debug("Executing MakeStaleJobTask");
            JobDataMap jobDataMap = executionContext.getJobDetail().getJobDataMap();
            Long jobStepId = (Long) jobDataMap.get(Constants.JOB_STEP_ID);
            List<JobCriteriaData> jobCriteriaDataList = jobCriteriaDataDao.findByJobId(jobStepId);  
        } catch (Exception e) {
            logger.error(String.format("Exception thrown by: %s Exception: %s", executionContext.getJobDetail().getName(), e.getMessage()), e);
            return JobStatus.FAILED.name();
        }
        logger.info(String.format("Job : %s is finished...", executionContext.getJobDetail().getName()));
        return JobStatus.COMPLETED.name();
    }

}
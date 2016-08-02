package com.westernalliancebancorp.positivepay.job;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;

import org.quartz.JobExecutionContext;

/**
 * @author Gopal Patil
 */
public interface ProcessTask {
    void process(JobExecutionContext executionContext) throws SchedulerException;
    void saveJobHistory(JobExecutionContext executionContext, String status) throws SchedulerException, Exception;
    void updateJobHistory(JobExecutionContext executionContext, String status) throws SchedulerException, Exception;
    void saveJobStepHistory(JobExecutionContext executionContext, String status) throws SchedulerException, Exception;
    void updateJob(JobExecutionContext executionContext) throws SchedulerException, Exception;
	void setPrincipal(String principal);    
}

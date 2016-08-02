package com.westernalliancebancorp.positivepay.job;

import org.quartz.JobExecutionContext;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * @author Gopal Patil
 */
public interface OneTimeProcessTask {
    void process(JobExecutionContext executionContext) throws SchedulerException;
    void setPrincipal(String principal);
    void setSource(PositivePayThreadLocal.SOURCE source);
}

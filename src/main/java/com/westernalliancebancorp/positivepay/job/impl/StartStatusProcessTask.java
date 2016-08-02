package com.westernalliancebancorp.positivepay.job.impl;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.job.AbstractOneTimeProcessTask;
import com.westernalliancebancorp.positivepay.job.OneTimeProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal.SOURCE;
import com.westernalliancebancorp.positivepay.service.ExceptionStatusService;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * This class process all checks in background having start status
 * @author Gopal Patil
 *
 */
@Component("startStatusProcessTask")
public class StartStatusProcessTask extends AbstractOneTimeProcessTask{

    @Loggable
    private Logger logger;
	
    @Autowired
    StartStatusService startStatusService;
    
    @Autowired
    ExceptionStatusService exceptionStatusService;
    
	@Override
	public void process(JobExecutionContext executionContext)
			throws SchedulerException {		
        try {
            startStatusService.processStartChecks();
			exceptionStatusService.processExceptionChecks();
		} catch (CallbackException e) {
			logger.error("Exception while processing checks from Start Status:", e);
		} catch (WorkFlowServiceException e) {
			logger.error("Exception while processing checks from Start Status:", e);
		} catch (RuntimeException re) {
            logger.error("Exception while processing checks from Start Status:", re);
        }
	}
	
	
}

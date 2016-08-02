package com.westernalliancebancorp.positivepay.job.impl;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.OneTimeProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

@Component
public class CheckProcessJobDetailBean  extends QuartzJobBean {
	
	@Loggable
    private Logger logger;

    private ApplicationContext applicationContext;

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {    	
    	JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    	String principal = (String) jobDataMap.get(Constants.PRINCIPAL);
    	PositivePayThreadLocal.SOURCE source= (PositivePayThreadLocal.SOURCE) jobDataMap.get(Constants.SOURCE);
    	this.applicationContext = (ApplicationContext) jobDataMap.get("appContextKey");   	
    	OneTimeProcessTask processTask = applicationContext.getBean("startStatusProcessTask", OneTimeProcessTask.class);
    	try {
    		processTask.setPrincipal(principal);
    		processTask.setSource(source);
			processTask.process(context);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}   
    }

}

package com.westernalliancebancorp.positivepay.job;

import org.quartz.JobExecutionContext;

import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal.SOURCE;

public abstract class AbstractOneTimeProcessTask implements OneTimeProcessTask{

    @Override
    public abstract void process(JobExecutionContext executionContext)
	    throws SchedulerException ;

    @Override
    public void setPrincipal(String principal) {
        if (principal == null) {
            throw new NullPointerException("Principal value passed is null please check");
        }
        PositivePayThreadLocal.set(principal);
    }

	@Override
	public void setSource(SOURCE source) {
	    PositivePayThreadLocal.setSource(source.name());
	    
	}

}

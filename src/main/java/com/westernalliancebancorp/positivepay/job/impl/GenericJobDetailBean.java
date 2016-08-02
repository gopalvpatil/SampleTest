package com.westernalliancebancorp.positivepay.job.impl;

import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.JobStepDao;
import com.westernalliancebancorp.positivepay.exception.SchedulerException;
import com.westernalliancebancorp.positivepay.job.ProcessTask;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User: gduggirala
 * Date: 9/4/14
 * Time: 2:53 PM
 */
@Component
public class GenericJobDetailBean extends QuartzJobBean {
   
	@Loggable
    private Logger logger;
	
	private JobStepDao jobStepDao;

    private ApplicationContext applicationContext;
    
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {    	
    	JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    	Long jobId = (Long) jobDataMap.get(Constants.JOB_DB_ID);
    	String principal = (String) jobDataMap.get(Constants.PRINCIPAL);
    	this.applicationContext = (ApplicationContext) jobDataMap.get("appContextKey");
    	jobStepDao = applicationContext.getBean("jobStepDao", JobStepDao.class);
    	List<JobStep> jobSteps =  jobStepDao.findAllJobStepsBy(jobId);
    	Integer i= Integer.valueOf(jobSteps.size());
		jobDataMap.put(Constants.JOB_ACTUAL_START_TIME, new Date());
		jobDataMap.put(Constants.JOB_STEP_EXCEPTION_FLAG, Boolean.FALSE);
		jobDataMap.put(Constants.JOB_FAILED_FLAG, Boolean.FALSE);
    	if(!jobSteps.isEmpty()) {
    		jobDataMap.put(Constants.JOB_STEPS_SIZE, i);
    		for(JobStep jobStep : jobSteps) {
		    	if(jobStep != null) { 
		    		jobDataMap.put(Constants.JOB_STEP_ID, jobStep.getId());
		    		jobDataMap.put(Constants.JOB_STEPS_COUNT, i);
		    		jobDataMap.put(Constants.JOB_STEP_ACTUAL_START_TIME, new Date());
		    		ProcessTask processTask = null;
		    		if(jobStep.getJobActionType() != null) {
			    		processTask = applicationContext.getBean(jobStep.getJobActionType().getSpringBeanName(), ProcessTask.class);
		    		}else { //Job Type does not have any action type so instantiate job on the basis of job type		    				
		    			if(jobStep.getJobType() != null) {
		    				String jobTypeName = jobStep.getJobType().getName();
			    			if(jobTypeName.equals(Constants.ACTION)) {		    				
			    				processTask = applicationContext.getBean("actionJobTask", ProcessTask.class);
			    			} else if(jobTypeName.equals(Constants.CONVERT)) {
			    				processTask = applicationContext.getBean("convertJobTask", ProcessTask.class);
			    			} else if(jobTypeName.equals(Constants.EXTERNAL_PROGRAM)) {
			    				processTask = applicationContext.getBean("externalProgramJobTask", ProcessTask.class);
			    			} else if(jobTypeName.equals(Constants.FILE_MANAGEMENT)) {
			    				processTask = applicationContext.getBean("fileManagementJobTask", ProcessTask.class);
			    			} else if(jobTypeName.equals(Constants.LOAD)) {
			    				processTask = applicationContext.getBean("loadJobTask", ProcessTask.class);
			    			} else if(jobTypeName.equals(Constants.REPORT_EXTRACT)) {
			    				processTask = applicationContext.getBean("reportExtractJobTask", ProcessTask.class);
			    			} else {
			    				throw new JobExecutionException("Job Id: "+jobId+" does not have job type");
			    			}	
		    			} else{
		    				throw new JobExecutionException("Job Id: "+jobId+" does not have job type");
		    			}
		    		}		    		
		    		try {
		        		processTask.setPrincipal(principal);
						processTask.process(context);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
		    	}
		    	i--;
    		}    		
    	}else{
    		throw new JobExecutionException("Job Id: "+jobId+" does not have job steps");
    	}
    }

}

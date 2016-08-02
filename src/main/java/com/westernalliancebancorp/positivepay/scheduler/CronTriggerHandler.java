package com.westernalliancebancorp.positivepay.scheduler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.util.StringUtils;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.JobUtils;


/**
* This class is to create cron trigger as per dynamic values from DB
* * * * * * *
| | | | | | | 
| | | | | | +-- Year              (range: 1900-3000)
| | | | | +---- Day of the Week   (range: 1-7, 1 standing for Monday)
| | | | +------ Month of the Year (range: 1-12)
| | | +-------- Day of the Month  (range: 1-31)
| | +---------- Hour              (range: 0-23)
| +------------ Minute            (range: 0-59)
+-------------- Second            (range: 0-59)
* 
* @author Gopal Patil
* 
*/
public class CronTriggerHandler implements InitializingBean{
	
	@Loggable
	private Logger logger;
	
	private List<CronTriggerBean> triggers;
	
	@Autowired
	private JobHandler jobHandler;	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("CronTriggerHandler afterPropertiesSet");
		try {	
			List<JobDetailBean> jobDetails = jobHandler.getJobDetails();
			triggers = new ArrayList<CronTriggerBean>();
			
			List<Job> jobList = jobHandler.getJobList();
			
			if(!jobList.isEmpty() && !jobDetails.isEmpty()) {
				for(int i=0, j=0; i < jobList.size() && j < jobDetails.size(); i++, j++) {				
					JobDetailBean jobDetail = jobDetails.get(j);
					Job job = jobList.get(i);
					CronTriggerBean cronTrigger = null;
					boolean isJobAlive = false;
					// Schedule a one time job if server restarted before triggering it i.e consider job start date in this case
					if(job.getFrequency().equals(Constants.ONE_TIME)) {
						if(job.getStartTime() != null && StringUtils.hasText(job.getStartTime())) {
							//Someone has made starttime datatype changed from string to time, so written getDateTimeFromDB to support 00:00:00 format
							isJobAlive = DateUtils.isJobAlive(DateUtils.getDateTimeFromDB(job.getStartDate(), job.getStartTime()), job.getTimezone());
						}else{
							isJobAlive = DateUtils.isJobAlive(job.getStartDate(), job.getTimezone());
						}
					}else {					
						if(job.getEndTime() != null && StringUtils.hasText(job.getEndTime().toString())) {
							//Someone has made starttime datatype changed from string to time, so written getDateTimeFromDB to support 00:00:00 format
							isJobAlive = DateUtils.isJobAlive(DateUtils.getDateTimeFromDB(job.getEndDate(), job.getEndTime()), job.getTimezone());
						}else{
							isJobAlive = DateUtils.isJobAlive(job.getEndDate(), job.getTimezone());
						}
					}					
					if(job.isActive() && isJobAlive) {
						if(job.getFrequency().equals(Constants.ONE_TIME)) {
							cronTrigger = this.createOneTimeCronTrigger(job, Constants.ONE_TIME_JOB_NAME, jobDetail, false);
						} else {
							cronTrigger = this.createNewCronTrigger(job, jobDetail);
						}
						triggers.add(cronTrigger);	
						logger.debug("Job Name : "+cronTrigger.getName()+" has CronExpression : "+job.getCronExpression());						
					}else{
						logger.info("Job :"+job.getName()+" can not schedule because its expired on "+ job.getEndDate());
					}					
				}
			}
		}catch(Exception e) {
			logger.error(Log.event(Event.JOB_RUN_ERROR, "Error occured while creating cron expression",e),e);	        	
			e.printStackTrace();
		}
		
	}
	
	public CronTriggerBean createNewCronTrigger(Job newJob, JobDetailBean jobDetail) throws ParseException, Exception{	
		logger.debug("CronTriggerHandler createNewCronTrigger");
		PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Batch.name());
		JobDataMap map = jobDetail.getJobDataMap();
		map.put(Constants.JOB_DB_ID, newJob.getId());
		String principal = null;		
		try {
			principal = SecurityUtility.getPrincipal();
		} catch(RuntimeException re) {
			logger.info("No user info found either in security context or ThreadLocal, please check");
			principal = newJob.getAuditInfo().getCreatedBy();
			SecurityUtility.setPrincipal(principal);	
		}	
		map.put(Constants.CREATED_BY, principal);		
		map.put(Constants.PRINCIPAL, principal);
		CronTriggerBean cronTrigger  = new CronTriggerBean();		
		cronTrigger.setBeanName(newJob.getName());	
		cronTrigger.setName(newJob.getName());
		cronTrigger.setGroup(Constants.JOB_GROUP_NAME);        
		cronTrigger.setCronExpression(newJob.getCronExpression());
		//TIMEZONE changes		
		Date jobStartDate = DateUtils.convertDateToServerTimezone(newJob.getStartDate(), newJob.getTimezone());
		Date jobEndDate = DateUtils.convertDateToServerTimezone(newJob.getEndDate(), newJob.getTimezone());
		Date currentDate = DateUtils.convertCurrentDateToServerTimezone(new Date());
		
		if(jobStartDate.before(currentDate)) {
			cronTrigger.setStartTime(currentDate);
		}else{
			cronTrigger.setStartTime(jobStartDate);
		}		
		cronTrigger.setEndTime(jobEndDate);
		cronTrigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
		cronTrigger.setJobDetail(jobDetail);			        
		cronTrigger.afterPropertiesSet();				
		logger.debug("Job Name : "+cronTrigger.getName()+" has CronExpression : "+newJob.getCronExpression());			
		return cronTrigger;		
	}

	public CronTriggerBean createOneTimeCronTrigger(Job newJob, String jobName, JobDetailBean jobDetail, boolean isRunSelected) throws ParseException, Exception {	
	    	PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Batch.name());
		JobDataMap map = jobDetail.getJobDataMap();
		map.put(Constants.JOB_DB_ID, newJob.getId());	
		String principal = null;		
		try {
			principal = SecurityUtility.getPrincipal();
		} catch(RuntimeException re) {
			logger.info("No user info found either in security context or ThreadLocal, please check");
			principal = newJob.getAuditInfo().getCreatedBy();
			SecurityUtility.setPrincipal(principal);	
		}		
		map.put(Constants.CREATED_BY, principal);		
		map.put(Constants.PRINCIPAL, principal);
		CronTriggerBean cronTrigger  = new CronTriggerBean();			
		cronTrigger.setBeanName(newJob.getName() + "_" + jobName);	
		cronTrigger.setName(newJob.getName() + "_" + jobName);
		cronTrigger.setGroup(Constants.JOB_GROUP_NAME);  
		
		Date date = null;		
		if(isRunSelected) {
			date = DateUtils.convertCurrentDateToServerTimezone(new Date());
		} else{
			//TIMEZONE changes
			Date startDate = newJob.getStartDate();
			date = DateUtils.convertDateToServerTimezone(startDate, newJob.getTimezone());			
		}		
		cronTrigger.setCronExpression(JobUtils.configureRunOnceCronExpression(date));	
		// job set to start after 3 seconds
		cronTrigger.setStartTime(DateUtils.nextDate(date, 0, 0, 0, 3));
		// Job set to end after next 10 minutes
		cronTrigger.setEndTime(DateUtils.nextDate(date, 0, 0, 10, 0));

		cronTrigger.setJobDetail(jobDetail);			        
		cronTrigger.afterPropertiesSet();				
		logger.debug("Job Name : "+cronTrigger.getName()+" has CronExpression : "+cronTrigger.getCronExpression());		
		return cronTrigger;
	}
	
	public CronTriggerBean createCheckProcessCronTrigger(JobDetailBean jobDetail) throws ParseException, Exception {
	    	PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Batch.name());
		JobDataMap map = jobDetail.getJobDataMap();
		map.put(Constants.PRINCIPAL, SecurityUtility.getPrincipal());
		map.put(Constants.SOURCE, PositivePayThreadLocal.SOURCE.Batch);
		Date date = new Date();
		CronTriggerBean cronTrigger  = new CronTriggerBean();			
		cronTrigger.setBeanName(Constants.CHECK_PROCESS_JOB);	
		cronTrigger.setName(Constants.CHECK_PROCESS_JOB);
		cronTrigger.setGroup(Constants.JOB_GROUP_NAME);        
		cronTrigger.setCronExpression(JobUtils.configureRunOnceCronExpression(date));
		// job set to start after 3 seconds
		cronTrigger.setStartTime(DateUtils.nextDate(date, 0, 0, 0, 3));
		// Job set to end after next 10 minutes
		cronTrigger.setEndTime(DateUtils.nextDate(date, 0, 0, 10, 0));

		cronTrigger.setJobDetail(jobDetail);			        
		cronTrigger.afterPropertiesSet();				
		logger.debug("Job Name : "+cronTrigger.getName()+" has CronExpression : "+cronTrigger.getCronExpression());		
		return cronTrigger;
	}

	public List<CronTriggerBean> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<CronTriggerBean> triggers) {
		this.triggers = triggers;
	}
}

package com.westernalliancebancorp.positivepay.scheduler;

import java.text.ParseException;
import java.util.List;

import org.quartz.Scheduler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * This class is to schedule job as per job details fetched from DB 
 * @author Gopal Patil
 * 
 */
public class PositivePaySchedulerFactoryBean extends SchedulerFactoryBean implements InitializingBean{
	
	@Autowired
	private JobHandler jobHandler;
	
	@Autowired
	private CronTriggerHandler cronTriggerHandler;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		List<JobDetailBean> jobDetailsList = jobHandler.getJobDetails();
		List<CronTriggerBean> triggersList = cronTriggerHandler.getTriggers();
		
		JobDetailBean[] jobDetails = new JobDetailBean[jobDetailsList.size()];
		CronTriggerBean[] triggers = new CronTriggerBean[triggersList.size()];	
		
		jobDetails = jobDetailsList.toArray(jobDetails);
		triggers = triggersList.toArray(triggers);
		
		super.setJobDetails(jobDetails);
		super.setTriggers(triggers);
		super.afterPropertiesSet();
	}

	/**
	 * @param newJob
	 * @throws ParseException
	 * @throws Exception
	 */
	public void scheduleNewJob(Job newJob) throws ParseException, Exception{		
		JobDetailBean jobDetailBean = jobHandler.createNewJobDetail(newJob);
		CronTriggerBean cronTriggerBean = cronTriggerHandler.createNewCronTrigger(newJob, jobDetailBean);
		Scheduler scheduler = super.getScheduler();
		scheduler.scheduleJob(jobDetailBean, cronTriggerBean);
	}
	
	/**
	 * @param newJob
	 * @throws ParseException
	 * @throws Exception
	 */
	public void unScheduleJob(Job newJob) throws ParseException, Exception{		
		Scheduler scheduler = super.getScheduler();
		scheduler.unscheduleJob(newJob.getName(), Constants.JOB_GROUP_NAME);
	}
	
	/**
	 * @param job
	 * @param jobName TODO
	 * @throws Exception 
	 * @throws ParseException 
	 */
	public void scheduleOneTimeJob(Job job, String jobName, boolean isRunSelected) throws ParseException, Exception{
		JobDetailBean jobDetailBean = jobHandler.createOneTimeJobDetail(job, jobName);
		CronTriggerBean cronTriggerBean = cronTriggerHandler.createOneTimeCronTrigger(job, jobName, jobDetailBean, isRunSelected);
		Scheduler scheduler = super.getScheduler();
		scheduler.scheduleJob(jobDetailBean, cronTriggerBean);
	}
	
	/**
	 * @param newJob
	 * @throws ParseException
	 * @throws Exception
	 */
	public void unScheduleOneTimeJob(Job newJob) throws ParseException, Exception{		
		Scheduler scheduler = super.getScheduler();
		scheduler.unscheduleJob(newJob.getName() + "_" + Constants.ONE_TIME_JOB_NAME, Constants.JOB_GROUP_NAME);
	}	
	
	public void scheduleCheckProcessJob() throws Exception{
		JobDetailBean jobDetailBean = jobHandler.createCheckProcessJobDetail();
		CronTriggerBean cronTriggerBean = cronTriggerHandler.createCheckProcessCronTrigger(jobDetailBean);
		Scheduler scheduler = super.getScheduler();
		scheduler.scheduleJob(jobDetailBean, cronTriggerBean);
	}

	/**
	 * @return the cronTriggerHandler
	 */
	public CronTriggerHandler getCronTriggerHandler() {
		return cronTriggerHandler;
	}

	/**
	 * @param cronTriggerHandler the cronTriggerHandler to set
	 */
	public void setCronTriggerHandler(
			CronTriggerHandler cronTriggerHandler) {
		this.cronTriggerHandler = cronTriggerHandler;
	}

}

package com.westernalliancebancorp.positivepay.scheduler;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.JobDetailBean;
import org.springframework.util.StringUtils;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.service.JobService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
*
* @author Gopal Patil
* 
*/
public abstract class JobHandler implements InitializingBean{
	
	@Loggable
	private Logger logger;
	
	@Autowired
	JobService jobService;
	
	List<Job> jobList;
	
	private List<JobDetailBean> jobDetails;
   
    @Value("${job.execution.locker.delete.all.entries}")
    private String jobExecutionLockerDeleteAllEntries;
    
    protected abstract JobDetailBean createGenericProcessJobDetailBean();
    protected abstract JobDetailBean createCheckProcessJobDetailBean();
    
	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("Get Job Details");		
		try {			
			/*if(jobExecutionLockerDeleteAllEntries.equalsIgnoreCase("true")) {
				jobService.deleteAllJobExecutionLockers();
			}*/	
			//At server up time, remove all job execution locks specific to server.
			jobService.deleteJobExecutionLockersOnServer(InetAddress.getLocalHost().getHostName());
			
			jobDetails = new ArrayList<JobDetailBean>();
			jobList = new ArrayList<Job>();

			List<Job> activeJobList = jobService.findAllActiveJobs();
			
			if(!activeJobList.isEmpty()) {
				for(Job job : activeJobList) {					
					boolean isJobAlive = false;
					// Schedule a one time job if server restarted before triggering it i.e consider job start date in this case
					if(job.getFrequency().equals(Constants.ONE_TIME)) {
						if(job.getStartTime() != null && StringUtils.hasText(job.getStartTime().toString())) {
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
					
					if(isJobAlive) {
						jobList.add(job);						
						JobDetailBean genericJobDetailBean = createGenericProcessJobDetailBean();						
						if(job.getFrequency().equals(Constants.ONE_TIME)) {
							genericJobDetailBean.setBeanName(job.getName() + "_" + "OneTime");
							genericJobDetailBean.setName(job.getName() + "_" + "OneTime");
							
						}else{
							genericJobDetailBean.setBeanName(job.getName());
							genericJobDetailBean.setName(job.getName());
						}						
						genericJobDetailBean.afterPropertiesSet();
						jobDetails.add(genericJobDetailBean);									
					}
				}
			}			
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}

	public JobDetailBean createNewJobDetail(Job job) throws ParseException{	
		JobDetailBean genericJobDetailBean = null;
		boolean isJobAlive = false;
		if(job.getEndTime() != null && StringUtils.hasText(job.getEndTime().toString())) {			
			isJobAlive = DateUtils.isJobAlive(DateUtils.getDateTime(job.getEndDate(), job.getEndTime()), job.getTimezone());
		}else{
			isJobAlive = DateUtils.isJobAlive(job.getEndDate(), job.getTimezone());
		}		
		if(job.isActive() && isJobAlive) {
			genericJobDetailBean = createGenericProcessJobDetailBean();
			genericJobDetailBean.setBeanName(job.getName());
			genericJobDetailBean.setName(job.getName());				
		}
		return genericJobDetailBean;
	}
	
	public JobDetailBean createOneTimeJobDetail(Job job, String jobName) throws ParseException{	
		JobDetailBean genericJobDetailBean = null;
		if(job.isActive()) {
			genericJobDetailBean = createGenericProcessJobDetailBean();
			genericJobDetailBean.setBeanName(job.getName() + "_" + jobName);
			genericJobDetailBean.setName(job.getName() + "_" + jobName);				
		}
		return genericJobDetailBean;
	}
	
	public JobDetailBean createCheckProcessJobDetail() throws ParseException{	
		JobDetailBean checkProcessJobDetailBean = createCheckProcessJobDetailBean();
		checkProcessJobDetailBean.setBeanName(Constants.CHECK_PROCESS_JOB);
		checkProcessJobDetailBean.setName(Constants.CHECK_PROCESS_JOB);				
		return checkProcessJobDetailBean;
	}
	
	public List<Job> getJobList() {
		return jobList;
	}

	public void setJobList(List<Job> jobList) {
		this.jobList = jobList;
	}	

	public List<JobDetailBean> getJobDetails() {
		return jobDetails;
	}

	public void setJobDetails(List<JobDetailBean> jobDetails) {
		this.jobDetails = jobDetails;
	}

}

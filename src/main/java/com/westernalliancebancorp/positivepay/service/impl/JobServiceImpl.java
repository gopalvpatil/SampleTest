package com.westernalliancebancorp.positivepay.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.service.ResetCaching;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.JobActionTypeDao;
import com.westernalliancebancorp.positivepay.dao.JobCriteriaDataDao;
import com.westernalliancebancorp.positivepay.dao.JobDao;
import com.westernalliancebancorp.positivepay.dao.JobExecutionLockerDao;
import com.westernalliancebancorp.positivepay.dao.JobStatusTypeDao;
import com.westernalliancebancorp.positivepay.dao.JobStepDao;
import com.westernalliancebancorp.positivepay.dao.JobTypeDao;
import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.dto.JobDtoBuilder;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.model.JobExecutionLocker;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStatusType;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobType;
import com.westernalliancebancorp.positivepay.scheduler.PositivePaySchedulerFactoryBean;
import com.westernalliancebancorp.positivepay.service.JobHistoryService;
import com.westernalliancebancorp.positivepay.service.JobService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * @author Gopal Patil
 * 
 */
@Service
public class JobServiceImpl implements JobService {

	/** The logger object */
	@Loggable
	private Logger logger;

	@Autowired
	private JobDao jobDao;

	@Autowired
	private JobStepDao jobStepDao;

	@Autowired
	private JobTypeDao jobTypeDao;
	
	@Autowired
	private JobActionTypeDao jobActionTypeDao;

	@Autowired
	JobHistoryService jobHistoryService;

	@Autowired
	JobExecutionLockerDao jobExecutionLockerDao;

	@Autowired
	PositivePaySchedulerFactoryBean ppScheduler;
	
	@Autowired
	private JobStatusTypeDao jobStatusTypeDao;
	
	@Autowired
	private JobCriteriaDataDao jobCriteriaDataDao;

	@Autowired
    private ResetCaching resetCaching;
	
	@Autowired
	private BatchDao batchDao;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void save(Job job) {
		JobDtoBuilder builder = new JobDtoBuilder();
		jobDao.save(job);
		JobHistory jobHistory;
		try {
			jobHistory = builder.getJobHistory(job);			
			JobStatusType jobStatusTypeExist = jobHistoryService.findJobStatusTypeByCode(Constants.CREATED);
			
			if(jobStatusTypeExist == null) {
				//save job status first			
				JobStatusType jobStatusType = new JobStatusType();
				jobStatusType.setActive(true);
				jobStatusType.setName("");
				jobStatusType.setStatusCode("CREATED");				
				AuditInfo auditInfo = new AuditInfo();
	            String name = SecurityUtility.getPrincipal();
	            auditInfo.setCreatedBy(name);
	            auditInfo.setDateCreated(new Date());
	            auditInfo.setDateModified(new Date());
	            auditInfo.setModifiedBy(name);	            
	            jobStatusType.setAuditInfo(auditInfo);	            
	            jobStatusTypeDao.save(jobStatusType);
	            jobHistory.setJobStatusType(jobStatusType);
			} else {
				jobHistory.setJobStatusType(jobStatusTypeExist);
			}			
			
			jobHistoryService.save(jobHistory);
			// Add new job details to scheduler
			if(job.getFrequency().equals(Constants.ONE_TIME)) {
				ppScheduler.scheduleOneTimeJob(job, Constants.ONE_TIME_JOB_NAME, false);
			}else {
				ppScheduler.scheduleNewJob(job);
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void update(Job job) {		
		boolean isCronExpressionChanged = false;
		JobDto jobDto = this.findLastJobConfigurationBy(Long.valueOf(job.getId()));
		if (job.getCronExpression().equalsIgnoreCase(jobDto.getCronExpression())) {
			isCronExpressionChanged = false;
		} else {
			isCronExpressionChanged = true;
		}		
		//Last run time will be previous configuration
		if(jobDto.getJobLastRunDate() != null && StringUtils.hasText(jobDto.getJobLastRunDate())) {
			try {
				job.setLastRunDate(DateUtils.getDateTimeFromString(DateUtils.getWALFormatDateTime(jobDto.getJobLastRunDate())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 		
		//Update job with latest configuration
		jobDao.update(job);

		try {
			JobDtoBuilder builder = new JobDtoBuilder();
			JobHistory jobHistory = builder.getJobHistory(job);
			
			JobStatusType jobStatusTypeExist;
			if (jobDto.getJobStatusType() != null && StringUtils.hasText(jobDto.getJobStatusType())) {
				jobStatusTypeExist = jobHistoryService.findJobStatusTypeBy(jobDto.getJobStatusType());
			} else {	
				jobStatusTypeExist = jobHistoryService.findJobStatusTypeByCode(Constants.CREATED);
			}			
			
			if(jobStatusTypeExist != null) {
				jobHistory.setJobStatusType(jobStatusTypeExist);
			} else {
				//save job status first			
				JobStatusType jobStatusType = new JobStatusType();
				jobStatusType.setActive(true);
				jobStatusType.setName("");
				jobStatusType.setStatusCode("CREATED");				
				AuditInfo auditInfo = new AuditInfo();
	            String name = SecurityUtility.getPrincipal();
	            auditInfo.setCreatedBy(name);
	            auditInfo.setDateCreated(new Date());
	            auditInfo.setDateModified(new Date());
	            auditInfo.setModifiedBy(name);	            
	            jobStatusType.setAuditInfo(auditInfo);	            
	            jobStatusTypeDao.save(jobStatusType);
	            jobHistory.setJobStatusType(jobStatusType);
			}	
			
			jobHistoryService.update(jobHistory);
			
			if(isCronExpressionChanged) {				
				// Unscheduled old job
				if(job.getFrequency().equals(Constants.ONE_TIME)) {
					ppScheduler.unScheduleOneTimeJob(job);
				}else {
					ppScheduler.unScheduleJob(job);
				}
				// Add new updated job details to scheduler
				if(job.getFrequency().equals(Constants.ONE_TIME)) {
					ppScheduler.scheduleOneTimeJob(job, Constants.ONE_TIME_JOB_NAME, false);
				}else {
					ppScheduler.scheduleNewJob(job);
				}
			}
		} catch (Exception e) {
			logger.debug("excep1:"+e);
			e.printStackTrace();
		}		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<Job> findAllActiveJobs() {		
		return jobDao.findAllActiveJobs();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public Job findJobById(String jobId) {
		Job job = jobDao.findByJobId(Long.valueOf(jobId));
		return job;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<JobStep> findJobStepByJobId(Long jobId) {
		return jobStepDao.findAllJobStepsBy(jobId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteAllJobExecutionLockers() {
		jobExecutionLockerDao.deleteAll();		
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteJobExecutionLockersOnServer(String serverName) {
		jobExecutionLockerDao.deleteJobExecutionLockersOnServer(serverName);		
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void runSelectedJobs(List<String> selectedIdsList) {
		for (String jobId : selectedIdsList) {
			Job job = jobDao.findById(Long.valueOf(jobId));
			try {
				ppScheduler.scheduleOneTimeJob(job, Constants.RUN_SELECTED_JOB_NAME, true);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteSelectedJobById(Long id) {
		Job job = jobDao.findById(id);		
		List<JobExecutionLocker> jobExecutionLockerList = jobExecutionLockerDao.findByJobId(id);		
		 if (!jobExecutionLockerList.isEmpty()) {
			 for (JobExecutionLocker jobExecutionLocker : jobExecutionLockerList) {
				 jobExecutionLockerDao.delete(jobExecutionLocker);
			 }
		 }
		
		if (job != null) {
			jobDao.delete(job);
			try {
				ppScheduler.unScheduleJob(job);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}

	@Transactional(propagation = Propagation.REQUIRED)
    @Override
    @Cacheable(cacheName = "findJobTypeById")
	public JobType findJobTypeById(Long id) {
		return jobTypeDao.findById(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
    @Override
    @Cacheable(cacheName = "findJobActionTypeById")
	public JobActionType findJobActionTypeById(Long id) {
		return jobActionTypeDao.findById(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Cacheable(cacheName = "JobType")
	public List<JobType> findActiveJobTypes() {
		return jobTypeDao.findActiveJobTypes();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
    @Cacheable(cacheName = "findJobActionTypeByJobTypeId")
	public List<JobActionType> findJobActionTypeByJobTypeId(Long jobTypeId) {		
		return jobTypeDao.findJobActionTypeByJobTypeId(jobTypeId);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Cacheable(cacheName = "JobActionType")
	public List<JobActionType> findJobActionTypes() {		
		return jobTypeDao.findJobActionTypes();
	}

	@Override
	public List<JobCriteriaData> fetchCriteriaByStep(Long stepId) {
		return jobCriteriaDataDao.findByJobId(stepId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String findLastCronExpressionByJobId(Long id) {
		return batchDao.findLastCronExpressionByJobId(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobDto findLastJobConfigurationBy(Long jobId) {
		return batchDao.findLastJobConfigurationBy(jobId);
	}
	
}

package com.westernalliancebancorp.positivepay.dto;

import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.CronExpression;
import org.springframework.util.StringUtils;

import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.JobUtils;

/**
 * @author Gopal Patil
 */
public class JobDtoBuilder {

	/**
	 * @param jobDto
	 * @return
	 * @throws Exception
	 */
	public Job getJobFromDto(JobDto jobDto) throws Exception {
		Job job = new Job();
		if (jobDto.getJobId() != null) {
			job.setId(jobDto.getJobId());
		}
		job.setName(jobDto.getJobName());
		job.setDescription(jobDto.getJobDescription());

		int hr = 0;
		int min = 0;
		if (jobDto.getIntervalTime() != null
				&& StringUtils.hasText(jobDto.getIntervalTime())) {
			String[] timeFrequency = jobDto.getIntervalTime().split(":");
			hr = Integer.parseInt(timeFrequency[0]);
			min = Integer.parseInt(timeFrequency[1]);
		}
		Date walFormatCurrentDate = DateUtils.getWALFormatDate(new Date());
		Date walFromatJobStartDate = DateUtils.getDateFromString(jobDto.getJobStartDate());
		Date walFormatCurrentDateTime = DateUtils.getWALFormatDateTime(new Date());

		if (hr > 0 || min > 0) {
			if (walFromatJobStartDate.compareTo(walFormatCurrentDate) <= 0) {
				job.setStartDate(walFormatCurrentDateTime);
			} else {
				job.setStartDate(DateUtils.getDateFromString(jobDto.getJobStartDate()));
			}
			job.setEndDate(DateUtils.getDate235959(DateUtils.getDateFromString(jobDto.getJobEndDate())));
		} else {
			job.setStartDate(DateUtils.getDateTime(DateUtils.getDateFromString(jobDto.getJobStartDate()), jobDto.getJobRunTime()));
			
			if(jobDto.getJobEndDate() != null && StringUtils.hasText(jobDto.getJobEndDate())) {
				if(jobDto.getJobEndRunTime() != null && StringUtils.hasText(jobDto.getJobEndRunTime())) {
					job.setEndDate(DateUtils.getDateTime(DateUtils.getDateFromString(jobDto.getJobEndDate()), jobDto.getJobEndRunTime()));
					job.setEndTime(jobDto.getJobEndRunTime());
				} else {
					job.setEndDate(DateUtils.getDateTime(DateUtils.getDateFromString(jobDto.getJobEndDate()), jobDto.getJobRunTime()));
					job.setEndTime("");
				}
			} else {
				//Job type frequency is one time and set to end after 10 minutes
				job.setEndDate(DateUtils.nextDate(new Date(), 0, 0, 10, 0));
			}
		}

		job.setFrequency(jobDto.getJobFrequency());
		job.setIndefinite(jobDto.isIndefinitely());
		job.setWeekly(jobDto.isWeekly());
		job.setActive(true);
		job.setRunDay(jobDto.getJobRunDay());
		job.setStartTime(jobDto.getJobRunTime());
		//User timezone from UI
		job.setTimezone(jobDto.getTimezone());
		job.setIntervalTime(jobDto.getIntervalTime());
		
		if(job.getFrequency().equals(Constants.ONE_TIME)) {
			//TIMEZONE changes
			Date currentDate = DateUtils.convertCurrentDateToServerTimezone(new Date());
			job.setCronExpression(JobUtils.configureRunOnceCronExpression(currentDate));
		} else {
			job.setCronExpression(JobUtils.configureCronExpression(job));
		}
		
		AuditInfo auditInfo = new AuditInfo();
		String name = SecurityUtility.getPrincipal();
		auditInfo.setCreatedBy(name);
		auditInfo.setDateCreated(new Date());
		auditInfo.setDateModified(new Date());
		job.setAuditInfo(auditInfo);
		
		//TIMEZONE changes		
		Date walFormatUserJobStartDate =  DateUtils.convertDateToUserTimezone(job.getStartDate(), job.getTimezone());	
		Date walFormatUserJobEndDate =  DateUtils.convertDateToUserTimezone(job.getEndDate(), job.getTimezone());
		job.setStartDate(walFormatUserJobStartDate);
		job.setEndDate(walFormatUserJobEndDate);
		
		if(job.getFrequency().equals(Constants.ONE_TIME)) {
			Date nextJobFireTime = DateUtils.convertDateToUserTimezone(job.getStartDate(), job.getTimezone());
			job.setNextRunDate(nextJobFireTime);
		} else {
			CronExpression expression = new CronExpression(job.getCronExpression());
			Date jobNextRunDate = expression.getNextValidTimeAfter(new Date());
			Date nextJobFireTime = DateUtils.convertDateToUserTimezone(jobNextRunDate, job.getTimezone());
			job.setNextRunDate(nextJobFireTime);
		}	
		return job;
	}

	public JobStep getJobStepFromDto(JobDto jobDto) {
		JobStep jobStep = new JobStep();
		jobStep.setName(jobDto.getJobStepName());
		jobStep.setDescription(jobDto.getJobStepDescription());

		Set<JobCriteriaData> jobCriteriaDataCollection = new HashSet<JobCriteriaData>();

		if (jobDto.getJobBankCriteria() != null
				&& !jobDto.getJobBankCriteria().isEmpty()) {
			JobCriteriaData jobCriteriaData = new JobCriteriaData();
			jobCriteriaData.setCriteriaName(JobCriteriaData.CRITERIA_NAME.BANK);
			jobCriteriaData.setValue(jobDto.getJobBankCriteria());
			jobCriteriaDataCollection.add(jobCriteriaData);
			jobCriteriaData.setJobStep(jobStep);
			jobStep.setJobCriteriaData(jobCriteriaDataCollection);
		}

		if (jobDto.getJobCustomerCriteria() != null
				&& !jobDto.getJobCustomerCriteria().isEmpty()) {
			JobCriteriaData jobCriteriaData = new JobCriteriaData();
			jobCriteriaData
					.setCriteriaName(JobCriteriaData.CRITERIA_NAME.COMPANY);
			jobCriteriaData.setValue(jobDto.getJobCustomerCriteria());
			jobCriteriaDataCollection.add(jobCriteriaData);
			jobCriteriaData.setJobStep(jobStep);
			jobStep.setJobCriteriaData(jobCriteriaDataCollection);
		}

		if (jobDto.getJobAccountCriteria() != null
				&& !jobDto.getJobAccountCriteria().isEmpty()) {
			JobCriteriaData jobCriteriaData = new JobCriteriaData();
			jobCriteriaData
					.setCriteriaName(JobCriteriaData.CRITERIA_NAME.ACCOUNT);
			jobCriteriaData.setValue(jobDto.getJobAccountCriteria());
			jobCriteriaDataCollection.add(jobCriteriaData);
			jobCriteriaData.setJobStep(jobStep);
			jobStep.setJobCriteriaData(jobCriteriaDataCollection);
		}

		AuditInfo auditInfo = new AuditInfo();
		String name = SecurityUtility.getPrincipal();
		auditInfo.setCreatedBy(name);
		auditInfo.setDateCreated(new Date());
		auditInfo.setDateModified(new Date());
		jobStep.setAuditInfo(auditInfo);
		return jobStep;
	}


	public JobDto getJobDtoFromJob(Job job) throws Exception {
		JobDto jobDto = new JobDto();
		if (job.getId() != null) {
			jobDto.setJobId(job.getId());
		}
		jobDto.setJobName(job.getName());
		jobDto.setJobDescription(job.getDescription());
		jobDto.setJobStartDate(DateUtils.getStringFromDate(job
				.getStartDate()));
		jobDto.setJobEndDate(DateUtils.getStringFromDate(job.getEndDate()));
		jobDto.setJobFrequency(job.getFrequency());
		jobDto.setIndefinitely(job.isIndefinite());
		jobDto.setWeekly(job.isWeekly());
		jobDto.setActive(job.isActive());
		jobDto.setJobRunDay(job.getRunDay());
		jobDto.setJobRunTime(job.getStartTime());
		jobDto.setJobEndRunTime(job.getEndTime());
		jobDto.setTimezone(job.getTimezone());
		jobDto.setIntervalTime(job.getIntervalTime());
		//jobDto.setJobTypeId(job.getJ);
		if (job.getAuditInfo() != null) {
			jobDto.setCreatedBy(job.getAuditInfo().getCreatedBy());
			jobDto.setDateCreated(DateUtils.getStringFromDateTime(job
					.getAuditInfo().getDateCreated()));
		}
		
		// Setting details for JobSteps
		return jobDto;
	}

	
	/**
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public JobHistory getJobHistory(Job job) throws Exception {
		JobHistory jobHistory = new JobHistory();
		if(job.getFrequency().equals(Constants.ONE_TIME)) {
			jobHistory.setScheduledStartDate(job.getStartDate());
		} else {
			CronExpression expression = new CronExpression(job.getCronExpression());
			Date jobNextRunDate = expression.getNextValidTimeAfter(job
					.getStartDate());
			jobHistory.setScheduledStartDate(jobNextRunDate);
		}		
		jobHistory.setActualStartTime(new Date());
		jobHistory.setExecutingOnMachine(InetAddress.getLocalHost().getHostName());
		
		jobHistory.setJob(job);
		return jobHistory;
	}

	/**
	 * @param jobHistories
	 * @return
	 */
	public List<JobDto> getJobDtoListFromHistory(List<JobHistory> jobHistories) {
		List<JobDto> jobDtoList = new ArrayList<JobDto>();
		for (JobHistory jobHistory : jobHistories) {
			try {
				JobDto jobDto = new JobDto();
				jobDto.setJobId(jobHistory.getJob().getId());
				jobDto.setJobName(jobHistory.getJob().getName());
				jobDto.setActive(jobHistory.getJob().isActive());
				
				if(jobHistory.getJobStatusType() != null && jobHistory.getJobStatusType().getName() != null) {
					jobDto.setJobStatusType(jobHistory.getJobStatusType().getName());
				}else {
					jobDto.setJobStatusType("");
				}				
				if (jobHistory.getJob() != null && jobHistory.getJob().getStartDate() != null) {
					jobDto.setJobStartDateTime(DateUtils.getStringFromDateTime(jobHistory.getJob().getStartDate()));
				} else {
					jobDto.setJobStartDateTime("");
				}
				if (jobHistory.getJob() != null && jobHistory.getJob().getEndDate() != null) {
					jobDto.setJobEndDateTime(DateUtils.getStringFromDateTime(jobHistory.getJob().getEndDate()));
				} else {
					jobDto.setJobEndDateTime("");
				}
				if (jobHistory.getJob() != null && jobHistory.getActualStartTime() != null) {
					jobDto.setJobActualStartTime(DateUtils.getStringFromDateTime(jobHistory.getActualStartTime()));
				} else {
					jobDto.setJobActualStartTime("");
				}
				if (jobHistory.getJob() != null && jobHistory.getActualEndTime() != null) {
					jobDto.setJobActualEndTime(DateUtils.getStringFromDateTime(jobHistory.getActualEndTime()));
				} else {
					jobDto.setJobActualEndTime("");
				}
				if (jobHistory.getJob() != null && jobHistory.getJob().getNextRunDate() != null) {
					jobDto.setJobNextRunDate(DateUtils.getStringFromDateTime(jobHistory.getJob().getNextRunDate()));
				} else {
					jobDto.setJobNextRunDate("");
				}				
				if (jobHistory.getJob() != null && jobHistory.getJob().getLastRunDate() != null) {
					jobDto.setJobLastRunDate(DateUtils.getStringFromDateTime(jobHistory.getJob().getLastRunDate()));
				} else {
					jobDto.setJobLastRunDate("");
				}
				
				jobDtoList.add(jobDto);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return jobDtoList;
	}

}

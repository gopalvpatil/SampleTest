package com.westernalliancebancorp.positivepay.web.validator;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * Validator class for Job Scheduler
 * @author Gopal Patil
 *
 */
@Component
public class JobValidator implements Validator{

	@Loggable
	private Logger logger;
	
	@Override
	public boolean supports(Class<?> clazz) {		
		return JobDto.class.equals(clazz);
	}

	@Override
	public void validate(Object jobObj, Errors errors) {		
		JobDto jobDto = (JobDto) jobObj;	
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobName", "jobName.required");
		
		//1. If job is set to run for specific time i.e. interval time is not set
		String[] time1 = jobDto.getJobRunTime().split(":");
		//String[] time2 = time1[1].split(" ");		
		int jobRunTimeHr = Integer.parseInt(time1[0]);
		//int jobRunTimeMin = Integer.parseInt(time2[0]);
		
		//2. If job is set to run for interval time i.e. start time is not set
		int intervalMinutes = 0;
		int intervalHours = 0;
		
		if(jobDto.getIntervalTime() != null && StringUtils.hasText(jobDto.getIntervalTime())) {
			String[] intervalTime = jobDto.getIntervalTime().split(":");
			intervalHours = Integer.parseInt(intervalTime[0]);
			intervalMinutes = Integer.parseInt(intervalTime[1]); 
		}
		
		if(jobDto.getJobFrequency().equals(Constants.RECURRING)) {
			if(!jobDto.isIndefinitely()) {
				if(jobDto.getJobEndDate() == null || !StringUtils.hasText(jobDto.getJobEndDate())) {
					errors.rejectValue("jobEndDate","jobEndDate.required");
				}else{
					try {
						if(DateUtils.getDateFromString(jobDto.getJobEndDate()).before(DateUtils.getDateFromString(jobDto.getJobStartDate()))) {
							errors.rejectValue("jobEndDate","jobEndDate.lesser");
						}
					} catch (ParseException e) {
						e.printStackTrace();
					} 	
				}
			}
	
			if((jobDto.getJobRunDay() == null || !StringUtils.hasText(jobDto.getJobRunDay().toString())) && !jobDto.isWeekly()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobRunDay", "jobRunDay.required");
			}
		}
		
		if(jobDto.getJobStartDate() == null || !StringUtils.hasText(jobDto.getJobStartDate())) {
			errors.rejectValue("jobStartDate","jobStartDate.required");
		} else {			
			if(!jobDto.isEditMode()) {
				// 1. Validate if job is set to run at specific time and edit mode is not enabled
				if(jobRunTimeHr > 0) {
					try {
						Date jobStartDateTimeZone = DateUtils.getDateTime(DateUtils.getDateFromString(jobDto.getJobStartDate()), jobDto.getJobRunTime());						
						//Date jobStartDateTimeZone = DateUtils.convertToServerTimezoneDate(jobStartDateTime);						
						Date currentDateTimeZone = DateUtils.convertDateToUserTimezone(new Date(), jobDto.getTimezone());
						if(jobStartDateTimeZone.before(currentDateTimeZone)) {
							errors.rejectValue("jobStartDate","jobStartDate.lesser");
						}
					} catch (ParseException e) {				
						e.printStackTrace();
					}
					
				} else {
					// 2. Validate if job is set to run at interval time and edit mode is not enabled
					if(intervalMinutes > 0 || intervalHours > 0) {
						try {
							Date jobStartDateTimeZone = DateUtils.getBeginningOfDayTime(DateUtils.getDateFromString(jobDto.getJobStartDate()));						
							Date currentDateTimeZone = DateUtils.getBeginningOfDayTime(DateUtils.convertDateToUserTimezone(new Date(), jobDto.getTimezone()));
							
							if(jobStartDateTimeZone.before(currentDateTimeZone)) {
								errors.rejectValue("jobStartDate","jobStartDate.lesser");
							}
						} catch (ParseException e) {				
							e.printStackTrace();
						}
					}
				}
			} else {
				//In edit mode, if current job start date time and previous job start date time not same then verify it should not be in past
				// 1. Validate if job is set to run at specific time and edit mode is enabled
				if(jobRunTimeHr > 0) {
					try {
						Date currentJobStartDateTime = DateUtils.getDateTime(DateUtils.getDateFromString(jobDto.getJobStartDate()), jobDto.getJobRunTime());
						Date olderJobStartDateTime = DateUtils.getDateTimeFromString(jobDto.getOlderStartDateTime());						
						if(olderJobStartDateTime.compareTo(currentJobStartDateTime) != 0) {
							Date currentDateTimeZone = DateUtils.convertDateToUserTimezone(new Date(), jobDto.getTimezone());
							if(currentJobStartDateTime.before(currentDateTimeZone)) {
								errors.rejectValue("jobStartDate","jobStartDate.lesser");
							}
						}
					} catch (ParseException e) {				
						e.printStackTrace();
					}
				} else {
					// 2. Validate if job is set to run at interval time and edit mode is enabled
					if(intervalMinutes > 0 || intervalHours > 0) {
						try {
							Date currentJobStartDateTime = DateUtils.getBeginningOfDayTime(DateUtils.getDateFromString(jobDto.getJobStartDate()));	
							Date olderJobStartDateTime = DateUtils.getBeginningOfDayTime(DateUtils.getDateFromString(jobDto.getOlderStartDateTime()));
							
							if(olderJobStartDateTime.compareTo(currentJobStartDateTime) != 0) {
								Date currentDateTimeZone = DateUtils.getBeginningOfDayTime(DateUtils.convertDateToUserTimezone(new Date(), jobDto.getTimezone()));
								if(currentJobStartDateTime.before(currentDateTimeZone)) {
									errors.rejectValue("jobStartDate","jobStartDate.lesser");
								}
							}						
						} catch (ParseException e) {				
							e.printStackTrace();
						}
					}
				}
			}
		}	
		
		if(jobDto.isSavePage()) {
			if(jobDto.getJobTypeId() == null) {
				errors.rejectValue("jobTypeId","jobType.required");
			}					
		}		
				
	}

}

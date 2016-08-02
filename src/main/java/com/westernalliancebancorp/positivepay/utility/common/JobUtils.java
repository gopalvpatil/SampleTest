package com.westernalliancebancorp.positivepay.utility.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.westernalliancebancorp.positivepay.model.Job;

/**
 * This class used for Job related common functionalities
 * @author Gopal Patil
 *
 */
@Component
public class JobUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JobUtils.class);
		
	public static String configureCronExpression(Job job) throws Exception {	
		logger.debug("Start Cron Expression for New Job :: "+job.getName());
		SimpleDateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");	
		StringBuilder cronExpressionBuilder =  new StringBuilder();			
		StringBuilder loggerStringBuilder = new StringBuilder();
		String runOnDays = job.getRunDay();
		Date jobEndDateTime = null;
		
		Date startDateTime = DateUtils.getDateTime(job.getStartDate(), job.getStartTime());	
		Date jobStartDateTime = DateUtils.convertDateToServerTimezone(startDateTime, job.getTimezone());
		
		if(job.getEndTime() != null) {
			Date endDateTime = DateUtils.getDateTime(job.getEndDate(), job.getEndTime());
			jobEndDateTime = DateUtils.convertDateToServerTimezone(endDateTime, job.getTimezone());
			logger.debug("User UI Dates which is not converted to server TZ job end date is: "+endDateTime);
		} else {
			Date endDateTime = DateUtils.getDateTime(job.getEndDate(), job.getStartTime());
			jobEndDateTime = DateUtils.convertDateToServerTimezone(endDateTime, job.getTimezone());
			logger.debug("User UI Dates which is not converted to server TZ job end date is: "+endDateTime);
		}
		logger.debug("User UI Dates job start date is: "+startDateTime);
		logger.debug("User UI Dates Converted to server timezone job start date is: "+jobStartDateTime+" And job end is: "+jobEndDateTime);
		logger.debug("Server Timezone default is: "+TimeZone.getDefault().getDisplayName());
		
		String hour = "";
		String minute = "";		
		
		//1. if Job is running specific to interval time
		if (job.getIntervalTime() != null && StringUtils.hasText(job.getIntervalTime())) {			
			String[] timeFrequency = job.getIntervalTime().split(":");
			hour = timeFrequency[0];
			minute = timeFrequency[1];
			logger.debug("Job will run in interval with Hour: "+hour+" And minutes: "+minute);
			
			//if job run on every given minutes
			if(minute != null && StringUtils.hasText(minute) && !minute.equals("00")) {
				cronExpressionBuilder.append("0").append(" ").append("0/").append(minute).append(" ").append("*").append(" ");
			} else {//if job run on every given hours
				cronExpressionBuilder.append("0").append(" ").append("0").append(" ").append("0/").append(hour).append(" ");
			}			
		} else {
			//2. if job is run at specific time given
			if(job.getStartTime() != null && StringUtils.hasText(job.getStartTime())) {				
				//TIMEZONE changes
				String twentyFourHrTime = TWENTY_FOUR_TF.format(jobStartDateTime);
				logger.debug("Timezone TWENTY_FOUR_TF is: "+twentyFourHrTime);
				//String twentyFourHrTime = DateUtils.convertTo24HoursFormat(job.getStartTime());				
				hour = twentyFourHrTime.split(":")[0];
				minute = twentyFourHrTime.split(":")[1];
				cronExpressionBuilder.append("0").append(" ").append(minute).append(" ").append(hour).append(" ");	
			}
		}			
		
		loggerStringBuilder.append("Job : "+job.getName()+" will run from "+jobStartDateTime);
		loggerStringBuilder.append("\n");
		if(job.isIndefinite()) {				
			//do not consider year i.e. year = "*"
			cronExpressionBuilder.append("?").append(" ").append("*").append(" ");		
			appendDailyOrWeeklyExpression(job.isWeekly(), cronExpressionBuilder, DateUtils.getDay(job.getStartDate()), runOnDays, loggerStringBuilder);
			loggerStringBuilder.append(" at "+job.getStartTime());
			cronExpressionBuilder.append("*");
			loggerStringBuilder.append(" indinitely i.e. No Job End date");
		} else {			
			logger.debug("Job : "+job.getName()+" will run from "+jobStartDateTime+" to "+jobEndDateTime);
			//0 15 10 ? * FRI,MON 2013-2015		
			cronExpressionBuilder.append("?").append(" ").append("*").append(" ");				
			appendDailyOrWeeklyExpression(job.isWeekly(), cronExpressionBuilder, DateUtils.getDay(job.getStartDate()), runOnDays, loggerStringBuilder);				
			cronExpressionBuilder.append(DateUtils.getYear(jobStartDateTime)+"-"+DateUtils.getYear(jobEndDateTime));
			loggerStringBuilder.append(" at "+job.getStartTime());
			loggerStringBuilder.append(" till "+jobEndDateTime);		
		}		
		String cronExpression = cronExpressionBuilder.toString();			
		logger.debug(loggerStringBuilder.toString());
		logger.debug("CronExpression : "+cronExpression);		
		logger.debug(":: End Cron Expression for Job "+job.getName());
		return cronExpression;		
	}
	
	/**
	 * @param isWeekly
	 * @param cronExpressionBuilder
	 * @param jobStartDateDay
	 * @param runOnDays
	 * @param loggerStringBuilder
	 */
	public static void appendDailyOrWeeklyExpression(boolean isWeekly, StringBuilder cronExpressionBuilder, 
			String jobStartDateDay, String runOnDays, StringBuilder loggerStringBuilder) {
		if(isWeekly) {		
			loggerStringBuilder.append("And will run weekly on every, " + jobStartDateDay);
			// 0 10 12 ? * MON * 
			cronExpressionBuilder.append(jobStartDateDay).append(" ");
		} else {
			// 0 10 12 ? * MON,TUE,FRI * 
			loggerStringBuilder.append("And will run on days: " + runOnDays);
			cronExpressionBuilder.append(runOnDays).append(" ");
		}
	}
	
	/**
	 * @param 
	 * @return
	 * @throws Exception
	 */
	public static String configureRunOnceCronExpression(Date date) throws Exception {		
		logger.debug("Start Cron Expression for Job run once");		
		StringBuilder cronExpressionBuilder =  new StringBuilder();
		//wal day format
		//SimpleDateFormat wdyf = new SimpleDateFormat("EEE");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//SET TO RUN AFTER 1 SECDONDS
		int sec = cal.get(Calendar.SECOND) + 1;
		int minute = cal.get(Calendar.MINUTE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int currentDate = cal.get(Calendar.DATE);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);
		//String currentDay = wdyf.format(cal.getTime());
        
        // Minute and Second values must be between 0 and 59
        if(sec >= 60) {
        	sec = 0;
        	minute = minute + 1;
        }
        
        if(minute >= 60) {
        	minute = 0;
        	hour = hour + 1;
        }
	
		/*
		 * 
		 * 0 15 13 2 4 ? 2014
		 * sec, min, hour, date month, days, year
		 * The command 2015 will execute at 1:15pm on the 2nd of April.
		 * 
		 */
		
		// Add sec, minute and hour
		cronExpressionBuilder.append(sec).append(" ").append(minute).append(" ").append(hour).append(" ");	
		// Add date and month
		cronExpressionBuilder.append(currentDate).append(" ").append(currentMonth).append(" ");		
		// Add Day SUN to SAT 
		cronExpressionBuilder.append("?").append(" ");		
		// Add year
		cronExpressionBuilder.append(currentYear);
		
		String cronExpression = cronExpressionBuilder.toString();
		logger.debug("CronExpression : "+cronExpression);		
		logger.debug(":: End Cron Expression for Job run once");		
		return cronExpression;
	}	
}

package com.westernalliancebancorp.positivepay.utility.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

/**
 * This DateUtils class is for date manipulations
 * @author Gopal Patil
 *
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {
	//wal date time format
	private static final SimpleDateFormat wdtf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	//wal date format
	public static final SimpleDateFormat wdf = new SimpleDateFormat("MM/dd/yyyy");
	//wal day format
	private static final SimpleDateFormat wdyf = new SimpleDateFormat("EEE");
	//wal year format
	private static final SimpleDateFormat wyf = new SimpleDateFormat("yyyy");	
	//date format in dailyStop file
	private static final SimpleDateFormat dailyStopFileDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	//Date format for CRS paid file
	private static final SimpleDateFormat crsPaidFileDateFormat = new SimpleDateFormat("MMddyyyy");
	//Fiserve date format
    private static final SimpleDateFormat fServeDateFormat = new SimpleDateFormat("MM-dd-yyyy");
	//System message date format
    private static final SimpleDateFormat systemMessageDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
	//Replace with KK:mma if you want 0-11 interval
	private static final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mm a");
	//Replace with kk:mm if you want 1-24 interval
	private static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat("HH:mm");
	//Support 00:00:00	
	private static final DateFormat tf = new SimpleDateFormat("hh:mm:ss");
	private static final SimpleDateFormat dbWALFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");
	
	private DateUtils() {}
	
    public static String getFiservDateFormat(Date postedDate) throws ParseException {
        return fServeDateFormat.format(postedDate);
    }

	public static Date getWALFormatDateTime(Date currentDate) throws ParseException {	
		String dateString = wdtf.format(currentDate);	       	  
		Date date = wdtf.parse(dateString);
		return date;
	}	

	public static Date getWALFormatDate(Date currentDate) throws ParseException {	
		String dateString = wdf.format(currentDate);	       	  
		Date date = wdf.parse(dateString);
		return date;
	}

    public static String getWALFormatDateString(Date currentDate) throws ParseException {
        return wdf.format(currentDate);
    }
    
    public static Date getSystemMessageFormatDate(Date currentDate) throws ParseException {
        String dateString = systemMessageDateFormat.format(currentDate);
        Date date = systemMessageDateFormat.parse(dateString);
        return date;
    }

	public static Date getDailyStopFileDateFormat(String dateString) throws ParseException {
		return dailyStopFileDateFormat.parse(dateString);
	}

    public static String getDailyStopFileStringFormat(Date date) throws ParseException {
        return dailyStopFileDateFormat.format(date);
    }
	
	public static Date getCRSPaidFileDateFormat(String dateString) throws ParseException {
		return crsPaidFileDateFormat.parse(dateString);
	}

    public static String getCRSPaidFileStringFormat(Date date) throws ParseException {
        return crsPaidFileDateFormat.format(date);
    }
    
	public static Date getStopPresentedFileDateFormat(String dateString) throws ParseException {
		return wdf.parse(dateString);
	}
	
	public static Date getUploadedFileDateFormat(String dateString) throws ParseException {
		String year = dateString.substring(dateString.lastIndexOf("/")+1);
		if (year.length() == 2) {
			return wdf.parse(dateString);
		}
		return wdf.parse(dateString);
	}

	public static Date nextDate(Date currentDate, int days, int hr, int min, int sec) throws ParseException {		
        Calendar cal = GregorianCalendar.getInstance(Locale.US);
        cal.setTime(currentDate);
        if(days != 0)
        	cal.add(Calendar.DATE, days); 
        if(hr != 0)
        	cal.add(Calendar.HOUR, hr); 
        if(min != 0)
        	cal.add(Calendar.MINUTE, min); 
        if(min != 0)
        	cal.add(Calendar.SECOND, sec); 
		return getWALFormatDateTime(cal.getTime());
	}	

	public static Date getDate235959(Date date) throws ParseException {
	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    c.set(Calendar.HOUR, 23);
	    c.set(Calendar.MINUTE, 59);
	    c.set(Calendar.SECOND, 59);
	    c.set(Calendar.MILLISECOND, 999);
	    return getWALFormatDateTime(c.getTime());
	}

	public static Date getDateTime(Date currentDate, String runTime) throws ParseException {		
		String currentDateString = wdf.format(currentDate);		
		StringBuffer sb = new StringBuffer(currentDateString);
		sb.append(" "+runTime);        	  
		Date nextRun = wdtf.parse(sb.toString());
		return nextRun;
	}
	
	//DataType is changed for startTime and EndTime in DB, hence runtime is 00:00:00, 
	// so above method getDateTime can not be used
	public static Date getDateTimeFromDB(Date currentDate, String runTime) throws ParseException {		
		String currentDateString = wdf.format(currentDate);
		String currentTimeString = TWELVE_TF.format(tf.parse(runTime));
		StringBuffer sb = new StringBuffer(currentDateString);
		sb.append(" "+currentTimeString);        	  
		Date nextRun = wdtf.parse(sb.toString());
		return nextRun;
	}	

	public static Date getDateFromString(String dateString) throws ParseException {
		Date date = wdf.parse(dateString);
		return date;
	}	

	public static Date getDateTimeFromString(String dateTimeString) throws ParseException {
		Date date = wdtf.parse(dateTimeString);
		return date;
	}
	
	public static String getWALFormatDateTime(String dateTimeString) throws ParseException {
		Date date = dbWALFormat.parse(dateTimeString);
		return wdtf.format(date);
	}

	public static String getStringFromDate(Date date) throws ParseException {
		String dateString = wdf.format(date);
		return dateString;
	}

	public static String getStringFromDateTime(Date date) throws ParseException {
		String dateString = wdtf.format(date);
		return dateString;
	}
	
	public static String getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);       
        String day = wdyf.format(cal.getTime());
        return day;
	}

	public static String getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return wyf.format(cal.getTime());
	}
	
	public static List<String> jobRunOnDaysList(String jobRunOnDays) {		
		List<String> days = Arrays.asList(jobRunOnDays.split("\\s*,\\s*"));
		return days;
	}
	
	public static boolean isJobAlive(Date endDateTime, String jobTimeZone) throws ParseException {	
		Date jobEndDateTime = convertDateToServerTimezone(endDateTime, jobTimeZone);
		Date currentDate = convertDateToServerTimezone(new Date(), jobTimeZone);
		return jobEndDateTime.after(currentDate);		
	}
	
	public static String convertTo24HoursFormat(String twelveHourTime) throws ParseException {
		return TWENTY_FOUR_TF.format(TWELVE_TF.parse(twelveHourTime));
	}
	
	public static String convertTo12HoursFormat(String time) throws ParseException {
		return TWELVE_TF.format(tf.parse(time));
	}

	public static boolean isDateOlderThanToday(Date issueDate) throws ParseException {
		boolean isDateOlderThanToday = true;
		Calendar today = Calendar.getInstance(); 

		Calendar issueDateCal = Calendar.getInstance();
		issueDateCal.setTime(issueDate); 

		if (today.get(Calendar.YEAR) == issueDateCal.get(Calendar.YEAR)
		  && today.get(Calendar.DAY_OF_YEAR) == issueDateCal.get(Calendar.DAY_OF_YEAR)) {
			isDateOlderThanToday = false;
		}
		
		return isDateOlderThanToday;
	}
	
	public static List<String> splitDateAndTimeFromStamp(Date dateTime) {
		Calendar calendar = GregorianCalendar.getInstance();
	    calendar.setTime(dateTime);
		List<String> dateTimeList = new ArrayList<String>();	
        dateTimeList.add( wdf.format(dateTime));
        dateTimeList.add( TWELVE_TF.format(dateTime));        
        return dateTimeList;
	}
	
	public static Date getBeginningOfDayTime(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    return calendar.getTime();
	}

	public static Date getEndOfDayTime(Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
	    calendar.setTime(date);
	    calendar.set(Calendar.HOUR_OF_DAY, 23);
	    calendar.set(Calendar.MINUTE, 59);
	    calendar.set(Calendar.SECOND, 59);
	    calendar.set(Calendar.MILLISECOND, 999);
	    return calendar.getTime();
	}
	
	public static Date convertCurrentDateToServerTimezone(Date date) throws ParseException {
		SimpleDateFormat tzf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		tzf.setTimeZone(TimeZone.getDefault());		
		return tzf.parse(tzf.format(date));
    }
	
	public static Date convertDateToServerTimezone(Date date, String timezone) throws ParseException {
		SimpleDateFormat serverTimeZone = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat userTimeZone = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		
		if(timezone != null && StringUtils.hasText(timezone)) {
			userTimeZone.setTimeZone(TimeZone.getTimeZone(timezone));
		} 
		
		serverTimeZone.setTimeZone(TimeZone.getDefault());
		return userTimeZone.parse(serverTimeZone.format(date));
    }
	
	public static Date convertDateToUserTimezone(Date date, String timezone) throws ParseException {
		SimpleDateFormat serverTimeZone = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat userTimeZone = new SimpleDateFormat("MM/dd/yyyy hh:mm a");		
		
		if(timezone != null && StringUtils.hasText(timezone)) {
			userTimeZone.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		
		serverTimeZone.setTimeZone(TimeZone.getDefault());	
		return serverTimeZone.parse(userTimeZone.format(date));
    }	
	
	public static int daysBetween(Date startDate, Date endDate) {
		return (int)( (startDate.getTime() - endDate.getTime()) / (1000 * 60 * 60 * 24));
	}
	
	public static String convertFromMilitaryToNormalTime(String militaryTime) {
		String[] militaryArray = militaryTime.split(":");
		String hour = militaryArray[0];
		String meridiam = "AM";
		String minutes = militaryArray[1];
		
		if(hour.equalsIgnoreCase("0") || hour.equalsIgnoreCase("00")) {
			hour = "12";
		} else if(Integer.parseInt(hour) > 11) {
			int hourValue = Integer.parseInt(hour) % 12;
			hour = (hourValue == 0) ? "12" : String.valueOf(hourValue);
			meridiam = "PM";
		}
		
		hour = (hour.length() == 1) ? "0" + hour : hour;
		return hour + ":" + minutes + " " + meridiam;
	}
	
	public static Date convertSymbolicDateToRealDate(String symbolicDate) {
		Calendar cal = Calendar.getInstance();

		if(symbolicDate.equals("LWD")) {
			if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				cal.add(Calendar.DAY_OF_MONTH, -2);
			}
		} else if(symbolicDate.equals("YESTERDAY")) {
			cal.add(Calendar.DATE, -1);
		} else {
			if(symbolicDate.equals("FDOTM")) {
				cal.set(Calendar.DATE, 1);
			} else if(symbolicDate.equals("LDOTM")) {
				cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); 
			} else {
				cal.add(Calendar.MONTH, -1);
				
				if(symbolicDate.equals("FDOLM")) {
					cal.set(Calendar.DATE, 1);
				} else if(symbolicDate.equals("LDOLM")) {
					cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); 
				}
			}
		}
		
		return cal.getTime();			
	}
	
	public static Date getDateBefore(int noOfDays){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -noOfDays);
		Date dateBefore = calendar.getTime();
		return dateBefore;
	}
}

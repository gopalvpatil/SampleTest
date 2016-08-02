package com.westernalliancebancorp.positivepay.dto;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

public class DecisionWindowDtoBuilder {

	public DecisionWindow dtoToModel(DecisionWindowDto dto)
			throws ParseException {
		DecisionWindow model = new DecisionWindow();

		String startTime = dto.getStartHour() + ":" + dto.getStartMin() + " "
				+ dto.getStartMeridiem();

		Time start = getTimeFromString(startTime);

		// DateUtils.
		model.setStartWindow(start);

		String endTime = dto.getEndHour() + ":" + dto.getEndMin() + " "
				+ dto.getEndMeridiem();

		Time end = getTimeFromString(endTime);

		
		model.setEndWindow(end);
		String date = dto.getDate();
		if(date!=null && date.length() > 0)
		{
			Date sdate = DateUtils.getDateFromString(date);
			model.setDecisionWindowDate(sdate);
		}

		model.setTimeZone(dto.getTimezone());
		AuditInfo auditInfo = new AuditInfo();
		String name = SecurityUtility.getPrincipal();
		auditInfo.setCreatedBy(name);
		auditInfo.setDateCreated(new Date());
		auditInfo.setDateModified(new Date());
		model.setAuditInfo(auditInfo);

		return model;
	}

	private Time getTimeFromString(String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
		long ms = sdf.parse(time).getTime();
		Time t = new Time(ms);
		return t;
	}

}

package com.westernalliancebancorp.positivepay.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.SystemMessageDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.SystemMessage.TYPE;
import com.westernalliancebancorp.positivepay.service.SystemMessageService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * User: gduggirala Date: 12/5/14 Time: 11:48 AM
 */
@Service
public class SystemMessageServiceImpl implements SystemMessageService {
	@Autowired
	SystemMessageDao systemMessageDao;
	
	 @Loggable
	 private Logger logger;
	 
	 @Autowired
	 BatchDao batchDao;
	 
	  

	/**
	 * Since flow is not very clear, written a kind of manual algorithm.
	 */
	 @Override
		public List<SystemMessage> getSystemMessages(SystemMessage.TYPE type)
				throws Exception {
		    Date currentDate = Calendar.getInstance().getTime();
		    logger.debug("CurrentDate:"+currentDate);
			List<SystemMessage> systemMessages = systemMessageDao
					.getSystemMessages(type);
			List<SystemMessage> validMessages = new ArrayList<SystemMessage>();
			for (SystemMessage message : systemMessages) {
				Date startDate = message.getStartDateTime();
				Date endDate = message.getEndDateTime();
				logger.debug("startDate:"+startDate);
				logger.debug("endDate:"+endDate);
				if (startDate.compareTo(currentDate) <= 0	&& ((endDate.compareTo(currentDate) >= 0)	&& (message.getEndDateTime().compareTo(currentDate)) >= 0)) 
				{
					message.setStartDateTime(DateUtils.convertDateToUserTimezone( message.getStartDateTime(), "US/Pacific"));
					message.setEndDateTime(DateUtils.convertDateToUserTimezone( message.getEndDateTime(), "US/Pacific"));
					validMessages.add(message);
				}
			}

			return validMessages;
		}

	@Override
	public List<SystemMessage> getSystemMessages(SystemMessage.TYPE type,
			Date startDate, Date endDate) {
		throw new RuntimeException("Not yet implemented.");
	}

	@Override
	public SystemMessage setSystemMessage(String message,
			SystemMessage.TYPE type, Date fromDate, Date toDate,String timezone) throws Exception {
		SystemMessage systemMessage = new SystemMessage();
		logger.debug("Browser Timezone:"+timezone);
		logger.debug("fromDate:"+fromDate);
		logger.debug("toDate:"+toDate);
		Date fDate = DateUtils.convertDateToServerTimezone(fromDate, timezone);
		Date eDate = DateUtils.convertDateToServerTimezone(toDate, timezone);
		logger.debug("-- after conversion --");
		logger.debug("FDate:"+fDate);
		logger.debug("EDate:"+eDate);
		systemMessage.setMessage(message);
		systemMessage.setEndDateTime(eDate);
		systemMessage.setStartDateTime(fDate);
		systemMessage.setType(type);
		systemMessageDao.save(systemMessage);
		return systemMessage;

	}
	
	@Override
	public void deleteAllSystemMessages(TYPE type) {
		systemMessageDao.deleteByType(type);
	}

	@Override
	public List<Long> systemMessageInUserDetailHistory(Long user_detail_id,
			Long messageId) {
		return batchDao.systemMessageInUserDetailHistory(user_detail_id, messageId);
	}
}

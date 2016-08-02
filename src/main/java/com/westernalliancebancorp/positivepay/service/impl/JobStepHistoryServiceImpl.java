package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.JobStepHistoryDao;
import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;
import com.westernalliancebancorp.positivepay.service.JobStepHistoryService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * @author Gopal Patil
 * 
 */
@Service
public class JobStepHistoryServiceImpl implements JobStepHistoryService {
	
	/** The logger object */
	@Loggable
	private Logger logger;
	
	@Autowired
	JobStepHistoryDao jobStepHistoryDao;
	
	@Autowired
	BatchDao batchDao;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobStepHistory save(JobStepHistory jobStepHistory) {		
		return jobStepHistoryDao.save(jobStepHistory);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobStepHistory update(JobStepHistory jobStepHistory) {		
		return jobStepHistoryDao.update(jobStepHistory);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void delete(JobStepHistory jobStepHistory) {
		jobStepHistoryDao.delete(jobStepHistory);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobStepHistory findById(Long jobStepId) {		
		return jobStepHistoryDao.findById(jobStepId);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<JobStepHistory> findAll() {		
		return jobStepHistoryDao.findAll();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<JobStepHistory> findJobStepHistoryByJobId(Long jobId) {
		return jobStepHistoryDao.findJobStepHistoryByJobId(jobId);
	}

	@Override
	public List<ItemErrorRecordsDto> fetchErrorsDto(String startDateTime,
			String endDateTime, String timezone) throws Exception {
		Date convertedStartDateTime = DateUtils.getDateTimeFromString(startDateTime);
		Date convertedEndDateTime = DateUtils.getDateTimeFromString(endDateTime);		
		//File saved in database as per server timezone, so we have to convert job step datetime to server timezone
		Date jobStartDate = DateUtils.convertDateToServerTimezone(convertedStartDateTime, timezone);
		Date jobEndDate = DateUtils.convertDateToServerTimezone(convertedEndDateTime, timezone);		
		return batchDao.findErrorsInFile(jobStartDate, jobEndDate);
	}

}

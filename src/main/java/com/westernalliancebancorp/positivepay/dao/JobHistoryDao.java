package com.westernalliancebancorp.positivepay.dao;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.SystemMessage.TYPE;

/**
 * @author Gopal Patil
 *
 */
public interface JobHistoryDao extends GenericDao<JobHistory, Long> {
	List<JobHistory> findJobsCreatedBy(String userName);
	@Override
	void delete(JobHistory entity);
	JobHistory findByJobIdAndActualStartTime(Long jobId,	Date actualStartTime);
	List<SystemMessage> find(Date fromDate, TYPE type);
	List<JobHistory> findAllJobs();
}

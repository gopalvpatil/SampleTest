package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobExecutionLocker;

/**
 * JobExecutionLockerDao is
 *
 * @author Giridhar Duggirala
 */

public interface JobExecutionLockerDao extends GenericDao<JobExecutionLocker, Long> {
	List<JobExecutionLocker> findByJobId(Long jobId);
	void deleteAll();
	void deleteJobExecutionLockersOnServer(String serverName);
}

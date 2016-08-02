package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;

/**
 * @author gpatil
 *
 */
public interface JobStepHistoryDao extends GenericDao<JobStepHistory, Long> {
	List<JobStepHistory> findJobStepHistoryByJobId(Long jobId);
}


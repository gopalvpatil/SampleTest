package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobStep;

public interface JobStepDao extends GenericDao<JobStep, Long> {
	List<JobStep> findAllJobStepsBy(Long jobId);
}

package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobType;

/**
 * @author Gopal Patil
 *
 */
public interface JobService {
	void save(Job job);
	void update(Job job);	
	List<Job> findAllActiveJobs();
	Job findJobById(String jobId);
	List<JobStep> findJobStepByJobId(Long jobId);	
	void deleteAllJobExecutionLockers();
	void runSelectedJobs(List<String> selectedIdsList);		
	void deleteSelectedJobById(Long id);
	JobType findJobTypeById(Long id);
	JobActionType findJobActionTypeById(Long id);
	List<JobType> findActiveJobTypes();
	List<JobActionType> findJobActionTypeByJobTypeId(Long jobTypeId);
	List<JobActionType> findJobActionTypes();
	List<JobCriteriaData> fetchCriteriaByStep(Long stepId);
	void deleteJobExecutionLockersOnServer(String serverName);
	String findLastCronExpressionByJobId(Long id);
	JobDto findLastJobConfigurationBy(Long jobId);
}

package com.westernalliancebancorp.positivepay.service;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStatusType;

/**
 * @author Gopal Patil
 *
 */
public interface JobHistoryService {	
	JobHistory save(JobHistory jobHistory);	
	JobHistory update(JobHistory jobHistory);
	void delete(JobHistory jobHistory);
	JobHistory findById(Long id);
	JobHistory findByJobIdAndActualStartTime(Long jobId, Date actualStartTime);
	List<JobHistory> findAll();
	List<JobHistory> findByUserName(String userName);
	JobStatusType findJobStatusTypeBy(String name);	
	List<JobStatusType> findAllJobStatusTypes();
	JobStatusType findJobStatusTypeByCode(String statusCode);
	List<JobHistory> findAllJobs();

    List<JobDto> findallJobsJdbcFetch();
}

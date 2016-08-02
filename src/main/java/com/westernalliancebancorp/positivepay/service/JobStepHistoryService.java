package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;

/**
 * @author Gopal Patil
 *
 */
public interface JobStepHistoryService {
	JobStepHistory save(JobStepHistory jobStepHistory);
	JobStepHistory update(JobStepHistory jobStepHistory);
	void delete(JobStepHistory jobStepHistory);
	JobStepHistory findById(Long jobStepId);
	List<JobStepHistory> findAll();
	List<JobStepHistory> findJobStepHistoryByJobId(Long jobId);
	List<ItemErrorRecordsDto> fetchErrorsDto(String startDateTime, String endDateTime, String timezone) throws Exception ;
}

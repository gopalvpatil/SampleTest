package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.JobStep;

import java.util.Date;
import java.util.List;

/**
 * User: gduggirala
 * Date: 19/6/14
 * Time: 1:02 PM
 */
public interface JobStepService {
    List<JobStep> findAllJobStepsByJobId(Long jobId);
	Long findJobStepNumOfItemsProcessedInFile(Date jobStepActualStartTime,	Date jobStepActualEndTime, String fileType);
	Long findJobStepNumOfErrorsInFile(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType);
	List<String> findJobStepFileNames(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType);
}

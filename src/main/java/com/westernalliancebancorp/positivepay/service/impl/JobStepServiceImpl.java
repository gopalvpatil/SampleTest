package com.westernalliancebancorp.positivepay.service.impl;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.JobStepDao;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.service.JobStepService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * User: gduggirala
 * Date: 19/6/14
 * Time: 1:03 PM
 */
@Service
public class JobStepServiceImpl implements JobStepService {
    @Autowired
    JobStepDao jobStepDao;
    
    @Autowired
    BatchDao batchDao;

    @Override
	@Transactional(propagation = Propagation.REQUIRED)
    @Cacheable(cacheName = "findAllJobStepsBy")
    public List<JobStep> findAllJobStepsByJobId(Long jobId) {
        return jobStepDao.findAllJobStepsBy(jobId);
    }

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Long findJobStepNumOfItemsProcessedInFile(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType) {
		return batchDao.findJobStepNumOfItemsProcessedInFile(jobStepActualStartTime, jobStepActualEndTime, fileType);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Long findJobStepNumOfErrorsInFile(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType) {
		return batchDao.findJobStepNumOfErrorsInFile(jobStepActualStartTime, jobStepActualEndTime, fileType);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<String> findJobStepFileNames(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType) {
		return batchDao.findJobStepFileNames(jobStepActualStartTime, jobStepActualEndTime, fileType);
	}
}

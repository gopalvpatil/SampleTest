package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Date;
import java.util.List;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.impl.jdbc.BatchJdbcDao;
import com.westernalliancebancorp.positivepay.dto.JobDto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.JobHistoryDao;
import com.westernalliancebancorp.positivepay.dao.JobStatusTypeDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStatusType;
import com.westernalliancebancorp.positivepay.service.JobHistoryService;

/**
 * @author Gopal Patil
 *
 */
@Service("jobHistoryService")
public class JobHistoryServiceImpl implements JobHistoryService {

	/** The logger object */
	@Loggable
	private Logger logger;

    @Autowired
    BatchDao batchDao;

	@Autowired
	private JobHistoryDao jobHistoryDao;
	
	@Autowired
	private JobStatusTypeDao jobStatusTypeDao;	

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobHistory save(JobHistory jobHistory) {		
		return jobHistoryDao.save(jobHistory);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobHistory update(JobHistory jobHistory) {
		return jobHistoryDao.update(jobHistory);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public void delete(JobHistory jobHistory) {
		jobHistoryDao.delete(jobHistory);		
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public JobHistory findById(Long id) {
		return jobHistoryDao.findById(id);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public JobHistory findByJobIdAndActualStartTime(Long jobId, Date actualStartTime) {
		return jobHistoryDao.findByJobIdAndActualStartTime(jobId, actualStartTime);		
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public List<JobHistory> findAll() {
		return jobHistoryDao.findAll();
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public List<JobHistory> findByUserName(String userName) {		
		return jobHistoryDao.findJobsCreatedBy(userName);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public List<JobHistory> findAllJobs() {		
		return jobHistoryDao.findAllJobs();
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<JobDto> findallJobsJdbcFetch(){
        return batchDao.getAllJobHistory();
    }

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	@Cacheable(cacheName ="findAllJobStatusTypes")
	public List<JobStatusType> findAllJobStatusTypes() {
		return jobStatusTypeDao.findAll();
	}	
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
    @com.googlecode.ehcache.annotations.Cacheable(cacheName = "JobStatusType")
	public JobStatusType findJobStatusTypeBy(String name) {
		return jobStatusTypeDao.findJobStatusTypeBy(name);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	@Cacheable(cacheName ="findJobStatusTypeByCode")
	public JobStatusType findJobStatusTypeByCode(String statusCode) {
		return jobStatusTypeDao.findJobStatusTypeByCode(statusCode);
	}
	
}

package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Job;


/**
 * @author Gopal Patil
 *
 */
public interface JobDao extends GenericDao<Job, Long> {	
	List<Job> findAllActiveJobs();		
	Job findByJobId(Long id);
}

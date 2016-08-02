package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobType;

/**
 * UserDetail:	Gopal Patil
 * Date:	Jan 13, 2014
 * Time:	1:00:51 PM
 */
public interface JobTypeDao extends GenericDao<JobType, Long> {
	List<JobType> findActiveJobTypes();
	List<JobActionType> findJobActionTypeByJobTypeId(Long jobTypeId);
	List<JobActionType> findJobActionTypes();
}

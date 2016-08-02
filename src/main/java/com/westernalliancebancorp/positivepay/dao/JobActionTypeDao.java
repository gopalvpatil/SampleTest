package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobActionType;

/**
 * @author Gopal Patil
 *
 */
public interface JobActionTypeDao extends GenericDao<JobActionType, Long> {
	List<JobActionType> findAllJobActionTypes();
}

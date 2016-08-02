package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.JobStatusType;

/**
 * UserDetail:	Gopal Patil
 * Date:	Jan 13, 2014
 * Time:	1:13:48 PM
 */
public interface JobStatusTypeDao extends GenericDao<JobStatusType, Long> {
	JobStatusType findJobStatusTypeBy(String name);
	JobStatusType findJobStatusTypeByCode(String statusCode);
}

package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;

/**
 * Dao to interact with ExceptionStatus Model
 * @author Anand Kumar
 */
public interface ExceptionStatusDao extends GenericDao<ExceptionStatus, Long> {
	ExceptionStatus findByName(String statusName);
}

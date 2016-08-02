/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Reason;

/**
 * Data access object interface to work with Reason Object database operations
 * @author Anand Kumar
 *
 */
public interface ReasonDao extends GenericDao<Reason, Long> {
	List<Reason> findAllActiveReasons(boolean isPay);	
}

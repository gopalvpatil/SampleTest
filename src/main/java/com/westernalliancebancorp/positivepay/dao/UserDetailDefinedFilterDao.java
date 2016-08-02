/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter;

/**
 * Data access object interface to work with UserDetailDefinedFilter Object database operations
 * @author Anand Kumar
 *
 */
public interface UserDetailDefinedFilterDao extends GenericDao<UserDetailDefinedFilter, Long> {
	List<UserDetailDefinedFilter> findAllForUser(UserDetail userDetail);
}

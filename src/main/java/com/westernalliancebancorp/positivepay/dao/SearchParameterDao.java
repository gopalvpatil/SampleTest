/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.Map;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.SearchParameter;

/**
 * Data access object interface to work with SearchParameter Object database operations
 * @author Anand Kumar
 *
 */
public interface SearchParameterDao extends GenericDao<SearchParameter, Long> {
	SearchParameter findByName(String name);
	Map<Long, String> getIdNameMap();
}

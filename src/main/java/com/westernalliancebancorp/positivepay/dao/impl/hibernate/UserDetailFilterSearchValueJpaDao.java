/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.UserDetailFilterSearchValueDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.UserDetailFilterSearchValue;

/**
 * Data access object JPA impl to work with UserDetailFilterSearchValue model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class UserDetailFilterSearchValueJpaDao extends GenericJpaDao<UserDetailFilterSearchValue, Long> implements UserDetailFilterSearchValueDao {
	
	public UserDetailFilterSearchValueJpaDao() {
        super();
	}
}

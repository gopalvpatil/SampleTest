/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.DelimiterDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Delimiter;

/**
 * Data access object JPA impl to work with FileMapping model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class DelimiterJpaDao extends GenericJpaDao<Delimiter, Long> implements DelimiterDao {
	
	public DelimiterJpaDao() {
        super();
	}
}

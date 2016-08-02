/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.DecisionWindowDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;

/**
 * Data access object JPA impl to work with DecisionWindow model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class DecisionWindowJpaDao extends GenericJpaDao<DecisionWindow, Long> implements DecisionWindowDao {
	
	public DecisionWindowJpaDao() {
        super();
	}
}

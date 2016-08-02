/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.UserDetailDefinedFilterDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter_;
import com.westernalliancebancorp.positivepay.model.UserDetail_;

/**
 * Data access object JPA impl to work with UserDetailDefinedFilter model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class UserDetailDefinedFilterJpaDao extends GenericJpaDao<UserDetailDefinedFilter, Long> implements UserDetailDefinedFilterDao {
	
	public UserDetailDefinedFilterJpaDao() {
        super();
	}
	
	@Override
	public List<UserDetailDefinedFilter> findAllForUser(UserDetail userDetail) {
		CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<UserDetailDefinedFilter> cq = criteriaBuilder.createQuery(UserDetailDefinedFilter.class);
        Root<UserDetailDefinedFilter> userDetailDefinedFilterRoot = cq.from(UserDetailDefinedFilter.class);
        Predicate useridCondition = criteriaBuilder.equal(userDetailDefinedFilterRoot.get(UserDetailDefinedFilter_.userDetail).get(UserDetail_.id), userDetail.getId());
        Predicate hybridPredicate = criteriaBuilder.and(useridCondition);
        cq.where(hybridPredicate);
        TypedQuery<UserDetailDefinedFilter> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
	}
}

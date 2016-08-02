/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ReasonDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Reason;
import com.westernalliancebancorp.positivepay.model.Reason_;

/**
 * Data access object JPA impl to work with Reason model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class ReasonJpaDao extends GenericJpaDao<Reason, Long> implements ReasonDao {
	
	@Override
	public List<Reason> findAllActiveReasons(boolean isPay) {        
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reason> reasonCriteriaQuery = criteriaBuilder.createQuery(Reason.class);
        Root<Reason> reasonRoot = reasonCriteriaQuery.from(Reason.class);
        Predicate conditionIsActive = criteriaBuilder.equal(reasonRoot.get(Reason_.isActive), Boolean.TRUE);
        Predicate conditionIsPay = criteriaBuilder.equal(reasonRoot.get(Reason_.isPay), isPay);
        Predicate andConditions = criteriaBuilder.and(conditionIsActive, conditionIsPay);
        reasonCriteriaQuery.where(andConditions);
        TypedQuery<Reason> reasonTypedQuery = entityManager.createQuery(reasonCriteriaQuery);
        return reasonTypedQuery.getResultList();
	}
}

package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.UserHistoryDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.AuditInfo_;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserDetail_;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.model.UserHistory_;

/**
 * UserHistoryJpaDao is
 *
 * @author Giridhar Duggirala
 * 
 */
@Repository
public class UserHistoryJpaDao extends GenericJpaDao<UserHistory, Long> implements UserHistoryDao {

	@Override
	public List<UserHistory> getUserDetailHistoryBy(Long userId, Integer startIndex, Integer maxResult) {	
		EntityManager entityManager = this.getEntityManager();		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
		CriteriaQuery<UserHistory> cq = cb.createQuery(UserHistory.class);	
		Root<UserHistory> cqr = cq.from(UserHistory.class);	
		Fetch<UserHistory, UserDetail> userDetail = cqr.fetch(UserHistory_.userDetail, JoinType.LEFT);
		Fetch<UserHistory, UserActivity> userActivity = cqr.fetch(UserHistory_.userActivity, JoinType.LEFT);
		
		cq.where(cb.equal(cqr.get(UserHistory_.userDetail).get(UserDetail_.id), userId));
		cq.orderBy(cb.desc(cqr.get(UserHistory_.auditInfo).get(AuditInfo_.dateCreated)));

		TypedQuery<UserHistory> query = entityManager.createQuery(cq);
		query.setFirstResult(startIndex == null ? 0 : startIndex);
		query.setMaxResults(maxResult == null ? Integer.MAX_VALUE : maxResult);
				
		return query.getResultList();
	}
}

package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.SystemMessageDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.SystemMessage.TYPE;
import com.westernalliancebancorp.positivepay.model.SystemMessage_;

/**
 * User: gduggirala Date: 12/5/14 Time: 11:56 AM
 */
@Repository(value = "systemMessageDao")
public class SystemMessageJpaDao extends GenericJpaDao<SystemMessage, Long>
		implements SystemMessageDao {

	
	/*
	 * @Override public List<SystemMessage> getSystemMessages(Date fromDate,
	 * SystemMessage.TYPE type) { EntityManager entityManager =
	 * this.getEntityManager(); CriteriaBuilder criteriaBuilder =
	 * entityManager.getCriteriaBuilder(); CriteriaQuery<SystemMessage>
	 * systemMessageCriteriaQuery =
	 * criteriaBuilder.createQuery(SystemMessage.class); Root<SystemMessage>
	 * systemMessageRoot = systemMessageCriteriaQuery.from(SystemMessage.class);
	 * 
	 * Predicate typePredicate =
	 * criteriaBuilder.equal(systemMessageRoot.get(SystemMessage_.type), type);
	 * 
	 * ParameterExpression<Date> fromDateParamExpr =
	 * criteriaBuilder.parameter(Date.class); Predicate fromDatecondition =
	 * criteriaBuilder
	 * .between(fromDateParamExpr,systemMessageRoot.get(SystemMessage_
	 * .startDateTime), systemMessageRoot.get(SystemMessage_.endDateTime));
	 * 
	 * logger.debug("SystemMessage_.startDateTime:"+SystemMessage_.startDateTime.
	 * getJavaType());
	 * logger.debug("SystemMessage_.endDateTime:"+SystemMessage_.
	 * endDateTime.toString());
	 * 
	 * Predicate hybridPredicate = criteriaBuilder.and(typePredicate,
	 * fromDatecondition);
	 * 
	 * systemMessageCriteriaQuery.where(hybridPredicate);
	 * 
	 * TypedQuery<SystemMessage> systemMessageTypedQuery =
	 * entityManager.createQuery
	 * (systemMessageCriteriaQuery).setParameter(fromDateParamExpr, fromDate,
	 * TemporalType.DATE); return systemMessageTypedQuery.getResultList();
	 * 
	 * }
	 */

	@Override
	public List<SystemMessage> getSystemMessages(SystemMessage.TYPE type) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<SystemMessage> systemMessageCriteriaQuery = criteriaBuilder
				.createQuery(SystemMessage.class);
		Root<SystemMessage> systemMessageRoot = systemMessageCriteriaQuery
				.from(SystemMessage.class);

		systemMessageCriteriaQuery.where(criteriaBuilder.equal(
				systemMessageRoot.get(SystemMessage_.type), type));

		TypedQuery<SystemMessage> allQuery = getEntityManager().createQuery(
				systemMessageCriteriaQuery);
		return allQuery.getResultList();

	}

	@Override
	@Transactional
	public void deleteByType(TYPE type) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

		// create delete
		CriteriaDelete<SystemMessage> delete = criteriaBuilder
				.createCriteriaDelete(SystemMessage.class);

		// set the root class
		Root<SystemMessage> e = delete.from(SystemMessage.class);

		// set where clause
		delete.where(criteriaBuilder.equal(e.get(SystemMessage_.type), type));

		// perform update
		entityManager.createQuery(delete).executeUpdate();
	}
}

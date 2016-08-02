package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobActionTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.JobActionType;

/**
 * @author Gopal Patil
 *
 */
@Repository
public class JobActionTypeJpaDao extends GenericJpaDao<JobActionType, Long> implements JobActionTypeDao {

	@Override
	public List<JobActionType> findAllJobActionTypes() {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobActionType> jobActionTypeCriteriaQuery = criteriaBuilder.createQuery(JobActionType.class);		
		Root<JobActionType> jobActionTypeRoot = jobActionTypeCriteriaQuery.from(JobActionType.class);		
		TypedQuery<JobActionType> jobActionTypeQuery = entityManager.createQuery(jobActionTypeCriteriaQuery);		
		return jobActionTypeQuery.getResultList();
	}
}

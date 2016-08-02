package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobActionType_;
import com.westernalliancebancorp.positivepay.model.JobType;
import com.westernalliancebancorp.positivepay.model.JobType_;

/**
 * UserDetail:	Gopal Patil
 * Date:	Jan 13, 2014
 * Time:	1:03:10 PM
 */
@Repository
public class JobTypeJpaDao extends GenericJpaDao<JobType, Long> implements JobTypeDao{
	
	@Override
	public List<JobType> findActiveJobTypes() {		
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobType> jobTypeCriteriaQuery = criteriaBuilder.createQuery(JobType.class);		
		Root<JobType> jobTypeRoot = jobTypeCriteriaQuery.from(JobType.class);		
		Predicate condition = criteriaBuilder.isTrue(jobTypeRoot.get(JobType_.isActive));
		jobTypeCriteriaQuery.where(condition);
		TypedQuery<JobType> jobTypeQuery = entityManager.createQuery(jobTypeCriteriaQuery);		
		return jobTypeQuery.getResultList();
	}

	@Override
	public List<JobActionType> findJobActionTypeByJobTypeId(Long jobTypeId) {		
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobActionType> jobActionTypeCriteriaQuery = criteriaBuilder.createQuery(JobActionType.class);		
		Root<JobActionType> jobActionTypeRoot = jobActionTypeCriteriaQuery.from(JobActionType.class);
		Fetch<JobActionType, JobType> jobType = jobActionTypeRoot.fetch(JobActionType_.jobType, JoinType.LEFT);		
		jobActionTypeCriteriaQuery.where(criteriaBuilder.equal(jobActionTypeRoot.get(JobActionType_.jobType).get(JobType_.id), jobTypeId));
		TypedQuery<JobActionType> jobActionTypeQuery = entityManager.createQuery(jobActionTypeCriteriaQuery);		
		return jobActionTypeQuery.getResultList();
	}
	
	
	@Override
	public List<JobActionType> findJobActionTypes() {		
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobActionType> jobActionTypeCriteriaQuery = criteriaBuilder.createQuery(JobActionType.class);		
		Root<JobActionType> jobActionTypeRoot = jobActionTypeCriteriaQuery.from(JobActionType.class);
		Fetch<JobActionType, JobType> jobType = jobActionTypeRoot.fetch(JobActionType_.jobType, JoinType.LEFT);		

		TypedQuery<JobActionType> jobActionTypeQuery = entityManager.createQuery(jobActionTypeCriteriaQuery);		
		return jobActionTypeQuery.getResultList();
	}

}

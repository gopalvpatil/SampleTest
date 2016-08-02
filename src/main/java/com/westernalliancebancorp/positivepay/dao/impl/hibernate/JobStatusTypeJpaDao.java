package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobStatusTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.JobStatusType;
import com.westernalliancebancorp.positivepay.model.JobStatusType_;

/**
 * UserDetail:	Gopal Patil
 * Date:	Jan 13, 2014
 * Time:	1:25:10 PM
 */
@Repository
public class JobStatusTypeJpaDao extends GenericJpaDao<JobStatusType, Long> implements JobStatusTypeDao {

	@Override
	public JobStatusType findJobStatusTypeBy(String name) {
		JobStatusType jobStatusType = null;
		try{			
			EntityManager entityManager = this.getEntityManager();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<JobStatusType> jobTypeCriteriaQuery = criteriaBuilder.createQuery(JobStatusType.class);		
			Root<JobStatusType> jobTypeRoot = jobTypeCriteriaQuery.from(JobStatusType.class);	
			Predicate condition = criteriaBuilder.equal(jobTypeRoot.get(JobStatusType_.name), name);
			jobTypeCriteriaQuery.where(condition);
			TypedQuery<JobStatusType> jobTypedQuery = entityManager.createQuery(jobTypeCriteriaQuery);
			jobStatusType =  jobTypedQuery.getSingleResult();		
		} catch (NoResultException nre) {
	        nre.printStackTrace();
	    }
		
		return jobStatusType;
		
	}

	@Override
	public JobStatusType findJobStatusTypeByCode(String statusCode) {
		JobStatusType jobStatusType = null;
		try{			
			EntityManager entityManager = this.getEntityManager();
			CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
			CriteriaQuery<JobStatusType> jobTypeCriteriaQuery = criteriaBuilder.createQuery(JobStatusType.class);		
			Root<JobStatusType> jobTypeRoot = jobTypeCriteriaQuery.from(JobStatusType.class);	
			Predicate condition = criteriaBuilder.equal(jobTypeRoot.get(JobStatusType_.statusCode), statusCode);
			jobTypeCriteriaQuery.where(condition);
			TypedQuery<JobStatusType> jobTypedQuery = entityManager.createQuery(jobTypeCriteriaQuery);
			jobStatusType =  jobTypedQuery.getSingleResult();		
		} catch (NoResultException nre) {
	        nre.printStackTrace();
	    }
		
		return jobStatusType;
		
	}

}

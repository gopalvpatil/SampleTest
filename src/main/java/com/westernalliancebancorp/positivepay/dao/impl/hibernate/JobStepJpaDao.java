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

import com.westernalliancebancorp.positivepay.dao.JobStepDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobStep_;
import com.westernalliancebancorp.positivepay.model.Job_;

/**
 * User:	Gopal Patil
 * Date:	Apr 11, 2014
 * Time:	12:36:35 PM
 */
@Repository("jobStepDao")
public class JobStepJpaDao extends GenericJpaDao<JobStep, Long> implements JobStepDao {
	
	
	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.dao.JobDao#fetchJobEagarlyBy(java.lang.Long)
	 */
	@Override
	public List<JobStep> findAllJobStepsBy(Long jobId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<JobStep> jobStepCriteriaQuery = criteriaBuilder.createQuery(JobStep.class);		
		Root<JobStep> jobStepRoot = jobStepCriteriaQuery.from(JobStep.class);
		Fetch<JobStep, Job> job = jobStepRoot.fetch(JobStep_.job, JoinType.LEFT);
		Fetch<JobStep, JobActionType> jobActionType = jobStepRoot.fetch(JobStep_.jobActionType, JoinType.LEFT);
		Predicate condition = criteriaBuilder.equal(jobStepRoot.get(JobStep_.job).get(Job_.id), jobId);
		jobStepCriteriaQuery.where(condition);
		TypedQuery<JobStep> jobStepQuery = entityManager.createQuery(jobStepCriteriaQuery);		
		return jobStepQuery.getResultList();
	}

}

package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobStepHistoryDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;
import com.westernalliancebancorp.positivepay.model.JobStepHistory_;
import com.westernalliancebancorp.positivepay.model.JobStep_;
import com.westernalliancebancorp.positivepay.model.Job_;

/**
 * @author Gopal Patil
 *
 */
@Repository
public class JobStepHistoryJpaDao extends GenericJpaDao<JobStepHistory, Long> implements JobStepHistoryDao{	
	
	@Override
	public List<JobStepHistory> findJobStepHistoryByJobId(Long jobId) {
		EntityManager entityManager = this.getEntityManager();		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
		CriteriaQuery<JobStepHistory> cq = cb.createQuery(JobStepHistory.class);	
		Root<JobStepHistory> cqr = cq.from(JobStepHistory.class);	
		cqr.fetch(JobStepHistory_.jobStatusType, JoinType.LEFT);
		cqr.fetch(JobStepHistory_.job, JoinType.LEFT);
		Fetch<JobStepHistory, JobStep> jobStep = cqr.fetch(JobStepHistory_.jobStep, JoinType.LEFT);		
		jobStep.fetch(JobStep_.jobActionType, JoinType.LEFT);
		
		Subquery<Long> sq = cq.subquery(Long.class);	
		Root<JobStepHistory> sqr = sq.from(JobStepHistory.class);			
		sq.select(cb.max(sqr.get(JobStepHistory_.id)));	
		sq.where(cb.equal(cqr.get(JobStepHistory_.job).get(Job_.id), jobId));
		sq.groupBy(sqr.get(JobStepHistory_.jobStep).get(JobStep_.id));		
		Expression<Long> exp = cqr.get(JobStepHistory_.id);
		Predicate predicate = exp.in(sq);		
		
		cq.where(predicate,	cb.equal(cqr.get(JobStepHistory_.job).get(Job_.id), jobId));
		TypedQuery<JobStepHistory> query = entityManager.createQuery(cq);
		return query.getResultList();
	}

}

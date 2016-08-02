package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobHistoryDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.AuditInfo_;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobHistory_;
import com.westernalliancebancorp.positivepay.model.Job_;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.SystemMessage_;
/**
 * @author Gopal Patil
 *
 */
@Repository
public class JobHistoryJpaDao extends GenericJpaDao<JobHistory, Long> implements JobHistoryDao {

	@Override
	public List<JobHistory> findJobsCreatedBy(String userName) {
		EntityManager entityManager = this.getEntityManager();		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
		CriteriaQuery<JobHistory> cq = cb.createQuery(JobHistory.class);	
		Root<JobHistory> cqr = cq.from(JobHistory.class);	
		cqr.fetch(JobHistory_.jobStatusType, JoinType.LEFT);
		cqr.fetch(JobHistory_.job, JoinType.LEFT);
		
		Subquery<Long> sq = cq.subquery(Long.class);	
		Root<JobHistory> sqr = sq.from(JobHistory.class);			
		sq.select(cb.max(sqr.get(JobHistory_.id)));		
		sq.groupBy(sqr.get(JobHistory_.job).get(Job_.id));		
		Expression<Long> exp = cqr.get(JobHistory_.id);
		Predicate predicate = exp.in(sq);		
		
		cq.where(predicate,
				cb.equal(cqr.get(JobHistory_.auditInfo).get(AuditInfo_.createdBy), userName));

		TypedQuery<JobHistory> query = entityManager.createQuery(cq);

		return query.getResultList();
	}
	
	@Override
	public List<JobHistory> findAllJobs() {
		EntityManager entityManager = this.getEntityManager();		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
		CriteriaQuery<JobHistory> cq = cb.createQuery(JobHistory.class);	
		Root<JobHistory> cqr = cq.from(JobHistory.class);	
		cqr.fetch(JobHistory_.jobStatusType, JoinType.LEFT);
		cqr.fetch(JobHistory_.job, JoinType.LEFT);
		
		Subquery<Long> sq = cq.subquery(Long.class);	
		Root<JobHistory> sqr = sq.from(JobHistory.class);			
		sq.select(cb.max(sqr.get(JobHistory_.id)));		
		sq.groupBy(sqr.get(JobHistory_.job).get(Job_.id));		
		Expression<Long> exp = cqr.get(JobHistory_.id);
		Predicate predicate = exp.in(sq);	
		
		cq.where(predicate);
		cq.orderBy(cb.asc(cqr.get(JobHistory_.job).get(Job_.name)));
		TypedQuery<JobHistory> query = entityManager.createQuery(cq);

		return query.getResultList();
	}
	
	@Override
	public JobHistory findByJobIdAndActualStartTime(Long jobId, Date actualStartTime) {
		EntityManager entityManager = this.getEntityManager();		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();		
		CriteriaQuery<JobHistory> cq = cb.createQuery(JobHistory.class);	
		Root<JobHistory> cqr = cq.from(JobHistory.class);	
		cqr.fetch(JobHistory_.jobStatusType, JoinType.LEFT);
		cqr.fetch(JobHistory_.job, JoinType.LEFT);	
		
		cq.where(	cb.equal(cqr.get(JobHistory_.job).get(Job_.id), jobId),	
					cb.equal(cqr.<Date>get(JobHistory_.actualStartTime), actualStartTime)
				);
		TypedQuery<JobHistory> query = entityManager.createQuery(cq);
		return query.getSingleResult();
	}
	
	@Override
	public List<SystemMessage> find(Date fromDate, SystemMessage.TYPE type) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SystemMessage> systemMessageCriteriaQuery = criteriaBuilder.createQuery(SystemMessage.class);
        Root<SystemMessage> systemMessageRoot = systemMessageCriteriaQuery.from(SystemMessage.class);
        
        Predicate typePredicate = criteriaBuilder.equal(systemMessageRoot.get(SystemMessage_.type), type);
        
        ParameterExpression<Date> fromDateParamExpr = criteriaBuilder.parameter(Date.class);
        Predicate fromDatecondition = criteriaBuilder.equal(fromDateParamExpr,systemMessageRoot.get(SystemMessage_.startDateTime));
        
        Predicate hybridPredicate = criteriaBuilder.and(typePredicate, fromDatecondition);
        
        systemMessageCriteriaQuery.where(hybridPredicate);
               
        TypedQuery<SystemMessage> systemMessageTypedQuery = entityManager.createQuery(systemMessageCriteriaQuery).setParameter(fromDateParamExpr, fromDate, TemporalType.DATE);
        return systemMessageTypedQuery.getResultList();
	}
	
	@Override
	public void delete(JobHistory entity) {
		try {
			throw new Exception("Job History can not be deleted");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

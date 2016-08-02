package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.Job_;
/**
 * @author Gopal Patil
 *
 */
@Repository
public class JobJpaDao extends GenericJpaDao<Job, Long> implements JobDao{

	@Override
	public List<Job> findAllActiveJobs() {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> jobCriteriaQuery = criteriaBuilder.createQuery(Job.class);		
		Root<Job> jobRoot = jobCriteriaQuery.from(Job.class);	
		Predicate condition = criteriaBuilder.isTrue(jobRoot.get(Job_.isActive));
		jobCriteriaQuery.where(condition);
		TypedQuery<Job> jobQuery = entityManager.createQuery(jobCriteriaQuery);		
		return jobQuery.getResultList();
	}
	
	@Override
	public Job findByJobId(Long id) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Job> jobCriteriaQuery = criteriaBuilder.createQuery(Job.class);		
		Root<Job> jobRoot = jobCriteriaQuery.from(Job.class);	
		Predicate condition = criteriaBuilder.equal(jobRoot.get(Job_.id), id);
		jobCriteriaQuery.where(condition);
		TypedQuery<Job> jobQuery = entityManager.createQuery(jobCriteriaQuery);		
		return jobQuery.getSingleResult();
	}

}

package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobExecutionLockerDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobExecutionLocker;
import com.westernalliancebancorp.positivepay.model.JobExecutionLocker_;
import com.westernalliancebancorp.positivepay.model.Job_;

/**
 * JobExecutionLockeJpaDao is
 *
 * @author Giridhar Duggirala
 */

@Repository("jobExecutionLockerDao")
public class JobExecutionLockeJpaDao extends GenericJpaDao<JobExecutionLocker, Long> implements JobExecutionLockerDao {
    @Override
    public List<JobExecutionLocker> findByJobId(Long jobId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobExecutionLocker> jobExecutionLockerCriteriaQuery = criteriaBuilder.createQuery(JobExecutionLocker.class);
        Root<JobExecutionLocker> jobExecutionLockerRoot = jobExecutionLockerCriteriaQuery.from(JobExecutionLocker.class);
		Fetch<JobExecutionLocker, Job> job = jobExecutionLockerRoot.fetch("job", JoinType.LEFT);
        Predicate jobNameCondition = criteriaBuilder.equal(jobExecutionLockerRoot.get(JobExecutionLocker_.job).get(Job_.id), jobId);
        jobExecutionLockerCriteriaQuery.where(jobNameCondition);
        TypedQuery<JobExecutionLocker> jobExecutionLockerTypedQuery = entityManager.createQuery(jobExecutionLockerCriteriaQuery);
        return jobExecutionLockerTypedQuery.getResultList();
    }
    
    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.dao.JobExecutionLockerDao#deleteAll()
     */
    @Override
    public void deleteAll() {
        EntityManager entityManager = this.getEntityManager();
        Query query = entityManager.createQuery("DELETE FROM JobExecutionLocker WHERE 0=0");
        query.executeUpdate();        
    }
    
    @Override
    public void deleteJobExecutionLockersOnServer(String serverName) {
        EntityManager entityManager = this.getEntityManager();
        Query query = entityManager.createQuery("DELETE FROM JobExecutionLocker WHERE executing_on_machine = '"+serverName+"'");
        query.executeUpdate();        
    }
    
}

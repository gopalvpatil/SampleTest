package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.JobCriteriaDataDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData_;
import com.westernalliancebancorp.positivepay.model.JobStep_;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/11/14
 * Time: 10:22 PM
 */
@Repository
public class JobCriteriaDataJpaDao extends GenericJpaDao<JobCriteriaData, Long> implements JobCriteriaDataDao {
    @Override
    public List<JobCriteriaData> findByJobId(Long jobStepId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JobCriteriaData> jobCriteriaDataCriteriaQuery = criteriaBuilder.createQuery(JobCriteriaData.class);
        Root<JobCriteriaData> jobCriteriaDataRoot = jobCriteriaDataCriteriaQuery.from(JobCriteriaData.class);        
        //Fetch<JobCriteriaData, JobStep> jobStep = jobCriteriaDataRoot.fetch("jobStep", JoinType.LEFT);
        jobCriteriaDataCriteriaQuery.where(criteriaBuilder.equal(jobCriteriaDataRoot.get(JobCriteriaData_.jobStep).get(JobStep_.id),jobStepId));
        TypedQuery<JobCriteriaData> allQuery = getEntityManager().createQuery(jobCriteriaDataCriteriaQuery);
        return allQuery.getResultList();
    }
}

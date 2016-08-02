package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.AuditInfo_;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck_;
import com.westernalliancebancorp.positivepay.model.FileMetaData_;

/**
 * 
 * @author akumar1
 *
 */
@Repository
public class ExceptionalCheckJpaDao extends GenericJpaDao<ExceptionalCheck, Long> implements ExceptionalCheckDao{

	@Override
	public List<ExceptionalCheck> findAllByUserName(String userName) {
		 	EntityManager entityManager = this.getEntityManager();
	        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
	        CriteriaQuery<ExceptionalCheck> expCheckCriteriaQuery = criteriaBuilder.createQuery(ExceptionalCheck.class);
	        Root<ExceptionalCheck> expCheckRoot = expCheckCriteriaQuery.from(ExceptionalCheck.class);
	        
	        Join<ExceptionalCheck, AuditInfo> auditJoin = expCheckRoot.join(ExceptionalCheck_.auditInfo);
	        Predicate userIdcondition = criteriaBuilder.equal(auditJoin.get(AuditInfo_.createdBy), userName);
	        expCheckCriteriaQuery.where(userIdcondition);
	        
	        TypedQuery<ExceptionalCheck> expCheckTypedQuery = entityManager.createQuery(expCheckCriteriaQuery);
	        return expCheckTypedQuery.getResultList();
	}

	@Override
	public List<ExceptionalCheck> findAllExceptionalChecks() {
	 	EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExceptionalCheck> expCheckCriteriaQuery = criteriaBuilder.createQuery(ExceptionalCheck.class);
        Root<ExceptionalCheck> expCheckRoot = expCheckCriteriaQuery.from(ExceptionalCheck.class);
        
        TypedQuery<ExceptionalCheck> expCheckTypedQuery = entityManager.createQuery(expCheckCriteriaQuery);
        return expCheckTypedQuery.getResultList();
	}

	@Override
	public List<ExceptionalCheck> findErrorRecordsUploadedBy(Long fileMetaDataId) {
		CriteriaBuilder cb =  getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ExceptionalCheck> cq = cb.createQuery(ExceptionalCheck.class);
        Root<ExceptionalCheck> checkRoot = cq.from(ExceptionalCheck.class);  
		checkRoot.fetch(ExceptionalCheck_.exceptionType, JoinType.LEFT);        
        cq.where(cb.equal(checkRoot.get(ExceptionalCheck_.fileMetaData).get(FileMetaData_.id), fileMetaDataId));  
        cq.orderBy(cb.asc(checkRoot.get(ExceptionalCheck_.lineNumber)));
        TypedQuery<ExceptionalCheck> allQuery = getEntityManager().createQuery(cq);
        return allQuery.getResultList();
	}
	
}

/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.googlecode.ehcache.annotations.Cacheable;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.FileMappingDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Company_;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.model.FileMapping_;

/**
 * Data access object JPA impl to work with FileMapping model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class FileMappingJpaDao extends GenericJpaDao<FileMapping, Long> implements FileMappingDao {
	
	public FileMappingJpaDao() {
        super();
	}

    /**
     * We no longer associate FileMapping to the user we associate it with the company.
    **/
	//More specific method implementations to follow for SampleModel entity
    @Cacheable(cacheName = "fileMappingByFileMappingIdAndCompanyId")
	public FileMapping findByCompanyIdAndFileMappingId(Long companyId, Long fileMappingId) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FileMapping> fileMappingCriteriaQuery = criteriaBuilder.createQuery(FileMapping.class);
        Root<FileMapping> fileMappingRoot = fileMappingCriteriaQuery.from(FileMapping.class);
        Predicate conditionUserId = criteriaBuilder.equal(fileMappingRoot.get(FileMapping_.company).get(Company_.id), companyId);
        Predicate conditionFileMappingId = criteriaBuilder.equal(fileMappingRoot.get(FileMapping_.id), fileMappingId);
        fileMappingCriteriaQuery.where(conditionUserId, conditionFileMappingId);
        TypedQuery<FileMapping> fileMappingTypedQuery = entityManager.createQuery(fileMappingCriteriaQuery);
        return fileMappingTypedQuery.getSingleResult();
	}
	
	public List<FileMapping> findAllByCompanyId(Long companyId) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<FileMapping> fileMappingCriteriaQuery = criteriaBuilder.createQuery(FileMapping.class);
        Root<FileMapping> fileMappingRoot = fileMappingCriteriaQuery.from(FileMapping.class);
        Predicate conditionCompanyId = criteriaBuilder.equal(fileMappingRoot.get(FileMapping_.company).get(Company_.id), companyId);
        fileMappingCriteriaQuery.where(conditionCompanyId);
        TypedQuery<FileMapping> fileMappingTypedQuery = entityManager.createQuery(fileMappingCriteriaQuery);
        return fileMappingTypedQuery.getResultList();
	}
}

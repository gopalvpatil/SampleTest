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

import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus_;

/**
 * Data access object JPA impl to work with Exception Status model database operations.
 *
 * @author Anand Kumar
 */
@Repository
public class ExceptionStatusJpaDao extends GenericJpaDao<ExceptionStatus, Long> implements ExceptionStatusDao {
    @Override
    @Cacheable(cacheName = "ExceptionStatusJpaDao.findByName")
    public ExceptionStatus findByName(String statusName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExceptionStatus> ExceptionStatusCriteriaQuery = criteriaBuilder.createQuery(ExceptionStatus.class);
        Root<ExceptionStatus> exceptionStatusRoot = ExceptionStatusCriteriaQuery.from(ExceptionStatus.class);
        Predicate exceptionNameCondition = criteriaBuilder.equal(exceptionStatusRoot.get(ExceptionStatus_.name), statusName);
        ExceptionStatusCriteriaQuery.where(exceptionNameCondition);
        TypedQuery<ExceptionStatus> ExceptionStatusTypedQuery = entityManager.createQuery(ExceptionStatusCriteriaQuery);
        List<ExceptionStatus> resultList = ExceptionStatusTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("No more than one status with same name %s should exist", statusName));
        }
        return null;
    }
}

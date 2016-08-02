package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * CheckStatusJpaDao is
 *
 * @author Giridhar Duggirala
 */

@Repository
public class CheckStatusJpaDao extends GenericJpaDao<CheckStatus, Long> implements CheckStatusDao {
	//Temporarily removing cache because of issues encountered in noPay scenario in exception Resolution -WALPP-124
    @Override
    //@Cacheable(cacheName = "checkStatusByNameAndVersion")
    public CheckStatus findByNameAndVersion(String statusName, Integer version) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckStatus> checkStatusCriteriaQuery = criteriaBuilder.createQuery(CheckStatus.class);
        Root<CheckStatus> checkStatusRoot = checkStatusCriteriaQuery.from(CheckStatus.class);
        Predicate statusNameCondition = criteriaBuilder.equal(checkStatusRoot.get(CheckStatus_.name), statusName);
        Predicate versionCondition = criteriaBuilder.equal(checkStatusRoot.get(CheckStatus_.version), version);
        Predicate hybridPredicate = criteriaBuilder.and(statusNameCondition, versionCondition);
        checkStatusCriteriaQuery.where(hybridPredicate);
        TypedQuery<CheckStatus> checkStatusTypedQuery = entityManager.createQuery(checkStatusCriteriaQuery);
        //To avoid NoResult exception etc.
        List<CheckStatus> resultList = checkStatusTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one status with same version %s and name %s should exist", version, statusName));
        }
        return null;
    }

    @Override
    public List<CheckStatus> findByName(String statusName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckStatus> checkStatusCriteriaQuery = criteriaBuilder.createQuery(CheckStatus.class);
        Root<CheckStatus> checkStatusRoot = checkStatusCriteriaQuery.from(CheckStatus.class);
        Predicate statusNameCondition = criteriaBuilder.equal(checkStatusRoot.get(CheckStatus_.name), statusName);
        checkStatusCriteriaQuery.where(statusNameCondition);
        TypedQuery<CheckStatus> checkStatusTypedQuery = entityManager.createQuery(checkStatusCriteriaQuery);
        //To avoid NoResult exception etc.
        return checkStatusTypedQuery.getResultList();
    }
}

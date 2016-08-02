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

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.LinkageType_;

/**
 * Data access object JPA impl to work with CheckLinkage model database operations.
 *
 * @author Anand Kumar
 */
@Repository
public class LinkageTypeJpaDao extends GenericJpaDao<LinkageType, Long> implements LinkageTypeDao {
    @Override
    public LinkageType findByName(LinkageType.NAME name) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LinkageType> linkageTypeCriteriaQuery = criteriaBuilder.createQuery(LinkageType.class);
        Root<LinkageType> linkageTypeRoot = linkageTypeCriteriaQuery.from(LinkageType.class);
        Predicate statusNameCondition = criteriaBuilder.equal(linkageTypeRoot.get(LinkageType_.name), name);
        linkageTypeCriteriaQuery.where(statusNameCondition);
        TypedQuery<LinkageType> linkageTypeTypedQuery = entityManager.createQuery(linkageTypeCriteriaQuery);
        List<LinkageType> resultList = linkageTypeTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one status with same name %s should exist", name));
        }
        return null;
    }
}

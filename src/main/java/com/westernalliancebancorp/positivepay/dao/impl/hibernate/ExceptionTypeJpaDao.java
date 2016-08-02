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

import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionType_;

/**
 * Data access object JPA impl to work with Exception type model database operations.
 *
 * @author Moumita Ghosh
 */
@Repository
public class ExceptionTypeJpaDao extends GenericJpaDao<ExceptionType, Long> implements ExceptionTypeDao {
    @Override
    public ExceptionType findByName(ExceptionType.EXCEPTION_TYPE name) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExceptionType> ExceptionTypeCriteriaQuery = criteriaBuilder.createQuery(ExceptionType.class);
        Root<ExceptionType> ExceptionTypeRoot = ExceptionTypeCriteriaQuery.from(ExceptionType.class);
        Predicate exceptionNameCondition = criteriaBuilder.equal(ExceptionTypeRoot.get(ExceptionType_.exceptionType), name);
        ExceptionTypeCriteriaQuery.where(exceptionNameCondition);
        TypedQuery<ExceptionType> ExceptionTypeTypedQuery = entityManager.createQuery(ExceptionTypeCriteriaQuery);
        List<ExceptionType> resultList = ExceptionTypeTypedQuery.getResultList();
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

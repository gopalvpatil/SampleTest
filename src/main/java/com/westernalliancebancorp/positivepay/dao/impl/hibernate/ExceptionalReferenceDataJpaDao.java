package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionType_;
import org.hibernate.annotations.Fetch;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData.EXCEPTION_STATUS;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData_;

/**
 * @author akumar1
 */
@Repository
public class ExceptionalReferenceDataJpaDao extends
        GenericJpaDao<ExceptionalReferenceData, Long> implements
        ExceptionalReferenceDataDao {
    @Loggable
    private Logger logger;

    @Override
    public List<ExceptionalReferenceData> findAllExceptionalReferenceDataByExceptionTypeId(
            Long exceptionTypeId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExceptionalReferenceData> exceptionalReferenceDataCriteriaQuery = criteriaBuilder
                .createQuery(ExceptionalReferenceData.class);
        Root<ExceptionalReferenceData> exceptionalReferenceDataRoot = exceptionalReferenceDataCriteriaQuery
                .from(ExceptionalReferenceData.class);
        exceptionalReferenceDataRoot.fetch(ExceptionalReferenceData_.exceptionType, JoinType.INNER);
        Predicate exceptionStatusCondition = criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.exceptionType).
                get(ExceptionType_.id), exceptionTypeId);
        exceptionalReferenceDataCriteriaQuery
                .where(exceptionStatusCondition);
        TypedQuery<ExceptionalReferenceData> ExceptionalReferenceDataTypedQuery = entityManager
                .createQuery(exceptionalReferenceDataCriteriaQuery);
        List<ExceptionalReferenceData> resultList = ExceptionalReferenceDataTypedQuery
                .getResultList();
        return resultList;
    }

    @Override
    public ExceptionalReferenceData findBy(String traceNumber, String amount, String accountNumber) {
        ExceptionalReferenceData exceptionalReferenceData = null;
        try {
            EntityManager entityManager = this.getEntityManager();
            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<ExceptionalReferenceData> exceptionalReferenceDataCriteriaQuery = criteriaBuilder
                    .createQuery(ExceptionalReferenceData.class);
            Root<ExceptionalReferenceData> exceptionalReferenceDataRoot = exceptionalReferenceDataCriteriaQuery
                    .from(ExceptionalReferenceData.class);
            exceptionalReferenceDataCriteriaQuery.where(
                    criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.traceNumber), traceNumber),
                    criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.amount), amount),
                    criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.accountNumber), accountNumber)
            );
            TypedQuery<ExceptionalReferenceData> exceptionalReferenceDataTypedQuery = entityManager.createQuery(exceptionalReferenceDataCriteriaQuery);
            exceptionalReferenceData = exceptionalReferenceDataTypedQuery.getSingleResult();
        } catch (NoResultException nre) {
            logger.error("Exception occurred at ExceptionalReferenceDataJpaDao : findBy(String traceNumber, String amount, String accountNumber) " + nre.getMessage(), nre);
        }
        return exceptionalReferenceData;
    }

    @Override
    public List<ExceptionalReferenceData> findByCheckNumberAndAccountNumber(String checkNumber, String accountNumber) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ExceptionalReferenceData> exceptionalReferenceDataCriteriaQuery = criteriaBuilder
                .createQuery(ExceptionalReferenceData.class);
        Root<ExceptionalReferenceData> exceptionalReferenceDataRoot = exceptionalReferenceDataCriteriaQuery
                .from(ExceptionalReferenceData.class);
        exceptionalReferenceDataCriteriaQuery.where(
                criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.accountNumber), accountNumber),
                criteriaBuilder.equal(exceptionalReferenceDataRoot.get(ExceptionalReferenceData_.checkNumber), checkNumber)
        );
        TypedQuery<ExceptionalReferenceData> exceptionalReferenceDataTypedQuery = entityManager.createQuery(exceptionalReferenceDataCriteriaQuery);
        return exceptionalReferenceDataTypedQuery.getResultList();
    }
}

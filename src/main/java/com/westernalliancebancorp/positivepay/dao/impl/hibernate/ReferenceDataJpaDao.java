package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.ReferenceData.ITEM_TYPE;
import com.westernalliancebancorp.positivepay.model.ReferenceData.STATUS;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/19/14
 * Time: 10:50 PM
 */
@Repository
public class ReferenceDataJpaDao extends GenericJpaDao<ReferenceData, Long> implements ReferenceDataDao{
	
	@Override
    public List<ReferenceData> findAllReferenceDataBy(List<Long> ids) {          
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();               
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);                 
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);
        
		Fetch<ReferenceData, Account> accountType = referenceDataRoot.fetch(ReferenceData_.account, JoinType.LEFT);
		Fetch<Account, Bank> bank = accountType.fetch(Account_.bank, JoinType.LEFT);
        Fetch<ReferenceData, FileMetaData> referenceDataFileMetaDataFetch = referenceDataRoot.fetch(ReferenceData_.fileMetaData, JoinType.LEFT);
        Expression<Long> exp = referenceDataRoot.get(ReferenceData_.id);        
        Predicate predicate = exp.in(ids);       
        referenceDataCriteriaQuery.where(predicate,
                    criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.status), ReferenceData.STATUS.NOT_PROCESSED));     
        
        TypedQuery<ReferenceData> referenceDataQuery = entityManager.createQuery(referenceDataCriteriaQuery);            
        return referenceDataQuery.getResultList();
  }

    @Override
    public List<ReferenceData> findByCheckNumberAndAccountId(String checkNumber, Long accountId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);

        referenceDataCriteriaQuery.where(
               criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.checkNumber), checkNumber),
               criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.account).get(Account_.id), accountId)
        );
        TypedQuery<ReferenceData> referenceDataQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataQuery.getResultList();
    }

    @Deprecated
    @Override
    /**
     * Please start using public List<ReferenceData> findByDigestAndItemType(String digest, ReferenceData.ITEM_TYPE item_type)
     */
    public List<ReferenceData> findByCheckNumberAccountIdAndItemType(String checkNumber, Long accountId, ReferenceData.ITEM_TYPE item_type) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);

        referenceDataCriteriaQuery.where(
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.checkNumber), checkNumber),
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.itemType), item_type),
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.account).get(Account_.id), accountId)
        );
        TypedQuery<ReferenceData> referenceDataQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataQuery.getResultList();
    }

    @Override
    public List<ReferenceData> findByDigestAndItemType(String digest, ReferenceData.ITEM_TYPE item_type) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);

        referenceDataCriteriaQuery.where(
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.digest), digest),
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.itemType), item_type)
        );
        TypedQuery<ReferenceData> referenceDataQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataQuery.getResultList();
    }

    @Override
    public List<ReferenceData> findByCheckNumberAccountNumberAndItemType(String checkNumber, String accountNumber, ReferenceData.ITEM_TYPE item_type) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);

        referenceDataCriteriaQuery.where(
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.checkNumber), checkNumber),
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.itemType), item_type),
                criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.account).get(Account_.number), accountNumber)
        );
        TypedQuery<ReferenceData> referenceDataQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataQuery.getResultList();
    }

    @Override
    public List<ReferenceData> findByCheckNumberAndAccountIdByStatus(String checkNumber, Long accountId, ReferenceData.STATUS status ) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);
        
        Join<ReferenceData, Account> joinAccount = referenceDataRoot.join(ReferenceData_.account);
        Predicate accountIdcondition = criteriaBuilder.equal(joinAccount.get(Account_.id), accountId);
        
        Predicate statusNameCondition = criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.status), status);
        
        Predicate checkNumberCondition = criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.checkNumber), checkNumber);
        
        Predicate hybridPredicate = criteriaBuilder.and(accountIdcondition, statusNameCondition,checkNumberCondition);
        
        referenceDataCriteriaQuery.where(hybridPredicate);
       
        TypedQuery<ReferenceData> referenceDataTypedQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataTypedQuery.getResultList();

    }

    @Override
    public List<ReferenceData> findByCheckNumberAccountIdItemTypeAndStatus(
	    String checkNumber, Long accountId, ITEM_TYPE item_type,
	    STATUS status) {
	EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReferenceData> referenceDataCriteriaQuery = criteriaBuilder.createQuery(ReferenceData.class);
        Root<ReferenceData> referenceDataRoot = referenceDataCriteriaQuery.from(ReferenceData.class);
        
        Join<ReferenceData, Account> joinAccount = referenceDataRoot.join(ReferenceData_.account);
        Predicate accountIdcondition = criteriaBuilder.equal(joinAccount.get(Account_.id), accountId);
        
        Predicate statusNameCondition = criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.status), status);
        
        Predicate checkNumberCondition = criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.checkNumber), checkNumber);
        
        Predicate itemtypeCondition = criteriaBuilder.equal(referenceDataRoot.get(ReferenceData_.itemType), item_type);
        
        Predicate hybridPredicate = criteriaBuilder.and(accountIdcondition, statusNameCondition,checkNumberCondition,itemtypeCondition);
        
        referenceDataCriteriaQuery.where(hybridPredicate);
       
        TypedQuery<ReferenceData> referenceDataTypedQuery = entityManager.createQuery(referenceDataCriteriaQuery);
        return referenceDataTypedQuery.getResultList();
    }
}

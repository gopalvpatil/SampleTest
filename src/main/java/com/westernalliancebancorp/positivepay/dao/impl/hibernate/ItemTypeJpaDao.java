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
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.CheckStatus_;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ItemType_;

/**
 * Data access object JPA impl to work with Company model database operations.
 *
 * @author Anand Kumar
 */
@Repository
public class ItemTypeJpaDao extends GenericJpaDao<ItemType, Long> implements ItemTypeDao {

    @Override
    public List<ItemType> findAllActiveItemTypes() {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemType> itemTypeCriteriaQuery = criteriaBuilder.createQuery(ItemType.class);
        Root<ItemType> itemTypeRoot = itemTypeCriteriaQuery.from(ItemType.class);
        Predicate conditionisActive = criteriaBuilder.equal(itemTypeRoot.get(ItemType_.isActive), Boolean.TRUE);
        itemTypeCriteriaQuery.where(conditionisActive);
        TypedQuery<ItemType> itemTypeTypedQuery = entityManager.createQuery(itemTypeCriteriaQuery);
        return itemTypeTypedQuery.getResultList();
    }

    @Override
    public ItemType findByName(String itemName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemType> itemTypeCriteriaQuery = criteriaBuilder.createQuery(ItemType.class);
        Root<ItemType> itemTypeRoot = itemTypeCriteriaQuery.from(ItemType.class);
        Predicate itemPredicate = criteriaBuilder.like(itemTypeRoot.get(ItemType_.name), itemName);
        itemTypeCriteriaQuery.where(itemPredicate);
        TypedQuery<ItemType> itemTypeTypedQuery = entityManager.createQuery(itemTypeCriteriaQuery);
        //To avoid NoResult exception etc.
        List<ItemType> resultList = itemTypeTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one item type with name %s should exist", itemName));
        }
        return null;
    }

    @Override
    @Cacheable(cacheName = "ItemType.findByCode")
    public ItemType findByCode(String itemCode) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemType> itemTypeCriteriaQuery = criteriaBuilder.createQuery(ItemType.class);
        Root<ItemType> itemTypeRoot = itemTypeCriteriaQuery.from(ItemType.class);
        Predicate itemPredicate = criteriaBuilder.like(itemTypeRoot.get(ItemType_.itemCode), itemCode);
        itemTypeCriteriaQuery.where(itemPredicate);
        TypedQuery<ItemType> itemTypeTypedQuery = entityManager.createQuery(itemTypeCriteriaQuery);
        //To avoid NoResult exception etc.
        List<ItemType> resultList = itemTypeTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one item type with code %s should exist", itemCode));
        }
        return null;
    }
}

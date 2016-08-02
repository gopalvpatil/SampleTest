package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.googlecode.ehcache.annotations.Cacheable;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.RoleDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:42 PM
 */
@Repository
public class BankJpaDao extends GenericJpaDao<Bank, Long> implements BankDao {
    @Override
    public List<Bank> findByParentId(Long parentBankId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bank> bankCriteriaQuery = criteriaBuilder.createQuery(Bank.class);
        Root<Bank> bankRoot = bankCriteriaQuery.from(Bank.class);

        Join<Bank, Bank> join = bankRoot.join(Bank_.parent);
        Predicate condition = criteriaBuilder.equal(join.get(Bank_.id), parentBankId);
        bankCriteriaQuery.where(condition);
        TypedQuery<Bank> bankTypedQuery = entityManager.createQuery(bankCriteriaQuery);
        return bankTypedQuery.getResultList();
    }

	@Override
    @Cacheable(cacheName = "findByAssignedBankNumber")
	public Bank findByAssignedBankNumber(Short assignedBankNumber) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bank> bankCriteriaQuery = criteriaBuilder.createQuery(Bank.class);
        Root<Bank> bankRoot = bankCriteriaQuery.from(Bank.class);

        bankCriteriaQuery.where(
                criteriaBuilder.equal(bankRoot.get(Bank_.assignedBankNumber), assignedBankNumber)
        );
        
        TypedQuery<Bank> allQuery = getEntityManager().createQuery(bankCriteriaQuery);
        return allQuery.getSingleResult();
	}
}

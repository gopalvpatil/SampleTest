package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import com.googlecode.ehcache.annotations.Cacheable;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Account_;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Bank_;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Company_;
import com.westernalliancebancorp.positivepay.model.UserDetail;

/**
 * AccountJpaDao is
 * 
 * @author Giridhar Duggirala
 */
@Repository("accountDao")
public class AccountJpaDao extends GenericJpaDao<Account, Long> implements
		AccountDao {
	@Override
	public Account findByAccountNumberAndBankId(String accountNumber,
			Long bankId) {
		try {
			EntityManager entityManager = this.getEntityManager();
			CriteriaBuilder criteriaBuilder = entityManager
					.getCriteriaBuilder();
			CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
					.createQuery(Account.class);
			Root<Account> accountRoot = accountCriteriaQuery
					.from(Account.class);
			Predicate conditionAccountNumber = criteriaBuilder.like(
					accountRoot.get(Account_.number), accountNumber);
			Predicate conditionBankId = criteriaBuilder.equal(
					accountRoot.get(Account_.bank).get(Bank_.id), bankId);
			accountCriteriaQuery.where(conditionAccountNumber, conditionBankId);
			TypedQuery<Account> accountTypedQuery = entityManager
					.createQuery(accountCriteriaQuery);
			return accountTypedQuery.getSingleResult();
		} catch (NoResultException ne) {
			return null;
		}

	}

	@Override
	public List<Account> findAllByCompanyId(String companyId) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		Predicate conditionCompanyId = criteriaBuilder.equal(
				accountRoot.get(Account_.company).get(Company_.id), companyId);
		accountCriteriaQuery.where(conditionCompanyId);
		TypedQuery<Account> allQuery = getEntityManager().createQuery(
				accountCriteriaQuery);
		return allQuery.getResultList();
	}

	@Override
	public List<Account> findAllByBank(Bank bank) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		Predicate conditionBankId = criteriaBuilder.equal(
				accountRoot.get(Account_.bank).get(Bank_.id), bank.getId());
		accountCriteriaQuery.where(conditionBankId);
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
    @Cacheable(cacheName = "findAllByBankId")
	public List<Account> findAllByBankId(Long bankId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		Predicate conditionBankId = criteriaBuilder.equal(
				accountRoot.get(Account_.bank).get(Bank_.id), bankId);
		accountCriteriaQuery.where(conditionBankId);
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
	public List<Account> findAllByCompany(Company company) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		Predicate conditionBankId = criteriaBuilder.equal(
				accountRoot.get(Account_.company).get(Company_.id),
				company.getId());
		accountCriteriaQuery.where(conditionBankId);
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
	public List<Account> findAllByCompany(Long companyId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		Predicate conditionBankId = criteriaBuilder.equal(
				accountRoot.get(Account_.company).get(Company_.id), companyId);
		accountCriteriaQuery.where(conditionBankId);
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
	public List<Account> findAllByCompanyIds(List<Long> companyIds) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		accountCriteriaQuery.select(accountRoot).where(
				accountRoot.get(Account_.company).get(Company_.id)
						.in(companyIds));
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
	public List<Account> findAllByBankIds(Long bankIds) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
		accountCriteriaQuery.select(accountRoot).where(
				accountRoot.get(Account_.bank).get(Bank_.id).in(bankIds));
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getResultList();
	}

	@Override
	public Account findByAccountNumberAndCompanyId(String accountNumber,
			Long companyId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder
				.createQuery(Account.class);
		Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);
        accountRoot.fetch(Account_.bank, JoinType.LEFT);
		Predicate conditionAccountNumber = criteriaBuilder.like(
				accountRoot.get(Account_.number), accountNumber);
		Predicate conditionCompanyId = criteriaBuilder.equal(
				accountRoot.get(Account_.company).get(Company_.id), companyId);
		accountCriteriaQuery.where(conditionAccountNumber, conditionCompanyId);
		TypedQuery<Account> accountTypedQuery = entityManager
				.createQuery(accountCriteriaQuery);
		return accountTypedQuery.getSingleResult();
	}

    @Override
    public List<Account> findByAccountIds(List<Long> accountIds) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> accountCriteriaQuery = criteriaBuilder.createQuery(Account.class);
        Root<Account> accountRoot = accountCriteriaQuery.from(Account.class);

        Expression<Long> exp = accountRoot.get(Account_.id);
        Predicate predicate = exp.in(accountIds);
        accountCriteriaQuery.where(predicate);
        TypedQuery<Account> accountTypedQuery = entityManager.createQuery(accountCriteriaQuery);
        return accountTypedQuery.getResultList();
    }

     /**
	 * This method pull the valid mappings of Account entity from the Database
	 */
	@Override
	public List<Account> findByAccountNumberCompanyIdBank(Long bankId,
			Long companyId) {
		EntityManager entityManager = this.getEntityManager();
		Query q = entityManager
				.createQuery("SELECT x FROM Account x WHERE x.bank.id = ?1 and x.company.id =?2");
		q.setParameter(1, bankId);
		q.setParameter(2, companyId);
		List<Account> results = (List<Account>) q.getResultList();
		return results;
	}


    @Override
    public Set<Account>  getAccountByUserDetailId(long userId) {
    	EntityManager entityManager = this.getEntityManager();
		Query q = entityManager.createQuery("SELECT x FROM UserDetail as x join fetch x.accounts WHERE x.id = ?1");
		q.setParameter(1, userId);
		UserDetail userDetail = (UserDetail)q.getSingleResult();
		Set<Account> accounts = userDetail.getAccounts();
		return accounts;
    }
    

    @Override
	public Account findByAccountNumber(String accountNo) {
		EntityManager entityManager = this.getEntityManager();
		Query q = entityManager
				.createQuery("SELECT x FROM Account x WHERE x.number = ?1 ");
		q.setParameter(1, accountNo);
		Account account = (Account) q.getSingleResult();
		return account;
	}
}

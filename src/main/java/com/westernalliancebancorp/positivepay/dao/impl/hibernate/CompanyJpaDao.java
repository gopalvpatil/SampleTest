/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import com.westernalliancebancorp.positivepay.model.*;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.CompanyDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;

/**
 * Data access object JPA impl to work with Company model database operations.
 * @author Anand Kumar
 *
 */
@Repository
public class CompanyJpaDao extends GenericJpaDao<Company, Long> implements CompanyDao {
	@Override
	public List<Company> findAllByBankIds(List<Long> bankIds) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> companyCriteriaQuery = criteriaBuilder
				.createQuery(Company.class);
		Root<Company> companyRoot = companyCriteriaQuery.from(Company.class);
		companyCriteriaQuery.select(companyRoot).where(
				companyRoot.get(Company_.bank).get(Bank_.id)
						.in(bankIds));
		TypedQuery<Company> companyTypedQuery = entityManager
				.createQuery(companyCriteriaQuery);
		return companyTypedQuery.getResultList();
	}
	
	@Override
	public Company getCompanyDetail(Long companyId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> companyCriteriaQuery = criteriaBuilder
				.createQuery(Company.class);
		Root<Company> companyRoot = companyCriteriaQuery.from(Company.class);
		companyRoot.fetch(Company_.accounts, JoinType.LEFT).fetch(Account_.userDetails, JoinType.LEFT).fetch(UserDetail_.baseRole, JoinType.LEFT);
		companyRoot.fetch(Company_.contacts, JoinType.LEFT);
		companyCriteriaQuery.where(criteriaBuilder.equal(companyRoot.get(Company_.id),companyId)); //where company.id = :companyId
		TypedQuery<Company> companyTypedQuery = entityManager
				.createQuery(companyCriteriaQuery);		
		return companyTypedQuery.getSingleResult();
	}
	
	@Override
	public Company getCompanyDetails(Long companyId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> companyCriteriaQuery = criteriaBuilder
				.createQuery(Company.class);
		Root<Company> companyRoot = companyCriteriaQuery.from(Company.class);
		companyRoot.fetch(Company_.contacts, JoinType.LEFT);
		companyRoot.fetch(Company_.accounts, JoinType.LEFT);
		companyCriteriaQuery.where(criteriaBuilder.equal(companyRoot.get(Company_.id),companyId)); //where company.id = :companyId
		TypedQuery<Company> companyTypedQuery = entityManager
				.createQuery(companyCriteriaQuery);		
		return companyTypedQuery.getSingleResult();
	}

    @Override
    public List<Company> findAllActiveCompanies() {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> companyCriteriaQuery = criteriaBuilder
                .createQuery(Company.class);
        Root<Company> companyRoot = companyCriteriaQuery.from(Company.class);
        companyCriteriaQuery.where(criteriaBuilder.equal(companyRoot.get(Company_.isActive),Boolean.TRUE));
        TypedQuery<Company> companyTypedQuery = entityManager
                .createQuery(companyCriteriaQuery);
        return companyTypedQuery.getResultList();
    }

	@Override
	public List<Company> findAllByUserName(String userName) {
		EntityManager entityManager = this.getEntityManager();
		//Query q = entityManager.createQuery("from UserDetail as ud left join fetch ud.accounts as account left join fetch account.company as company where ud.id = ?1");
		Query q = entityManager.createQuery("select distinct c from Company as c left join fetch c.accounts as account left join fetch account.userDetails as ud where ud.userName = ?1");
		q.setParameter(1, userName);
        try {
        	List<Company> companies = (List<Company>) q.getResultList();
        	return companies;
        } catch (NoResultException nre) {
            nre.printStackTrace();
        }
        
		return null;
	}
}

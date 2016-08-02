package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserDetail_;

/**
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:04 PM
 */
@Repository
public class UserDetailJpaDao extends GenericJpaDao<UserDetail, Long> implements UserDetailDao {

    @Override
    public UserDetail findByUserName(String userName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userRoot = userCriteriaQuery.from(UserDetail.class);
        Predicate condition = criteriaBuilder.like(userRoot.get(UserDetail_.userName), userName);
        userCriteriaQuery.where(condition);
        TypedQuery<UserDetail> userTypedQuery = entityManager.createQuery(userCriteriaQuery);
        return userTypedQuery.getSingleResult();
    }

    @Override
    public List<UserDetail> findBy(String corporateUserName, String institutionId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userRoot = userCriteriaQuery.from(UserDetail.class);
        Predicate condition1 = criteriaBuilder.like(userRoot.get(UserDetail_.corporateUserName), corporateUserName);
        Predicate condition2 = criteriaBuilder.like(userRoot.get(UserDetail_.institutionId), institutionId);
        Predicate hybridPredicate = criteriaBuilder.and(condition1, condition2);
        userCriteriaQuery.where(hybridPredicate);
        TypedQuery<UserDetail> userTypedQuery = entityManager.createQuery(userCriteriaQuery);
        return userTypedQuery.getResultList();
    }

    @Override
    public Set<Account> getAccountByUserDetailId(long id) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("FROM UserDetail as ud inner join fetch ud.accounts WHERE ud.id = ?1");
        q.setParameter(1, id);
        UserDetail userDetail = (UserDetail) q.getSingleResult();
        return userDetail.getAccounts();
    }

    @Override
    public Set<Account> getAccountByUserName(String userName) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("FROM UserDetail as ud inner join fetch ud.accounts WHERE ud.username = ?1");
        q.setParameter(1, userName);
        UserDetail userDetail  = (UserDetail)q.getSingleResult();
        return userDetail.getAccounts();
    }

    @Override
    public Set<Permission> getPermissionsByUserDetailId(Long userId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userDetailCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userDetailRoot = userDetailCriteriaQuery.from(UserDetail.class);
        userDetailRoot.fetch(UserDetail_.permissions, JoinType.LEFT);
        Predicate condition = criteriaBuilder.equal(userDetailRoot.get(UserDetail_.id), userId);
        userDetailCriteriaQuery.where(condition);
        TypedQuery<UserDetail> userDetailTypedQuery = entityManager.createQuery(userDetailCriteriaQuery);
        UserDetail userDetail =  userDetailTypedQuery.getSingleResult();
        return userDetail.getPermissions();
    }
    
    @Override
    public UserDetail findUserDetailAndAccountsByUserName(String userName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userRoot = userCriteriaQuery.from(UserDetail.class);
        userRoot.fetch(UserDetail_.accounts, JoinType.LEFT);
        Predicate condition = criteriaBuilder.like(userRoot.get(UserDetail_.userName), userName);
        userCriteriaQuery.where(condition);
        TypedQuery<UserDetail> userTypedQuery = entityManager.createQuery(userCriteriaQuery);
        return userTypedQuery.getSingleResult();
    }

    /**
     * In the managed users page , show all the archived users
     */
    @Override
    public List<UserDetail> showArchivedUsers() {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("SELECT x FROM UserDetail as x WHERE x.isActive = FALSE");
        List<UserDetail> userDetails = (List<UserDetail>) q.getResultList();
        return userDetails;
    }

    /**
     * Fetch User Details By Account Number
     */
    @Override
    public Set<UserDetail> findUserDetailByAccountNumber(String number) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("SELECT x FROM Account as x join fetch x.userDetails WHERE x.number = ?1");
        q.setParameter(1, number);
        Account account = (Account) q.getSingleResult();
        Set<UserDetail> details = new HashSet<UserDetail>(account.getUserDetails());
        return details;
    }

    /**
     * Fetch UserDetails based on BankId
     */
    @Override
    public Set<UserDetail> findUserDetailBySpecificBank(Long bankId) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("SELECT x FROM Account as x join fetch x.userDetails WHERE x.bank.id = ?1");
        q.setParameter(1, bankId);
        List<Account> accounts = (List<Account>) q.getResultList();
        Set<UserDetail> details = new HashSet<UserDetail>();
        for(Account account : accounts)
        {
            details.addAll(account.getUserDetails());
        }

        return details;
    }

    /**
     * Latest Comment : Broken because of the scehma change .....
     * Fetch User details by company Id
     */
    @Override
    public Set<UserDetail> findUserDetailByCompanyId(Long companyId) {
        EntityManager entityManager = this.getEntityManager();
        Query q = entityManager.createQuery("SELECT x FROM Account as x join fetch x.userDetails WHERE x.company.id = ?1");
        q.setParameter(1, companyId);
        List<Account> accounts = (List<Account>) q.getResultList();
        Set<UserDetail> details = new HashSet<UserDetail>();
        for(Account account : accounts)
        {
            details.addAll(account.getUserDetails());
        }
        return details;
    }
    
    /**
     * Fetches user detail along with Permissions and Role
     */
    @Override
    public UserDetail findUserPermissionsAndRoleByUserName(String userName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userRoot = userCriteriaQuery.from(UserDetail.class);
        userRoot.fetch(UserDetail_.permissions, JoinType.LEFT);
        userRoot.fetch(UserDetail_.baseRole, JoinType.LEFT);
        Predicate condition = criteriaBuilder.like(userRoot.get(UserDetail_.userName), userName);
        userCriteriaQuery.where(condition);
        TypedQuery<UserDetail> userTypedQuery = entityManager.createQuery(userCriteriaQuery);
        return userTypedQuery.getSingleResult();
    }

    @Override
    public UserDetail findUserRoleByUserName(String userName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserDetail> userCriteriaQuery = criteriaBuilder.createQuery(UserDetail.class);
        Root<UserDetail> userRoot = userCriteriaQuery.from(UserDetail.class);
        userRoot.fetch(UserDetail_.baseRole, JoinType.LEFT);
        Predicate condition = criteriaBuilder.like(userRoot.get(UserDetail_.userName), userName);
        userCriteriaQuery.where(condition);
        TypedQuery<UserDetail> userTypedQuery = entityManager.createQuery(userCriteriaQuery);
        return userTypedQuery.getSingleResult();
    }

	@Override
	public boolean isUserFullRecon(String userName) {
		EntityManager entityManager = this.getEntityManager();
		Query query = entityManager.createQuery("select count(*) from Account a join a.userDetails ud where ud.userName = :username and a.accountServiceOption.id = 1");
		query.setParameter("username", userName);
		
		long count = (Long)query.getSingleResult();
		if(count > 0) {
			return true;
		}
		
		return false;
	}
}

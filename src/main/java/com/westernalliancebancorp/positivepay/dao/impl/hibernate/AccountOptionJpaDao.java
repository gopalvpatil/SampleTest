package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.AccountOptionDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AccountServiceOption;

@Repository("accountOptionDao")
public class AccountOptionJpaDao extends
		GenericJpaDao<AccountServiceOption, Long> implements AccountOptionDao {

	@Override
	public AccountServiceOption fetchAccountServiceOptionInfo(String number) {
		EntityManager entityManager = this.getEntityManager();
		Query q = entityManager
				.createQuery("SELECT x FROM Account as x join fetch x.accountServiceOption WHERE x.number = ?1");
		q.setParameter(1, number);
		Account account = (Account) q.getSingleResult();
		return account.getAccountServiceOption();
	}

}

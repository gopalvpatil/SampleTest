package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.westernalliancebancorp.positivepay.log.Loggable;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.AccountCycleCutOffDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AccountCycleCutOff;

@Repository("accountCycleCutOffDao")
public class AccountCycleCutOffJpaDao extends GenericJpaDao<AccountCycleCutOff, Long> implements
		AccountCycleCutOffDao {

    @Loggable
    Logger logger;
	@Override
	public AccountCycleCutOff fetchAccountCycleInfo(String number) {
		EntityManager entityManager = this.getEntityManager();
		Query q = entityManager.createQuery("SELECT x FROM Account as x join fetch x.accountCycleCutOff WHERE x.number = ?1");
		q.setParameter(1, number);
        try{
		Account account = (Account) q.getSingleResult();
		return account.getAccountCycleCutOff();
        }catch (NoResultException nre) {
            logger.error("No result found for AccountCycleCutOff with account number "+number, nre);
            nre.printStackTrace();
        }
        return null;
	}
}
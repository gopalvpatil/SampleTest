package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.model.Sso_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:42 PM
 */
@Repository(value = "ssoDao")
public class SsoJpaDao extends GenericJpaDao<Sso, Long> implements SsoDao {

    @Override
    public Sso findByUid(String uid) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Sso> ssoCriteriaQuery = criteriaBuilder.createQuery(Sso.class);
        Root<Sso> ssoRoot = ssoCriteriaQuery.from(Sso.class);
        Predicate condition = criteriaBuilder.equal(ssoRoot.get(Sso_.uid), uid);
        ssoCriteriaQuery.where(condition);
        TypedQuery<Sso> ssoTypedQuery = entityManager.createQuery(ssoCriteriaQuery);
        return ssoTypedQuery.getSingleResult();
    }
}

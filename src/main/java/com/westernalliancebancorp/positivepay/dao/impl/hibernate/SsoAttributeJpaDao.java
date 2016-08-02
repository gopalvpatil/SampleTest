package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.SsoAttributeDao;
import com.westernalliancebancorp.positivepay.dao.SsoDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.Sso;
import com.westernalliancebancorp.positivepay.model.SsoAttribute;
import com.westernalliancebancorp.positivepay.model.SsoAttribute_;
import com.westernalliancebancorp.positivepay.model.Sso_;
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
@Repository(value = "ssoAttributeDao")
public class SsoAttributeJpaDao extends GenericJpaDao<SsoAttribute, Long> implements SsoAttributeDao {
    @Override
    public List<SsoAttribute> findBySsoId(Long ssoId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<SsoAttribute> ssoAttributeCriteriaQuery = criteriaBuilder.createQuery(SsoAttribute.class);
        Root<SsoAttribute> ssoAttributeRoot = ssoAttributeCriteriaQuery.from(SsoAttribute.class);
        Join<SsoAttribute, Sso> join = ssoAttributeRoot.join(SsoAttribute_.sso);
        Predicate condition = criteriaBuilder.equal(join.get(Sso_.id), ssoId);
        ssoAttributeCriteriaQuery.where(condition);
        TypedQuery<SsoAttribute> ssoAttributeTypedQuery = entityManager.createQuery(ssoAttributeCriteriaQuery);
        return ssoAttributeTypedQuery.getResultList();
    }
}

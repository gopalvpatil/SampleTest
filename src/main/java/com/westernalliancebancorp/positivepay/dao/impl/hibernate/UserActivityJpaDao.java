package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.UserActivityDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

/**
 * UserActivityJpaDao is
 *
 * @author Giridhar Duggirala
 */

@Repository
public class UserActivityJpaDao extends GenericJpaDao<UserActivity, Long> implements UserActivityDao {
    @Override
    public UserActivity findByName(String userActivityName) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserActivity> userActivityCriteriaQuery = criteriaBuilder.createQuery(UserActivity.class);
        Root<UserActivity> userActivityRoot = userActivityCriteriaQuery.from(UserActivity.class);

        Predicate condition = criteriaBuilder.like(userActivityRoot.get(UserActivity_.name), userActivityName);
        userActivityCriteriaQuery.where(condition);
        TypedQuery<UserActivity> userActivityTypedQuery = entityManager.createQuery(userActivityCriteriaQuery);
        List<UserActivity> resultList = userActivityTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one user activity with same name %s should exist", userActivityName));
        }
        return null;
    }
}

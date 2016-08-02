package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.exception.PositivePayRuleVoilationException;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.Action_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * ActionJpaDao is
 *
 * @author Giridhar Duggirala
 */
@Repository
public class ActionJpaDao extends GenericJpaDao<Action, Long> implements ActionDao {
    @Override
    public Action findByNameAndVersion(String actionName, Integer version, Action.ACTION_TYPE action_type) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Action> actionCriteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = actionCriteriaQuery.from(Action.class);
        Predicate actionNameCondition = criteriaBuilder.like(actionRoot.get(Action_.name), actionName);
        Predicate versionCondition = criteriaBuilder.equal(actionRoot.get(Action_.version), version);
        Predicate actionTypeCondition  = criteriaBuilder.equal(actionRoot.get(Action_.actionType), action_type);
        Predicate hybridPredicate = criteriaBuilder.and(actionNameCondition, versionCondition, actionTypeCondition);
        actionCriteriaQuery.where(hybridPredicate);
        TypedQuery<Action> actionTypedQuery = entityManager.createQuery(actionCriteriaQuery);
        //To avoid NoResult exception etc.
        List<Action> resultList = actionTypedQuery.getResultList();
        if (resultList != null && resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList == null) {
            return null;
        } else if (resultList.size() > 1) {
            throw new PositivePayRuleVoilationException(String.format("Not more than one status with same version %s and name %s should exist", version, actionName));
        }
        return null;
    }
    
    @Override
    public List<Action> findAllAdminActions() {
    	EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Action> actionCriteriaQuery = criteriaBuilder.createQuery(Action.class);
        Root<Action> actionRoot = actionCriteriaQuery.from(Action.class);
        Predicate isAdminCondition = criteriaBuilder.equal(actionRoot.get(Action_.isAnAdminAction), new Boolean(true));
        actionCriteriaQuery.where(isAdminCondition);
        TypedQuery<Action> actionTypedQuery = entityManager.createQuery(actionCriteriaQuery);
        //To avoid NoResult exception etc.
        List<Action> resultList = actionTypedQuery.getResultList();
        return resultList;
    }
}

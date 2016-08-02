package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.AuditInfo_;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.CheckHistory_;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.CheckStatus_;
import com.westernalliancebancorp.positivepay.model.Check_;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;

/**
 * CheckHistoryJpaDao is
 *
 * @author Giridhar Duggirala
 */

@Repository
public class CheckHistoryJpaDao extends GenericJpaDao<CheckHistory, Long> implements CheckHistoryDao {
    
    @Loggable
    private Logger logger;
	
	@Override
    public List<CheckHistory> findByCheckId(Long checkId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckHistory> checkHistoryCriteriaQuery = criteriaBuilder.createQuery(CheckHistory.class);
        Root<CheckHistory> checkHistoryRoot = checkHistoryCriteriaQuery.from(CheckHistory.class);
        
        Join<CheckHistory, Check> join = checkHistoryRoot.join(CheckHistory_.check);
        Predicate condition = criteriaBuilder.equal(join.get(Check_.id), checkId);
        checkHistoryCriteriaQuery.where(condition);
        checkHistoryCriteriaQuery.orderBy(criteriaBuilder.asc(checkHistoryRoot.get(CheckHistory_.id)));
        TypedQuery<CheckHistory> checkHistoryTypedQuery = entityManager.createQuery(checkHistoryCriteriaQuery);
        return checkHistoryTypedQuery.getResultList();
    
    }

    @Override
    public List<CheckHistory> findOrderedCheckHistory(Long checkId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckHistory> checkHistoryCriteriaQuery = criteriaBuilder.createQuery(CheckHistory.class);
        Root<CheckHistory> checkHistoryRoot = checkHistoryCriteriaQuery.from(CheckHistory.class);
        checkHistoryRoot.fetch(CheckHistory_.targetCheckStatus, JoinType.LEFT);
        checkHistoryRoot.fetch(CheckHistory_.check, JoinType.LEFT).fetch(Check_.checkStatus, JoinType.LEFT);
        checkHistoryRoot.fetch(CheckHistory_.referenceData, JoinType.LEFT);
        checkHistoryRoot.fetch(CheckHistory_.action, JoinType.LEFT);
        checkHistoryRoot.fetch(CheckHistory_.formerCheckStatus, JoinType.LEFT);
        checkHistoryRoot.fetch(CheckHistory_.checkStatus, JoinType.LEFT);
        Join<CheckHistory, Check> join = checkHistoryRoot.join(CheckHistory_.check);
        Predicate condition = criteriaBuilder.equal(join.get(Check_.id), checkId);
        checkHistoryCriteriaQuery.where(condition);
        checkHistoryCriteriaQuery.orderBy(criteriaBuilder.desc(checkHistoryRoot.get(CheckHistory_.id)));
        TypedQuery<CheckHistory> checkHistoryTypedQuery = entityManager.createQuery(checkHistoryCriteriaQuery);
        return checkHistoryTypedQuery.getResultList();
    }

    @Override
    public List<CheckHistory> findByCheckIdWithCheckStatus(Long checkId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckHistory> checkHistoryCriteriaQuery = criteriaBuilder.createQuery(CheckHistory.class);
        Root<CheckHistory> checkHistoryRoot = checkHistoryCriteriaQuery.from(CheckHistory.class);
        javax.persistence.criteria.Fetch<CheckHistory, CheckStatus> targetCheckStatus = checkHistoryRoot.fetch(CheckHistory_.targetCheckStatus, JoinType.LEFT);
        javax.persistence.criteria.Fetch<CheckHistory, Action> action = checkHistoryRoot.fetch(CheckHistory_.action, JoinType.LEFT);
        javax.persistence.criteria.Fetch<CheckHistory, CheckStatus> formerCheckStatus = checkHistoryRoot.fetch(CheckHistory_.formerCheckStatus, JoinType.LEFT);
        javax.persistence.criteria.Fetch<CheckHistory, CheckStatus> checkStatus = checkHistoryRoot.fetch(CheckHistory_.checkStatus, JoinType.LEFT);
        Join<CheckHistory, Check> join = checkHistoryRoot.join(CheckHistory_.check);
        Predicate condition = criteriaBuilder.equal(join.get(Check_.id), checkId);
        checkHistoryCriteriaQuery.where(condition);
        TypedQuery<CheckHistory> checkHistoryTypedQuery = entityManager.createQuery(checkHistoryCriteriaQuery);
        return checkHistoryTypedQuery.getResultList();

    }
	
	@Override
    public List<CheckHistory> findByCheckIdandStatusId(Long checkId,Long statusId) {
        EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CheckHistory> checkHistoryCriteriaQuery = criteriaBuilder.createQuery(CheckHistory.class);
        Root<CheckHistory> checkHistoryRoot = checkHistoryCriteriaQuery.from(CheckHistory.class);
        
        Join<CheckHistory, Check> join = checkHistoryRoot.join(CheckHistory_.check);
        Predicate checkIdcondition = criteriaBuilder.equal(join.get(Check_.id), checkId);
        
        Join<CheckHistory, CheckStatus> joinStatus = checkHistoryRoot.join(CheckHistory_.formerCheckStatus);
        Predicate statusIdcondition = criteriaBuilder.equal(joinStatus.get(CheckStatus_.id), statusId);
        
        Predicate hybridPredicate = criteriaBuilder.and(checkIdcondition, statusIdcondition);
        
        Join<CheckHistory, AuditInfo> auditJoin = checkHistoryRoot.join(CheckHistory_.auditInfo);
        
                checkHistoryCriteriaQuery.where(hybridPredicate);
                checkHistoryCriteriaQuery.orderBy(criteriaBuilder.desc((auditJoin.get(AuditInfo_.dateCreated))));
        TypedQuery<CheckHistory> checkHistoryTypedQuery = entityManager.createQuery(checkHistoryCriteriaQuery);
        return checkHistoryTypedQuery.getResultList();
    }

    @Override
    public CheckHistory save(CheckHistory entity) {
        if (entity.getSource() == null) {
            if (PositivePayThreadLocal.getSource() != null) {
                entity.setSource(PositivePayThreadLocal.getSource());
            } else {
        /*
         * This should not happen, source should get either batch for
		 * jobs or actions for UI
		 */
                entity.setSource(PositivePayThreadLocal.SOURCE.Unknown.name());
                logger.error("Source not available in PositivePayThreadLocal there must be a scenario where we are not setting thread source information in thread. So lets take the thread dump to investigate more about it.");
                Thread.dumpStack();
            }
        }
        return super.save(entity);
    }
}

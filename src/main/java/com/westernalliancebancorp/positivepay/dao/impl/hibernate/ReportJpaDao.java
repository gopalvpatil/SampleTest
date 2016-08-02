package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Order;

import com.westernalliancebancorp.positivepay.model.*;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ReportDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;

@Repository
public class ReportJpaDao extends GenericJpaDao<Report, Long> implements ReportDao {
	@Override
	public List<Report> findByUserName(String userName) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Report> reportCriteriaQuery = criteriaBuilder.createQuery(Report.class);
        Root<Report> reportRoot = reportCriteriaQuery.from(Report.class);
        Predicate conditionUserId = criteriaBuilder.equal(reportRoot.get(Report_.userDetail).get(UserDetail_.userName), userName);

        Join<Report, AuditInfo> auditJoin = reportRoot.join(Report_.auditInfo);
        
        reportCriteriaQuery.where(conditionUserId);
        reportCriteriaQuery.orderBy(criteriaBuilder.desc((auditJoin.get(AuditInfo_.dateCreated))));
        TypedQuery<Report> reportTypedQuery = entityManager.createQuery(reportCriteriaQuery);
        return reportTypedQuery.getResultList();
	}
}
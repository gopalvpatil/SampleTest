package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.ReportTemplateDao;
import com.westernalliancebancorp.positivepay.dao.common.GenericJpaDao;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;
import com.westernalliancebancorp.positivepay.model.ReportTemplate_;

@Repository
public class ReportTemplateJpaDao extends GenericJpaDao<ReportTemplate, Long> implements ReportTemplateDao {
	@Override
	public List<ReportTemplate> findReportTemplates(boolean fullRecon) {
		EntityManager entityManager = this.getEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReportTemplate> reportTemplateCriteriaQuery = criteriaBuilder.createQuery(ReportTemplate.class);
        Root<ReportTemplate> reportTemplateRoot = reportTemplateCriteriaQuery.from(ReportTemplate.class);

        Predicate conditionAccountServiceOptionPositivePay = criteriaBuilder.equal(reportTemplateRoot.get(ReportTemplate_.accountServiceOption), "P");
        Predicate conditionAccountServiceOptionFullRecon = criteriaBuilder.equal(reportTemplateRoot.get(ReportTemplate_.accountServiceOption), "F");

        if (fullRecon == true) {
        	reportTemplateCriteriaQuery.where(criteriaBuilder.or(conditionAccountServiceOptionPositivePay, conditionAccountServiceOptionFullRecon));
        } else {        	
            reportTemplateCriteriaQuery.where(conditionAccountServiceOptionPositivePay);
        }
        
        TypedQuery<ReportTemplate> reportTemplateTypedQuery = entityManager.createQuery(reportTemplateCriteriaQuery);
        return reportTemplateTypedQuery.getResultList();
	}
}
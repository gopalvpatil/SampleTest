package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;

public interface ReportTemplateDao extends GenericDao<ReportTemplate, Long> {
    List<ReportTemplate> findReportTemplates(boolean fullRecon);
}

package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.Report;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;

/**
 * User:	Gopal Patil
 * Date:	Feb 7, 2014
 * Time:	1:15:35 AM
 */
public interface ReportService {
	Report findById(Long id);
	List<Report> findAllReportsByUserName(String userName);
	List<ReportTemplate> findAllReportTemplates();
	ReportTemplate getReportTemplateById(Long id);
	List<ReportTemplate> findReportTemplatesForLoggedInUser();
	Report saveOrUpdate(Report report);
	void delete(Report report);
}

package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;
import com.westernalliancebancorp.positivepay.dao.ReportDao;
import com.westernalliancebancorp.positivepay.dao.ReportTemplateDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Report;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.ReportService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

/**
 * User:	Gopal Patil
 * Date:	Feb 7, 2014
 * Time:	1:23:39 AM
 */

@Service
public class ReportServiceImpl implements ReportService {
	@Loggable
	private Logger logger;
	@Autowired
	private ReportDao reportDao;
	@Autowired
	private ReportTemplateDao reportTemplateDao;
	@Autowired
	private UserDetailDao userDao;
	
	@Override
	public Report findById(Long id) {
		return reportDao.findById(id);
	}
	
	@Override
	public List<Report> findAllReportsByUserName(String userName) {
		return reportDao.findByUserName(userName);
	}
	
	@Override
	public List<ReportTemplate> findAllReportTemplates() {
		return reportTemplateDao.findAll();
	}

	@Override
	public ReportTemplate getReportTemplateById(Long id) {
		return reportTemplateDao.findById(id);
	}
	
	@Override
	@RollbackForEmulatedUser
	public Report saveOrUpdate(Report report) {
		report.setUserDetail(getUserDetail());
		if(report.getId() != null) {
			//Get the current record
			Report reportFromDB = reportDao.findById(report.getId());
			AuditInfo auditInfo = new AuditInfo();
			auditInfo.setCreatedBy(reportFromDB.getAuditInfo().getCreatedBy());
			auditInfo.setDateCreated(reportFromDB.getAuditInfo().getDateCreated());
			//Set the audit Info in the file mapping.
			report.setAuditInfo(auditInfo);
			logger.info("Report with id = {} updated", report.getId());
			return reportDao.update(report);
		}
		else {
			logger.info("Report with name = {} getting created", report.getName());
			return reportDao.save(report);
		}
	}
	
	@Override
	@RollbackForEmulatedUser
	public void delete(Report report) {
		reportDao.delete(report);
	}
	
	private UserDetail getUserDetail() {
		String userName = SecurityUtility.getPrincipal();
		return userDao.findByUserName(userName);
	}

	@Override
	public List<ReportTemplate> findReportTemplatesForLoggedInUser() {
		List<ReportTemplate> ret = new ArrayList<ReportTemplate>();
		
		if (SecurityUtility.isLoggedInUserBankAdmin()) {
    		ret = reportTemplateDao.findAll();
    	} else  {
    		String userName = SecurityUtility.getPrincipal();
    		boolean isFullRecon = userDao.isUserFullRecon(userName);
    		ret = reportTemplateDao.findReportTemplates(isFullRecon);
    	}
		
		return ret;
	}
}

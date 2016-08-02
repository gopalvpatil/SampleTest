package com.westernalliancebancorp.positivepay.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * User:	Gopal Patil
 * Date:	Feb 17, 2014
 * Time:	10:04:54 PM
 */

public class ReportDto {
	private Boolean isFavorite;
	private Long reportId;
	private Long deleteReportId;
	private String reportName;
	private Long templateId;
	private String templateName;
	private String templateFileName;
	private String reportType;
	private String outputFormat;
	private String dateCreated;
	private String asOfDate;
	private Boolean asOfDateIsSymbolic = false;
	private String asOfDateSymbolicValue;
	private String packageName;
	private List<Long> bankIds = new ArrayList<Long>();
	private List<Long> companyIds = new ArrayList<Long>();	
	private List<Long> accountIds = new ArrayList<Long>();

	public Boolean getIsFavorite() {
		return isFavorite;
	}
	
	public void setIsFavorite(Boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public Long getDeleteReportId() {
		return deleteReportId;
	}

	public void setDeleteReportId(Long deleteReportId) {
		this.deleteReportId = deleteReportId;
	}

	public String getReportName() {
		return reportName;
	}
	
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public String getTemplateName() {
		return templateName;
	}
	
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public String getTemplateFileName() {
		return templateFileName;
	}
	
	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}
	
	public String getReportType() {
		return reportType;
	}
	
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	
	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getAsOfDate() {
		return asOfDate;
	}
	
	public void setAsOfDate(String asOfDate) {
		this.asOfDate = asOfDate;
	}
	
	public Boolean getAsOfDateIsSymbolic() {
		return asOfDateIsSymbolic;
	}

	public void setAsOfDateIsSymbolic(Boolean asOfDateIsSymbolic) {
		this.asOfDateIsSymbolic = asOfDateIsSymbolic;
	}

	public String getAsOfDateSymbolicValue() {
		return asOfDateSymbolicValue;
	}

	public void setAsOfDateSymbolicValue(String asOfDateSymbolicValue) {
		this.asOfDateSymbolicValue = asOfDateSymbolicValue;
	}

	public List<Long> getBankIds() {
		return bankIds;
	}
	
	public void setBankIds(List<Long> bankIds) {
		this.bankIds = bankIds;
	}
	
	public List<Long> getCompanyIds() {
		return companyIds;
	}
	
	public void setCompanyIds(List<Long> companyIds) {
		this.companyIds = companyIds;
	}
	
	public List<Long> getAccountIds() {
		return accountIds;
	}
	
	public void setAccountIds(List<Long> accountIds) {
		this.accountIds = accountIds;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}

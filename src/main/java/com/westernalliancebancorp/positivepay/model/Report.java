package com.westernalliancebancorp.positivepay.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

@javax.persistence.Table(name = "REPORT")
@EntityListeners(AuditListener.class)
@Entity
public class Report implements Auditable {
    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "AS_OF_DATE", nullable = true)
    @Type(type="date")
    private Date asOfDate;

    @javax.persistence.Column(name = "AS_OF_IS_SYMBOLIC", nullable = false)
    private Boolean asOfDateIsSymbolic;

    @javax.persistence.Column(name = "AS_OF_DATE_SYMBOLIC_VALUE", length = 50, nullable = true)
    private String asOfDateSymbolicValue;

    @javax.persistence.Column(name = "OUTPUT_FORMAT", length = 50, nullable = false)
    private String outputFormat;
    
    @OneToOne
    @JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false)
    private ReportTemplate reportTemplate;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "USER_DETAIL_ID", nullable = false)
    private UserDetail userDetail;
    
    @JsonIgnore
    @OneToMany(mappedBy = "report", targetEntity = ReportBankCompanyAccountParameter.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportBankCompanyAccountParameter> reportBankCompanyAccountParameters = new HashSet<ReportBankCompanyAccountParameter>();

    @JsonIgnore
    @OneToMany(mappedBy = "report", targetEntity = ReportParameterOptionValue.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReportParameterOptionValue> reportParameterOptionValues = new HashSet<ReportParameterOptionValue>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
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

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public ReportTemplate getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}

	public Set<ReportBankCompanyAccountParameter> getReportBankCompanyAccountParameters() {
		return reportBankCompanyAccountParameters;
	}

	public void setReportBankCompanyAccountParameters(
			Set<ReportBankCompanyAccountParameter> reportBankCompanyAccountParameters) {
		this.reportBankCompanyAccountParameters = reportBankCompanyAccountParameters;
	}

	public Set<ReportParameterOptionValue> getReportParameterOptionValues() {
		return reportParameterOptionValues;
	}

	public void setReportParameterOptionValues(
			Set<ReportParameterOptionValue> reportParameterOptionValues) {
		this.reportParameterOptionValues = reportParameterOptionValues;
	}
	
	@Embedded
    private AuditInfo auditInfo = new AuditInfo();
    
	public AuditInfo getAuditInfo() {
        return auditInfo;
    }

	public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}
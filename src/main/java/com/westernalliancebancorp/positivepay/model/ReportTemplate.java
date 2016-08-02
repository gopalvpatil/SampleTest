package com.westernalliancebancorp.positivepay.model;

import javax.persistence.*;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a report template
 * @author Boris Tubak
 */
@javax.persistence.Table(name = "REPORT_TEMPLATE")
@EntityListeners(AuditListener.class)
@Entity
public class ReportTemplate implements Auditable {
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "TEMPLATE_FILENAME", length = 50, nullable = true)
    private String templateFileName;

    @Column(name = "IS_INTERNAL", nullable = false)
    private Boolean isInternal;

    @Column(name = "IS_EXTERNAL", nullable = false)
    private Boolean isExternal;

    @OneToOne
    @JoinColumn(name = "REPORT_TEMPLATE_TYPE_ID", nullable = false)
    private ReportTemplateType reportTemplateType;

    @Column(name = "ACCOUNT_SERVICE_OPTION", length = 1, nullable = true)
    private String accountServiceOption;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "reportTemplates")
    public Set<ReportParameterOption> reportParameterOptions = new HashSet<ReportParameterOption>();

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

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public Boolean getIsInternal() {
		return isInternal;
	}

	public void setIsInternal(Boolean isInternal) {
		this.isInternal = isInternal;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}

	public ReportTemplateType getReportTemplateType() {
		return reportTemplateType;
	}

	public void setReportTemplateType(ReportTemplateType reportTemplateType) {
		this.reportTemplateType = reportTemplateType;
	}

    public Boolean getInternal() {
        return isInternal;
    }

    public void setInternal(Boolean internal) {
        isInternal = internal;
    }

    public Boolean getExternal() {
        return isExternal;
    }

    public void setExternal(Boolean external) {
        isExternal = external;
    }

    public String getAccountServiceOption() {
        return accountServiceOption;
    }

    public void setAccountServiceOption(String accountServiceOption) {
        this.accountServiceOption = accountServiceOption;
    }

    public Set<ReportParameterOption> getReportParameterOptions() {
		return reportParameterOptions;
	}

	public void setReportParameterOptions(
			Set<ReportParameterOption> reportParameterOptions) {
		this.reportParameterOptions = reportParameterOptions;
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
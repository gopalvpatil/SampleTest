package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/11/14
 * Time: 10:19 AM
 */
@javax.persistence.Table(name = "JOB_FILTER_CRITERIA")
@EntityListeners(AuditListener.class)
@Entity
public class JobCriteriaData implements Serializable, Auditable {
	private static final long serialVersionUID = -1721333157717897316L;

	public enum CRITERIA_NAME {
        BANK {
            public String toString() {
                return "Bank";
            }
        },
        COMPANY {
            public String toString() {
                return "Company";
            }
        },
        ACCOUNT {
            public String toString() {
                return "Account";
            }
        }
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "JOB_STEP_ID", nullable = false)
    private JobStep jobStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "CRITERIA_NAME", length = 50, nullable = false)
    private CRITERIA_NAME criteriaName;

    @Column(name = "VALUE", length = 255, nullable = true)
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public JobStep getJobStep() {
		return jobStep;
	}

	public void setJobStep(JobStep jobStep) {
		this.jobStep = jobStep;
	}

	public CRITERIA_NAME getCriteriaName() {
        return criteriaName;
    }

    public void setCriteriaName(CRITERIA_NAME criteriaName) {
        this.criteriaName = criteriaName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

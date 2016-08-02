package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * This class represents status of the task 
 *  
 *
 * @author Gopal Patil
 * 
 */
@javax.persistence.Table(name = "JOB_STATUS_TYPE")
@EntityListeners(AuditListener.class)
@Entity
public class JobStatusType implements Serializable, Auditable {
	private static final long serialVersionUID = -2130221309017774666L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, unique = true, nullable = false)
	private String name;
	
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive;
    
	@Column(name = "STATUS_CODE", length = 15, nullable = false)
	private String statusCode;
	
    @OneToMany(mappedBy = "jobStatusType", fetch = FetchType.LAZY, targetEntity = JobHistory.class)
    private Set<JobHistory> jobHistory = new HashSet<JobHistory>(0);
    
    @OneToMany(mappedBy = "jobStatusType", fetch = FetchType.LAZY, targetEntity = JobStepHistory.class, cascade = CascadeType.ALL)
    private Set<JobStepHistory> jobStepHistory = new HashSet<JobStepHistory>();
    
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Set<JobHistory> getJobHistory() {
		return jobHistory;
	}

	public void setJobHistory(Set<JobHistory> jobHistory) {
		this.jobHistory = jobHistory;
	}

	public Set<JobStepHistory> getJobStepHistory() {
		return jobStepHistory;
	}

	public void setJobStepHistory(Set<JobStepHistory> jobStepHistory) {
		this.jobStepHistory = jobStepHistory;
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

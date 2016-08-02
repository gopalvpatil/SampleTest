package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * job type Class represents File, Payment 
 * processing types for job
 *
 * @author Gopal Patil
 * 
 */
@javax.persistence.Table(name = "JOB_TYPE")
@EntityListeners(AuditListener.class)
@Entity
public class JobType implements Serializable, Auditable {
	
	private static final long serialVersionUID = -2897106631568217943L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, unique = true, nullable = false)
	private String name;
	
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive;
    
    @OneToMany(mappedBy = "jobType", fetch = FetchType.LAZY, targetEntity = JobActionType.class)
    @JsonManagedReference
    private Set<JobActionType> jobActionTypes = new HashSet<JobActionType>();
	
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

	public Set<JobActionType> getJobActionTypes() {
		return jobActionTypes;
	}

	public void setJobActionTypes(Set<JobActionType> jobActionTypes) {
		this.jobActionTypes = jobActionTypes;
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

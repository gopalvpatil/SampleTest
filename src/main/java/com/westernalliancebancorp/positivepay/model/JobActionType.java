package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * User:	Gopal Patil
 * Date:	Apr 9, 2014
 * Time:	8:36:07 PM
 */
@javax.persistence.Table(name = "JOB_ACTION_TYPE")
@EntityListeners(AuditListener.class)
@Entity
public class JobActionType implements Serializable, Auditable {	
	private static final long serialVersionUID = 1296786995545943416L;
	
	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	
	@Column(name = "DESCRIPTION", length = 255, nullable = true)
	private String description;
	
	@Column(name = "SPRING_BEAN_NAME", length = 255, nullable = true)
	private String springBeanName;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_TYPE_ID", nullable = false)
	@JsonBackReference
    private JobType jobType;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSpringBeanName() {
		return springBeanName;
	}

	public void setSpringBeanName(String springBeanName) {
		this.springBeanName = springBeanName;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
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

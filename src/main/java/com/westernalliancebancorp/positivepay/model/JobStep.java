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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * User:	Gopal Patil
 * Date:	Mar 19, 2014
 * Time:	3:53:33 PM
 */
@javax.persistence.Table(name = "JOB_STEP")
@EntityListeners(AuditListener.class)
@Entity
public class JobStep implements Serializable, Auditable{
	
	private static final long serialVersionUID = -49454961324645904L;
	
	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	
	@Column(name = "DESCRIPTION", length = 255, nullable = true)
	private String description;
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ACTION_TYPE_ID", nullable = true)
    private JobActionType jobActionType;
	
    @Column(name = "SEQUENCE", nullable = false)
	private Byte sequence;
	
    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "JOB_ID", nullable = false)
    private Job job; 
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_TYPE_ID", nullable = false)
    private JobType jobType;	
    
    @OneToMany(mappedBy = "jobStep", fetch = FetchType.LAZY, targetEntity = JobCriteriaData.class, cascade = CascadeType.ALL)
    private Set<JobCriteriaData> jobCriteriaData = new HashSet<JobCriteriaData>(0);
    
    @OneToMany(mappedBy = "jobStep", fetch = FetchType.LAZY, targetEntity = JobStepHistory.class, cascade = CascadeType.ALL)
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Byte getSequence() {
		return sequence;
	}

	public void setSequence(Byte sequence) {
		this.sequence = sequence;
	}

	public JobType getJobType() {
		return jobType;
	}

	public void setJobType(JobType jobType) {
		this.jobType = jobType;
	}

	public JobActionType getJobActionType() {
		return jobActionType;
	}

	public void setJobActionType(JobActionType jobActionType) {
		this.jobActionType = jobActionType;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public Set<JobCriteriaData> getJobCriteriaData() {
		return jobCriteriaData;
	}

	public void setJobCriteriaData(Set<JobCriteriaData> jobCriteriaData) {
		this.jobCriteriaData = jobCriteriaData;
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

package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Exception Status Model
 * @author Anand Kumar
 */
@javax.persistence.Table(name = "JOB_STEP_HISTORY")
@EntityListeners(AuditListener.class)
@Entity
public class JobStepHistory implements Serializable, Auditable{
	
	private static final long serialVersionUID = -49454961324645904L;
	
	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "EXECUTING_ON_MACHINE", nullable = false)
	private String executingOnMachine;
	@Column(name = "ACTUAL_START_TIME", nullable = false)
	private Date actualStartTime;
	@Column(name = "ACTUAL_END_TIME", nullable = true)
	private Date actualEndTime;
	@Column(name = "NUMBER_ITEMS_PROCESSED", nullable = false)
	private Long numberItemsProcessed;
	@Column(name = "NUMBER_OF_ERRORS", nullable = false)
	private Long numberOfErrors;
	@Column(name = "COMMENTS", nullable = true)
	private String comments;	
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID", nullable = false)
    private Job job;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_STATUS_TYPE_ID", nullable = false)
    private JobStatusType jobStatusType;	
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_STEP_ID", nullable = false)
    private JobStep jobStep;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExecutingOnMachine() {
		return executingOnMachine;
	}

	public void setExecutingOnMachine(String executingOnMachine) {
		this.executingOnMachine = executingOnMachine;
	}

	public Date getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public Date getActualEndTime() {
		return actualEndTime;
	}

	public void setActualEndTime(Date actualEndTime) {
		this.actualEndTime = actualEndTime;
	}

	public Long getNumberItemsProcessed() {
		return numberItemsProcessed;
	}

	public void setNumberItemsProcessed(Long numberItemsProcessed) {
		this.numberItemsProcessed = numberItemsProcessed;
	}

	public Long getNumberOfErrors() {
		return numberOfErrors;
	}

	public void setNumberOfErrors(Long numberOfErrors) {
		this.numberOfErrors = numberOfErrors;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}

	public JobStatusType getJobStatusType() {
		return jobStatusType;
	}

	public void setJobStatusType(JobStatusType jobStatusType) {
		this.jobStatusType = jobStatusType;
	}

	public JobStep getJobStep() {
		return jobStep;
	}

	public void setJobStep(JobStep jobStep) {
		this.jobStep = jobStep;
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

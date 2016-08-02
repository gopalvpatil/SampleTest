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
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * This class is to maintain Job History 
 * which will save job last run time, next run time, job status etc.
 * @author Gopal Patil
 * 
 */
@javax.persistence.Table(name = "JOB_HISTORY", uniqueConstraints = {@UniqueConstraint(columnNames = { "JOB_ID", "ACTUAL_START_TIME"})})
@EntityListeners(AuditListener.class)
@Entity
public class JobHistory implements Serializable, Auditable {
	private static final long serialVersionUID = -1799508442144442966L;
	
	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;  
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "JOB_ID", nullable = false)
    private Job job;    
    
    @Column(name = "ACTUAL_START_TIME", nullable = false)
    private Date actualStartTime;

    @Column(name = "ACTUAL_END_TIME", nullable = true)
    private Date actualEndTime;

    @Column(name = "SCHEDULED_START_DATE", nullable = false)
    private Date scheduledStartDate;

    @Column(name = "EXECUTING_ON_MACHINE", length = 255, unique = false, nullable = false)
    private String executingOnMachine;

	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_STATUS_TYPE_ID", nullable = false)
    private JobStatusType jobStatusType;	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
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

	public Date getScheduledStartDate() {
		return scheduledStartDate;
	}

	public void setScheduledStartDate(Date scheduledStartDate) {
		this.scheduledStartDate = scheduledStartDate;
	}

	public String getExecutingOnMachine() {
		return executingOnMachine;
	}

	public void setExecutingOnMachine(String executingOnMachine) {
		this.executingOnMachine = executingOnMachine;
	}

	public JobStatusType getJobStatusType() {
		return jobStatusType;
	}

	public void setJobStatusType(JobStatusType jobStatusType) {
		this.jobStatusType = jobStatusType;
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

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

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * This class is for Execution Locker for particular job
 * This will ensure that only one machine is executing particular job
 * 
 * @author Giridhar Duggirala
 */
@javax.persistence.Table(name = "JOB_EXECUTION_LOCK", uniqueConstraints = {@UniqueConstraint(columnNames = { "JOB_ID" })})
@EntityListeners(AuditListener.class)
@Entity
public class JobExecutionLocker implements Serializable, Comparable<JobExecutionLocker>, Auditable {
    private static final long serialVersionUID = -1917763313975690440L;

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID", nullable = false, unique = true)
    private Job job;  

    @Column(name = "SCHEDULED_START_DATE_TIME", nullable = false)
    private Date scheduledStartTime;

    @Column(name = "ACTUAL_START_TIME", nullable = false)
    private Date actualStartTime;

    @Column(name = "ACTUAL_END_TIME", nullable = true)
    private Date endTime;

    @Column(name = "IS_RUNNING", nullable = false)
    private boolean isRunning;

    @Column(name = "EXECUTING_ON_MACHINE", length = 255, unique = false, nullable = false)
    private String executingOnMachine;

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

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(Date scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getExecutingOnMachine() {
        return executingOnMachine;
    }

    public void setExecutingOnMachine(String executingOnMachine) {
        this.executingOnMachine = executingOnMachine;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setJobEndTime(Date jobEndTime) {
        this.endTime = jobEndTime;
    }

	@Embedded
    private AuditInfo auditInfo = new AuditInfo();
    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
    
    @Override
    public int compareTo(JobExecutionLocker o) {
        //Descending Order
        return o.getScheduledStartTime().compareTo(this.getScheduledStartTime());
    }
}

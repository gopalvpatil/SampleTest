package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.UniqueConstraint;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * This class is to create job  
 * for one-time or recurring types
 * for specific job type e.g. MakeStale, Load, FileManagement etc
 * for multiple job steps configurations
 * 
 *
 * @author Gopal Patil
 * 
 */
@javax.persistence.Table(name = "JOB", uniqueConstraints = {@UniqueConstraint(columnNames = { "NAME", "CRON_EXPRESSION", "CREATED_BY" })})
@EntityListeners(AuditListener.class)
@Entity
public class Job implements Serializable, Auditable {
	private static final long serialVersionUID = -1917763313975690440L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    
	@Column(name = "NAME", unique = true, length = 50, nullable = false)
	private String name;
	
	@Column(name = "DESCRIPTION", length = 255, nullable = false)
	private String description;
	
	@Column(name = "FREQUENCY", length = 255, nullable = true)
	private String frequency;	

    @Column(name = "START_DATE", nullable = false)
    private Date startDate;  
	
    @Column(name = "START_TIME", nullable = true)
    private String startTime; 
    
    @Column(name = "END_DATE", nullable = false)
    private Date endDate;
    
    @Column(name = "END_TIME", nullable = true)
    private String endTime;

    @Column(name = "NEXT_RUN_DATE", nullable = true)
    private Date nextRunDate;

    @Column(name = "LAST_RUN_DATE", nullable = true)
    private Date lastRunDate;

    @Column(name = "IS_INDEFINITE", nullable = false)
    private boolean isIndefinite;
    
    @Column(name = "IS_WEEKLY", nullable = false)
    private boolean isWeekly;
    
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive;
    
    @Column(name = "RUN_DAY", length = 255, nullable = true)
    private String runDay;
    
    @Column(name = "TIME_ZONE", length = 50, nullable = true)
    private String timezone;
    
    @Column(name = "INTERVAL_TIME", nullable = true)
    private String intervalTime; 

    @Column(name = "CRON_EXPRESSION", length = 255, nullable = true)
    private String cronExpression;
	
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, targetEntity = JobHistory.class, cascade = CascadeType.ALL)
    private Set<JobHistory> jobHistory = new HashSet<JobHistory>();
    
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, targetEntity = JobStep.class, cascade = CascadeType.ALL)
    private Set<JobStep> jobStep = new HashSet<JobStep>();
    
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY, targetEntity = JobStepHistory.class, cascade = CascadeType.ALL)
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

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Date getNextRunDate() {
		return nextRunDate;
	}

	public void setNextRunDate(Date nextRunDate) {
		this.nextRunDate = nextRunDate;
	}

	public Date getLastRunDate() {
		return lastRunDate;
	}

	public void setLastRunDate(Date lastRunDate) {
		this.lastRunDate = lastRunDate;
	}

	public boolean isIndefinite() {
		return isIndefinite;
	}

	public void setIndefinite(boolean isIndefinite) {
		this.isIndefinite = isIndefinite;
	}

	public boolean isWeekly() {
		return isWeekly;
	}

	public void setWeekly(boolean isWeekly) {
		this.isWeekly = isWeekly;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getRunDay() {
		return runDay;
	}

	public void setRunDay(String runDay) {
		this.runDay = runDay;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(String intervalTime) {
		this.intervalTime = intervalTime;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public Set<JobHistory> getJobHistory() {
		return jobHistory;
	}

	public void setJobHistory(Set<JobHistory> jobHistory) {
		this.jobHistory = jobHistory;
	}

	public Set<JobStep> getJobStep() {
		return jobStep;
	}

	public void setJobStep(Set<JobStep> jobStep) {
		this.jobStep = jobStep;
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

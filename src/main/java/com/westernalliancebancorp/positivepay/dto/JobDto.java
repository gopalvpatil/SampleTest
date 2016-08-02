package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;



/**
 * @author Gopal Patil
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class JobDto implements Serializable
{	

	private static final long serialVersionUID = 1L;

	private Long jobId;

	private String jobName;

	private String jobDescription;

	private String jobFrequency;	

    private String jobStartDate;  

    private String jobEndDate;    
    
    private String jobStartDateTime;  
    
    private String jobEndDateTime;  
    
    private String jobActualStartTime;  
    
    private String jobActualEndTime;

    private boolean indefinitely;

    private boolean weekly;

    private boolean active;
    
    private String jobRunDay;
    
    private String jobRunTime;
    
    private String jobEndRunTime;  
    
    private String timezone;
    
    private String intervalTime; 

	private String jobStepName;

	private String jobStepDescription;
	
	private Long jobActionTypeId;
	
	private String jobBankCriteria;

	private String jobCustomerCriteria;

	private String jobAccountCriteria;
	
    private Long thresholdTime;

    private Long jobTypeId;
    
    private Long jobHistoryId;
    
	private String jobLastRunDate;	

	private String jobNextRunDate;

    private String jobStatusType;

	private String createdBy;
    
    private String dateCreated;
    
    private String jobActionTypeName;
    
    private boolean savePage;    
	
	private String[] selectedIds;
	
	private long jobStepId;
	
	private boolean editMode;
	
	private String olderStartDateTime;
	
	private String cronExpression;
    
	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public String getJobStartDate() {
		return jobStartDate;
	}

	public void setJobStartDate(String jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	public String getJobEndDate() {
		return jobEndDate;
	}

	public void setJobEndDate(String jobEndDate) {
		this.jobEndDate = jobEndDate;
	}

	public String getJobStartDateTime() {
		return jobStartDateTime;
	}

	public void setJobStartDateTime(String jobStartDateTime) {
		this.jobStartDateTime = jobStartDateTime;
	}

	public String getJobEndDateTime() {
		return jobEndDateTime;
	}

	public void setJobEndDateTime(String jobEndDateTime) {
		this.jobEndDateTime = jobEndDateTime;
	}

	public String getJobActualStartTime() {
		return jobActualStartTime;
	}

	public void setJobActualStartTime(String jobActualStartTime) {
		this.jobActualStartTime = jobActualStartTime;
	}

	public String getJobActualEndTime() {
		return jobActualEndTime;
	}

	public void setJobActualEndTime(String jobActualEndTime) {
		this.jobActualEndTime = jobActualEndTime;
	}

	public boolean isIndefinitely() {
		return indefinitely;
	}

	public void setIndefinitely(boolean indefinitely) {
		this.indefinitely = indefinitely;
	}

	public boolean isWeekly() {
		return weekly;
	}

	public void setWeekly(boolean weekly) {
		this.weekly = weekly;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getJobRunDay() {
		return jobRunDay;
	}

	public void setJobRunDay(String jobRunDay) {
		this.jobRunDay = jobRunDay;
	}

	public String getJobRunTime() {
		return jobRunTime;
	}

	public void setJobRunTime(String jobRunTime) {
		this.jobRunTime = jobRunTime;
	}

	public String getJobEndRunTime() {
		return jobEndRunTime;
	}

	public void setJobEndRunTime(String jobEndRunTime) {
		this.jobEndRunTime = jobEndRunTime;
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

	public String getJobStepName() {
		return jobStepName;
	}

	public void setJobStepName(String jobStepName) {
		this.jobStepName = jobStepName;
	}

	public String getJobStepDescription() {
		return jobStepDescription;
	}

	public void setJobStepDescription(String jobStepDescription) {
		this.jobStepDescription = jobStepDescription;
	}

	public Long getJobActionTypeId() {
		return jobActionTypeId;
	}

	public void setJobActionTypeId(Long jobActionType) {
		this.jobActionTypeId = jobActionType;
	}

	public String getJobBankCriteria() {
		return jobBankCriteria;
	}

	public void setJobBankCriteria(String jobBankCriteria) {
		this.jobBankCriteria = jobBankCriteria;
	}

	public String getJobCustomerCriteria() {
		return jobCustomerCriteria;
	}

	public void setJobCustomerCriteria(String jobCustomerCriteria) {
		this.jobCustomerCriteria = jobCustomerCriteria;
	}

	public String getJobAccountCriteria() {
		return jobAccountCriteria;
	}

	public void setJobAccountCriteria(String jobAccountCriteria) {
		this.jobAccountCriteria = jobAccountCriteria;
	}

	public Long getThresholdTime() {
		return thresholdTime;
	}

	public void setThresholdTime(Long thresholdTime) {
		this.thresholdTime = thresholdTime;
	}

	public Long getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(Long jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public Long getJobHistoryId() {
		return jobHistoryId;
	}

	public void setJobHistoryId(Long jobHistoryId) {
		this.jobHistoryId = jobHistoryId;
	}

	public String getJobLastRunDate() {
		return jobLastRunDate;
	}

	public void setJobLastRunDate(String jobLastRunDate) {
		this.jobLastRunDate = jobLastRunDate;
	}

	public String getJobNextRunDate() {
		return jobNextRunDate;
	}

	public void setJobNextRunDate(String jobNextRunDate) {
		this.jobNextRunDate = jobNextRunDate;
	}

	public String getJobStatusType() {
		return jobStatusType;
	}

	public void setJobStatusType(String jobStatusType) {
		this.jobStatusType = jobStatusType;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public boolean isSavePage() {
		return savePage;
	}

	public void setSavePage(boolean savePage) {
		this.savePage = savePage;
	}

    public String[] getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String[] selectedIds) {
        this.selectedIds = selectedIds;
    }

	public long getJobStepId() {
		return jobStepId;
	}

	public void setJobStepId(long jobStepId) {
		this.jobStepId = jobStepId;
	}

	public String getJobActionTypeName() {
		return jobActionTypeName;
	}

	public void setJobActionTypeName(String jobActionTypeName) {
		this.jobActionTypeName = jobActionTypeName;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String getOlderStartDateTime() {
		return olderStartDateTime;
	}

	public void setOlderStartDateTime(String olderStartDateTime) {
		this.olderStartDateTime = olderStartDateTime;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	@Override
    public String toString() {
        return "JobDto{" +
                "jobId=" + jobId +
                ", jobName='" + jobName + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", jobFrequency='" + jobFrequency + '\'' +
                ", jobStartDate='" + jobStartDate + '\'' +
                ", jobEndDate='" + jobEndDate + '\'' +
                ", jobStartDateTime='" + jobStartDateTime + '\'' +
                ", jobEndDateTime='" + jobEndDateTime + '\'' +
                ", jobActualStartTime='" + jobActualStartTime + '\'' +
                ", jobActualEndTime='" + jobActualEndTime + '\'' +
                ", indefinitely=" + indefinitely +
                ", weekly=" + weekly +
                ", active=" + active +
                ", jobRunDay='" + jobRunDay + '\'' +
                ", jobRunTime='" + jobRunTime + '\'' +
                ", jobEndRunTime='" + jobEndRunTime + '\'' +
                ", timezone='" + timezone + '\'' +
                ", intervalTime='" + intervalTime + '\'' +
                ", jobStepName='" + jobStepName + '\'' +
                ", jobStepDescription='" + jobStepDescription + '\'' +
                ", jobActionTypeId=" + jobActionTypeId +
                ", jobBankCriteria='" + jobBankCriteria + '\'' +
                ", jobCustomerCriteria='" + jobCustomerCriteria + '\'' +
                ", jobAccountCriteria='" + jobAccountCriteria + '\'' +
                ", thresholdTime=" + thresholdTime +
                ", jobTypeId=" + jobTypeId +
                ", jobHistoryId=" + jobHistoryId +
                ", jobLastRunDate='" + jobLastRunDate + '\'' +
                ", jobNextRunDate='" + jobNextRunDate + '\'' +
                ", jobStatusType='" + jobStatusType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", jobActionTypeName='" + jobActionTypeName + '\'' +
                ", savePage=" + savePage +
                ", selectedIds=" + Arrays.toString(selectedIds) +
                ", jobStepId=" + jobStepId +
                '}';
    }
}

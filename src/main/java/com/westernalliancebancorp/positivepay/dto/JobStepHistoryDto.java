package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

/**
 * @author Gopal Patil
 *
 */
public class JobStepHistoryDto implements Serializable {

	private static final long serialVersionUID = 6131326196936164741L;	
	private Long jobId;
	private Long jobStepId;
	private String jobStepName;
	private String jobType;
	private String jobStepFilename;
	private String jobStepStatus;
	private Long jobStepNumOfItemsProcessed;
	private Long jobStepNumOfErrors;
	private Long jobStepNumOfFilesProcessed;
	private Long jobStepNumOfFilesFailed;
	private String jobStepActualStartTime;
	private String jobStepActualEndTime;
	private String jobActualStartTime;
	private String jobActualEndTime;
	private String comments;
	private String jobTimezone;
	private boolean showErrorLink;
	
	public Long getJobId() {
		return jobId;
	}
	
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}
	
	public Long getJobStepId() {
		return jobStepId;
	}
	
	public void setJobStepId(Long jobStepId) {
		this.jobStepId = jobStepId;
	}
	
	public String getJobStepName() {
		return jobStepName;
	}

	public void setJobStepName(String jobStepName) {
		this.jobStepName = jobStepName;
	}

	public String getJobType() {
		return jobType;
	}
	
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public String getJobStepFilename() {
		return jobStepFilename;
	}
	
	public void setJobStepFilename(String jobStepFilename) {
		this.jobStepFilename = jobStepFilename;
	}
	
	public String getJobStepStatus() {
		return jobStepStatus;
	}
	
	public void setJobStepStatus(String jobStepStatus) {
		this.jobStepStatus = jobStepStatus;
	}
	
	public Long getJobStepNumOfItemsProcessed() {
		return jobStepNumOfItemsProcessed;
	}
	
	public void setJobStepNumOfItemsProcessed(Long jobStepNumOfItemsProcessed) {
		this.jobStepNumOfItemsProcessed = jobStepNumOfItemsProcessed;
	}
	
	public Long getJobStepNumOfErrors() {
		return jobStepNumOfErrors;
	}
	
	public void setJobStepNumOfErrors(Long jobStepNumOfErrors) {
		this.jobStepNumOfErrors = jobStepNumOfErrors;
	}
	
	public Long getJobStepNumOfFilesProcessed() {
		return jobStepNumOfFilesProcessed;
	}

	public void setJobStepNumOfFilesProcessed(Long jobStepNumOfFilesProcessed) {
		this.jobStepNumOfFilesProcessed = jobStepNumOfFilesProcessed;
	}

	public Long getJobStepNumOfFilesFailed() {
		return jobStepNumOfFilesFailed;
	}

	public void setJobStepNumOfFilesFailed(Long jobStepNumOfFilesFailed) {
		this.jobStepNumOfFilesFailed = jobStepNumOfFilesFailed;
	}

	public String getJobStepActualStartTime() {
		return jobStepActualStartTime;
	}
	
	public void setJobStepActualStartTime(String jobStepActualStartTime) {
		this.jobStepActualStartTime = jobStepActualStartTime;
	}
	public String getJobStepActualEndTime() {
		return jobStepActualEndTime;
	}
	
	public void setJobStepActualEndTime(String jobStepActualEndTime) {
		this.jobStepActualEndTime = jobStepActualEndTime;
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
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getJobTimezone() {
		return jobTimezone;
	}

	public void setJobTimezone(String jobTimezone) {
		this.jobTimezone = jobTimezone;
	}

	public boolean isShowErrorLink() {
		return showErrorLink;
	}

	public void setShowErrorLink(boolean showErrorLink) {
		this.showErrorLink = showErrorLink;
	}
	
}

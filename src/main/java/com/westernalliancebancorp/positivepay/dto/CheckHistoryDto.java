package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.Date;

public class CheckHistoryDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Date dateTime;
	private String user;
	private String description;
	private String comment;
	private String resultingStatus;
	private String category;
	private String createdMethod;
	private long sequence;
	
	
	/**
	 * @return the sequence
	 */
	public long getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(long sequence) {
		this.sequence = sequence;
	}
	public Date getDateTime() {
		return dateTime;
	}
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getResultingStatus() {
		return resultingStatus;
	}
	public void setResultingStatus(String resultingStatus) {
		this.resultingStatus = resultingStatus;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getCreatedMethod() {
		return createdMethod;
	}
	public void setCreatedMethod(String createdMethod) {
		this.createdMethod = createdMethod;
	}
	@Override
	public String toString() {
		return "CheckHistoryDto [dateTime=" + dateTime + ", user=" + user
				+ ", description=" + description + ", comment=" + comment
				+ ", resultingStatus=" + resultingStatus + ", category="
				+ category + ", createdMethod=" + createdMethod + "]";
	}
}

package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

/**
 * @author Gopal Patil
 *
 */
public class DashboardDto implements Serializable{

	private static final long serialVersionUID = 1689416422626851178L;	
	
	private Long fileMetaDataId;
	
	private String companyName;
	
	private String userName;
	
	private String fileName;

    private String originalFileName;
	
	private String fileType;
	
	private String uploadedDate;
	
	private String status;
	
	private String itemsReceived;
	
	private String itemsLoaded;
	
	private String errorRecordsLoaded;
	
	private String companyNameSearchCriteria;
	
	private String statusSearchCriteria;
	
	private String dateRangeSearchCriteria;

	public Long getFileMetaDataId() {
		return fileMetaDataId;
	}

	public void setFileMetaDataId(Long fileMetaDataId) {
		this.fileMetaDataId = fileMetaDataId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getUploadedDate() {
		return uploadedDate;
	}

	public void setUploadedDate(String uploadedDate) {
		this.uploadedDate = uploadedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getItemsReceived() {
		return itemsReceived;
	}

	public void setItemsReceived(String itemsReceived) {
		this.itemsReceived = itemsReceived;
	}

	public String getItemsLoaded() {
		return itemsLoaded;
	}

	public void setItemsLoaded(String itemsLoaded) {
		this.itemsLoaded = itemsLoaded;
	}

	public String getErrorRecordsLoaded() {
		return errorRecordsLoaded;
	}

	public void setErrorRecordsLoaded(String errorRecordsLoaded) {
		this.errorRecordsLoaded = errorRecordsLoaded;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCompanyNameSearchCriteria() {
		return companyNameSearchCriteria;
	}

	public void setCompanyNameSearchCriteria(String companyNameSearchCriteria) {
		this.companyNameSearchCriteria = companyNameSearchCriteria;
	}

	public String getStatusSearchCriteria() {
		return statusSearchCriteria;
	}

	public void setStatusSearchCriteria(String statusSearchCriteria) {
		this.statusSearchCriteria = statusSearchCriteria;
	}

	public String getDateRangeSearchCriteria() {
		return dateRangeSearchCriteria;
	}

	public void setDateRangeSearchCriteria(String dateRangeSearchCriteria) {
		this.dateRangeSearchCriteria = dateRangeSearchCriteria;
	}

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }   
    
}

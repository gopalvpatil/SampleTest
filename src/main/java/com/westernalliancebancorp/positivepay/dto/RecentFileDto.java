package com.westernalliancebancorp.positivepay.dto;

import java.util.Date;
import java.util.List;

public class RecentFileDto {
	private Long fileMetaDataId;
	private Date uploadDate;
	private String fileName;
	private String fileUid;
	private Long noOfRecords;
	private Long itemsLoaded;
	private Long errorRecordsLoaded;
	private String companyName;
	List<String> accountNumbersInFile;
	public Long getFileMetaDataId() {
		return fileMetaDataId;
	}
	public void setFileMetaDataId(Long fileMetaDataId) {
		this.fileMetaDataId = fileMetaDataId;
	}
	public Date getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getNoOfRecords() {
		return noOfRecords;
	}
	public void setNoOfRecords(Long noOfRecords) {
		this.noOfRecords = noOfRecords;
	}
	public List<String> getAccountNumbersInFile() {
		return accountNumbersInFile;
	}
	public void setAccountNumbersInFile(List<String> accountNumbersInFile) {
		this.accountNumbersInFile = accountNumbersInFile;
	}
	public String getFileUid() {
		return fileUid;
	}
	public void setFileUid(String fileUid) {
		this.fileUid = fileUid;
	}
	public Long getItemsLoaded() {
		return itemsLoaded;
	}
	public void setItemsLoaded(Long itemsLoaded) {
		this.itemsLoaded = itemsLoaded;
	}
	public Long getErrorRecordsLoaded() {
		return errorRecordsLoaded;
	}
	public void setErrorRecordsLoaded(Long errorRecordsLoaded) {
		this.errorRecordsLoaded = errorRecordsLoaded;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	@Override
	public String toString() {
		return "RecentFileDto [fileMetaDataId=" + fileMetaDataId
				+ ", uploadDate=" + uploadDate + ", fileName=" + fileName
				+ ", fileUid=" + fileUid + ", noOfRecords=" + noOfRecords
				+ ", itemsLoaded=" + itemsLoaded + ", errorRecordsLoaded="
				+ errorRecordsLoaded + ", companyName=" + companyName
				+ ", accountNumbersInFile=" + accountNumbersInFile + "]";
	}
}

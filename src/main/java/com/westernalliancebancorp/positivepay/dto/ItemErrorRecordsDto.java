package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

/**
 * @author Gopal Patil
 *
 */
public class ItemErrorRecordsDto implements Serializable{
	private static final long serialVersionUID = 5630447148990363912L;
	
	private Long fileMetaDataId;
	
	private String fileLineNumber;
	
	private String accountNumber;
	
	private String routingNumber;
	
	private String checkNumber;
	
	private String issueCode;
	
	private String issueAmount;
	
	private String issueDate;
	
	private String payee;
	
	private String exceptionTypeName;
	
	/**
	 * Added new fields for error page : Sameer Shukla 
	 * @return
	 */
	private String traceNumber;
	
	private String amount;
	
	private String paidDate;
	
	private String stopDate;
	
	private String stopPresentedDate;
	
	private String itemType;

	public Long getFileMetaDataId() {
		return fileMetaDataId;
	}

	public void setFileMetaDataId(Long fileMetaDataId) {
		this.fileMetaDataId = fileMetaDataId;
	}

	public String getFileLineNumber() {
		return fileLineNumber;
	}

	public void setFileLineNumber(String fileLineNumber) {
		this.fileLineNumber = fileLineNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getRoutingNumber() {
		return routingNumber;
	}

	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getIssueCode() {
		return issueCode;
	}

	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}

	public String getIssueAmount() {
		return issueAmount;
	}

	public void setIssueAmount(String issueAmount) {
		this.issueAmount = issueAmount;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getExceptionTypeName() {
		return exceptionTypeName;
	}

	public void setExceptionTypeName(String exceptionTypeName) {
		this.exceptionTypeName = exceptionTypeName;
	}

	public String getTraceNumber() {
		return traceNumber;
	}

	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getStopDate() {
		return stopDate;
	}

	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}

	public String getStopPresentedDate() {
		return stopPresentedDate;
	}

	public void setStopPresentedDate(String stopPresentedDate) {
		this.stopPresentedDate = stopPresentedDate;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
}

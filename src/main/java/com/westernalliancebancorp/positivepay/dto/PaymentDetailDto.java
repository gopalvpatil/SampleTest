package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PaymentDetailDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long checkId;
	private String accountNumber;
	private String checkNumber;
	private String paymentStatus;
	private String matchStatus;
	private String exceptionType;
	private String exceptionStatus;
	private BigDecimal issuedAmount;
	private Date issuedDate;
	private BigDecimal paidAmount;
	private Date paidDate;
	private Date stopDate;
	private Date voidDate;
	private String accountName;
	private Long workflowId;
	private String itemType;
	private BigDecimal itemAmount;
	private Date itemDate;
	private String createdBy;
	private Date createdDate;
	private String bankName;
	private String itemCode;
	private String company;
	private String payee;
	private String traceNumber;
	private String createdMethod;
	
	/**
	 * @return the bankName
	 */
	public String getBankName() {
		return bankName;
	}
	/**
	 * @param bankName the bankName to set
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	/**
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	/**
	 * @return the itemAmount
	 */
	public BigDecimal getItemAmount() {
		return itemAmount;
	}
	/**
	 * @param itemAmount the itemAmount to set
	 */
	public void setItemAmount(BigDecimal itemAmount) {
		this.itemAmount = itemAmount;
	}
	/**
	 * @return the itemDate
	 */
	public Date getItemDate() {
		return itemDate;
	}
	/**
	 * @param itemDate the itemDate to set
	 */
	public void setItemDate(Date itemDate) {
		this.itemDate = itemDate;
	}
	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	/**
	 * @return the createdMethod
	 */
	public String getCreatedMethod() {
		return createdMethod;
	}
	/**
	 * @param createdMethod the createdMethod to set
	 */
	public void setCreatedMethod(String createdMethod) {
		this.createdMethod = createdMethod;
	}
	
	public Long getCheckId() {
		return checkId;
	}
	public void setCheckId(Long checkId) {
		this.checkId = checkId;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getMatchStatus() {
		return matchStatus;
	}
	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}
	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return the payee
	 */
	public String getPayee() {
		return payee;
	}
	/**
	 * @param payee the payee to set
	 */
	public void setPayee(String payee) {
		this.payee = payee;
	}
	/**
	 * @return the traceNumber
	 */
	public String getTraceNumber() {
		return traceNumber;
	}
	/**
	 * @param traceNumber the traceNumber to set
	 */
	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}
	public void setMatchStatus(String matchStatus) {
		this.matchStatus = matchStatus;
	}
	public String getExceptionType() {
		return exceptionType;
	}
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	public String getExceptionStatus() {
		return exceptionStatus;
	}
	public void setExceptionStatus(String exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}
	public BigDecimal getIssuedAmount() {
		return issuedAmount;
	}
	public void setIssuedAmount(BigDecimal issuedAmount) {
		this.issuedAmount = issuedAmount;
	}
	public Date getIssuedDate() {
		return issuedDate;
	}
	public void setIssuedDate(Date issuedDate) {
		this.issuedDate = issuedDate;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	public Date getPaidDate() {
		return paidDate;
	}
	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
	public Date getVoidDate() {
		return voidDate;
	}
	public void setVoidDate(Date voidDate) {
		this.voidDate = voidDate;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	@Override
	public String toString() {
		return "PaymentDetailDto [checkId=" + checkId + ", accountNumber="
				+ accountNumber + ", checkNumber=" + checkNumber
				+ ", paymentStatus=" + paymentStatus + ", matchStatus="
				+ matchStatus + ", exceptionType=" + exceptionType
				+ ", exceptionStatus=" + exceptionStatus + ", issuedAmount="
				+ issuedAmount + ", issuedDate=" + issuedDate + ", paidAmount="
				+ paidAmount + ", paidDate=" + paidDate + ", stopDate="
				+ stopDate + ", voidDate=" + voidDate + ", accountName="
				+ accountName + ", workflowId=" + workflowId + "]";
	}
}

package com.westernalliancebancorp.positivepay.dto;

import java.math.BigDecimal;
import java.util.Date;

public class CheckDto {
	private Long id;
	private Long referenceDataId;
	private String accountNumber;
	private String accountName;
	private String checkNumber;
	private BigDecimal issuedAmount;
	private BigDecimal paidAmount;
	private String issueCode;
	private Date issueDate;
	private Date paidDate;
	private Date stopDate;
	private Date voidDate;
	private String traceNumber;
	private String referenceNumber;
	private String bankName;
	private String bankNumber;
	private String payee;
	private Long companyId;
	private String exceptionType;
	private String exceptionDescription;
	private String exceptionStatus;
	private String matchStatus;
	private String paymentStatus;
	private String statusName;
	private boolean isExceptional;
	private Long workflowId;
	private String decision;
	private String reason;
	private String routingNumber;
	private String companyName;
    private String digest;
    private String manualEntryDate;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the referenceDataId
	 */
	public Long getReferenceDataId() {
		return referenceDataId;
	}
	/**
	 * @param referenceDataId the referenceDataId to set
	 */
	public void setReferenceDataId(Long referenceDataId) {
		this.referenceDataId = referenceDataId;
	}
	
	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	/**
	 * @param accountNumber the accountNumber to set
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	/**
	 * @return the checkNumber
	 */
	public String getCheckNumber() {
		return checkNumber;
	}
	/**
	 * @param checkNumber the checkNumber to set
	 */
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	/**
	 * @return the issuedAmount
	 */
	public BigDecimal getIssuedAmount() {
		return issuedAmount;
	}
	/**
	 * @param issuedAmount the issuedAmount to set
	 */
	public void setIssuedAmount(BigDecimal issuedAmount) {
		this.issuedAmount = issuedAmount;
	}
	/**
	 * @return the issueCode
	 */
	public String getIssueCode() {
		return issueCode;
	}
	/**
	 * @param issueCode the issueCode to set
	 */
	public void setIssueCode(String issueCode) {
		this.issueCode = issueCode;
	}
	/**
	 * @return the issueDate
	 */
	public Date getIssueDate() {
		return issueDate;
	}
	/**
	 * @param issueDate the issueDate to set
	 */
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
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
	 * @return the companyId
	 */
	public Long getCompanyId() {
		return companyId;
	}
	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	/**
	 * @return the exceptionType
	 */
	public String getExceptionType() {
		return exceptionType;
	}
	/**
	 * @param exceptionType the exceptionType to set
	 */
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	/**
	 * @return the workflowId
	 */
	public Long getWorkflowId() {
		return workflowId;
	}
	/**
	 * @param workflowId the workflowId to set
	 */
	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}
	/**
	 * @return the exceptionDescription
	 */
	public String getExceptionDescription() {
		return exceptionDescription;
	}
	/**
	 * @param exceptionDescription the exceptionDescription to set
	 */
	public void setExceptionDescription(String exceptionDescription) {
		this.exceptionDescription = exceptionDescription;
	}
	/**
	 * @return the statusName
	 */
	public String getStatusName() {
		return statusName;
	}
	/**
	 * @param statusName the statusName to set
	 */
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	/**
	 * @return the isExceptional
	 */
	public boolean isExceptional() {
		return isExceptional;
	}
	/**
	 * @param isExceptional the isExceptional to set
	 */
	public void setExceptional(boolean isExceptional) {
		this.isExceptional = isExceptional;
	}
	/**
	 * @return the decision
	 */
	public String getDecision() {
		return decision;
	}
	/**
	 * @param decision the decision to set
	 */
	public void setDecision(String decision) {
		this.decision = decision;
	}
	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * @return the routingNumber
	 */
	public String getRoutingNumber() {
		return routingNumber;
	}
	/**
	 * @param routingNumber the routingNumber to set
	 */
	public void setRoutingNumber(String routingNumber) {
		this.routingNumber = routingNumber;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the paidAmount
	 */
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	/**
	 * @param paidAmount the paidAmount to set
	 */
	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}
	/**
	 * @return the paidDate
	 */
	public Date getPaidDate() {
		return paidDate;
	}
	/**
	 * @param paidDate the paidDate to set
	 */
	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}
	/**
	 * @return the stopDate
	 */
	public Date getStopDate() {
		return stopDate;
	}
	/**
	 * @param stopDate the stopDate to set
	 */
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
	/**
	 * @return the voidDate
	 */
	public Date getVoidDate() {
		return voidDate;
	}
	/**
	 * @param voidDate the voidDate to set
	 */
	public void setVoidDate(Date voidDate) {
		this.voidDate = voidDate;
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
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
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
	 * @return the bankNumber
	 */
	public String getBankNumber() {
		return bankNumber;
	}
	/**
	 * @param bankNumber the bankNumber to set
	 */
	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}
	/**
	 * @return the exceptionStatus
	 */
	public String getExceptionStatus() {
		return exceptionStatus;
	}
	/**
	 * @param exceptionStatus the exceptionStatus to set
	 */
	public void setExceptionStatus(String exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}
	/**
	 * @return the matchStatus
	 */
	public String getMatchStatus() {
		return matchStatus;
	}
	/**
	 * @param matchStatus the matchStatus to set
	 */
	public void setMatchStatus(String matchStatus) {
		this.matchStatus = matchStatus;
	}
	/**
	 * @return the paymentStatus
	 */
	public String getPaymentStatus() {
		return paymentStatus;
	}
	/**
	 * @param paymentStatus the paymentStatus to set
	 */
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}	
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getManualEntryDate() {
		return manualEntryDate;
	}
	public void setManualEntryDate(String manualEntryDate) {
		this.manualEntryDate = manualEntryDate;
	}
	@Override
	public String toString() {
		return "CheckDto [id=" + id + ", referenceDataId=" + referenceDataId
				+ ", accountNumber=" + accountNumber + ", accountName="
				+ accountName + ", checkNumber=" + checkNumber
				+ ", issuedAmount=" + issuedAmount + ", paidAmount="
				+ paidAmount + ", issueCode=" + issueCode + ", issueDate="
				+ issueDate + ", paidDate=" + paidDate + ", stopDate="
				+ stopDate + ", voidDate=" + voidDate + ", traceNumber="
				+ traceNumber + ", referenceNumber=" + referenceNumber
				+ ", bankName=" + bankName + ", bankNumber=" + bankNumber
				+ ", payee=" + payee + ", companyId=" + companyId
				+ ", exceptionType=" + exceptionType
				+ ", exceptionDescription=" + exceptionDescription
				+ ", exceptionStatus=" + exceptionStatus + ", matchStatus="
				+ matchStatus + ", paymentStatus=" + paymentStatus
				+ ", statusName=" + statusName + ", isExceptional="
				+ isExceptional + ", workflowId=" + workflowId + ", decision="
				+ decision + ", reason=" + reason + ", routingNumber="
				+ routingNumber + ", companyName=" + companyName + ", digest="
				+ digest + ", manualEntryDate=" + manualEntryDate + "]";
	}
}

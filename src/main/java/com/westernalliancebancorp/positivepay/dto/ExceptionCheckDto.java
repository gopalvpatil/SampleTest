package com.westernalliancebancorp.positivepay.dto;

import java.math.BigDecimal;
import java.util.Date;

public class ExceptionCheckDto {
	private Long id;
	private Long exceptionCheckId;
	private String accountNumber;
	private String checkNumber;
	private BigDecimal issuedAmount;
	private String issueCode;
	private Date issueDate;
	private String payee;
	private Long companyId;
	
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
	public Long getExceptionCheckId() {
		return exceptionCheckId;
	}
	public void setExceptionCheckId(Long exceptionCheckId) {
		this.exceptionCheckId = exceptionCheckId;
	}
	/**
	 * @param companyId the companyId to set
	 */
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckDto [id=" + id + ", referenceDataId=" + exceptionCheckId
				+ ", accountNumber=" + accountNumber + ", checkNumber="
				+ checkNumber + ", checkAmount=" + issuedAmount + ", issueCode="
				+ issueCode + ", issueDate=" + issueDate + ", payee=" + payee
				+ ", companyId=" + companyId + "]";
	}
	
}

package com.westernalliancebancorp.positivepay.dto;

import java.math.BigDecimal;
import java.util.List;

public class DataCriteriaDto {
	
	private List<String> accountNumbers;
	private List<String> paymentStatusTypes;
	private List<String> paidExceptionStatus;
	private Long fromCheckNumber;
	private Long toCheckNumber;
	private String amountType;
	private BigDecimal fromAmount;
	private BigDecimal toAmount;
	private String dateType;
	private String fromDate;
	private String toDate;
	
	public List<String> getAccountNumbers() {
		return accountNumbers;
	}
	public void setAccountNumbers(List<String> accountNumbers) {
		this.accountNumbers = accountNumbers;
	}
	public List<String> getPaymentStatusTypes() {
		return paymentStatusTypes;
	}
	public void setPaymentStatusTypes(List<String> paymentStatusTypes) {
		this.paymentStatusTypes = paymentStatusTypes;
	}
	public List<String> getPaidExceptionStatus() {
		return paidExceptionStatus;
	}
	public void setPaidExceptionStatus(List<String> paidExceptionStatus) {
		this.paidExceptionStatus = paidExceptionStatus;
	}
	public Long getFromCheckNumber() {
		return fromCheckNumber;
	}
	public void setFromCheckNumber(Long fromCheckNumber) {
		this.fromCheckNumber = fromCheckNumber;
	}
	public Long getToCheckNumber() {
		return toCheckNumber;
	}
	public void setToCheckNumber(Long toCheckNumber) {
		this.toCheckNumber = toCheckNumber;
	}
	public String getAmountType() {
		return amountType;
	}
	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}
	public BigDecimal getFromAmount() {
		return fromAmount;
	}
	public void setFromAmount(BigDecimal fromAmount) {
		this.fromAmount = fromAmount;
	}
	public BigDecimal getToAmount() {
		return toAmount;
	}
	public void setToAmount(BigDecimal toAmount) {
		this.toAmount = toAmount;
	}
	public String getDateType() {
		return dateType;
	}
	public void setDateType(String dateType) {
		this.dateType = dateType;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

}

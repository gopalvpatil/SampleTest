package com.westernalliancebancorp.positivepay.dto;

import java.util.Date;


public class ExceptionalReferenceDataDto {
	
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
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	public Long getReferenceDataId() {
		return referenceDataId;
	}

	public void setReferenceDataId(Long referenceDataId) {
		this.referenceDataId = referenceDataId;
	}

	public Long getExpReferenceDataId() {
		return expReferenceDataId;
	}

	public void setExpReferenceDataId(Long expReferenceDataId) {
		this.expReferenceDataId = expReferenceDataId;
	}

	private Long referenceDataId;
	
	private Long expReferenceDataId;
	
	private String checkNumber;
	
	private String amount;
	
	private Date date;
		
	
	
	
}

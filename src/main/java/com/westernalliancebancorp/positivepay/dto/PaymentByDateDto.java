package com.westernalliancebancorp.positivepay.dto;

import java.util.Date;

/**
 * Date: 5/6/14
 * Time: 9:43 AM
 */
public class PaymentByDateDto {
	private Long accountId;
	private String accountNumber;
    private Date paymentDate;
    private double amount;
    private Long count;
    
    public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

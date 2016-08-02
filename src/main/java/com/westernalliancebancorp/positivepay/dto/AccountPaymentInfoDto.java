package com.westernalliancebancorp.positivepay.dto;

/**
 * User: gduggirala
 * Date: 3/6/14
 * Time: 7:13 PM
 */
public class AccountPaymentInfoDto {
    private Long accountId;
    private String accountNumber;
    private double totalAmount;
    private String checkStatusName;
    private Integer totalCount;
    private String checkStatusDescription;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCheckStatusName() {
        return checkStatusName;
    }

    public void setCheckStatusName(String checkStatusName) {
        this.checkStatusName = checkStatusName;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

    public String getCheckStatusDescription() {
        return checkStatusDescription;
    }

    public void setCheckStatusDescription(String checkStatusDescription) {
        this.checkStatusDescription = checkStatusDescription;
    }
}

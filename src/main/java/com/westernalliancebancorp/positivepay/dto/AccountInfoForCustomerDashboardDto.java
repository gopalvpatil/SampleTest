package com.westernalliancebancorp.positivepay.dto;

import java.util.List;

public class AccountInfoForCustomerDashboardDto {
	DecisionWindowDto decisionWindow;
	List<AccountInfoDto> accountInfoDtoList;
	public DecisionWindowDto getDecisionWindow() {
		return decisionWindow;
	}
	public void setDecisionWindow(DecisionWindowDto decisionWindow) {
		this.decisionWindow = decisionWindow;
	}
	public List<AccountInfoDto> getAccountInfoDtoList() {
		return accountInfoDtoList;
	}
	public void setAccountInfoDtoList(List<AccountInfoDto> accountInfoDtoList) {
		this.accountInfoDtoList = accountInfoDtoList;
	}
	@Override
	public String toString() {
		return "AccountInfoForCustomerDashboardDto [decisionWindow="
				+ decisionWindow + ", accountInfoDtoList=" + accountInfoDtoList
				+ "]";
	}	
}

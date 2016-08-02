package com.westernalliancebancorp.positivepay.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * @author umeshram
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FindUserDto {

	private String username;
	private Long accountNo;
	private Long bankId;
	@JsonIgnore
	private List<Long> allowedBankIds;
	private Long companyId;
	@JsonIgnore
	private List<Long> allowedCompanyIds;
	private Boolean archivedUser;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getAccountNo() {
		return accountNo;
	}
	public void setAccountNo(Long accountNo) {
		this.accountNo = accountNo;
	}
	public Long getBankId() {
		return bankId;
	}
	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Boolean getArchivedUser() {
		return archivedUser;
	}
	public void setArchivedUser(Boolean archivedUser) {
		this.archivedUser = archivedUser;
	}
	public List<Long> getAllowedBankIds() {
		return allowedBankIds;
	}
	public void setAllowedBankIds(List<Long> allowedBankIds) {
		this.allowedBankIds = allowedBankIds;
	}
	public List<Long> getAllowedCompanyIds() {
		return allowedCompanyIds;
	}
	public void setAllowedCompanyIds(List<Long> allowedCompanyIds) {
		this.allowedCompanyIds = allowedCompanyIds;
	}
}

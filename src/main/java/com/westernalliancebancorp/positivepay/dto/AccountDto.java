package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.westernalliancebancorp.positivepay.model.UserDetail;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String accountNumber;
	private String accountName;
	private Boolean active = true;
	private int staleDays;
	private Long accountServiceOption;
	private String ppDecision;
	private Long accountCycleCutOff;
	private String fileInputMethod;
	private String dataOutputMethod;
	private String reportOutputMethod;
	private boolean thirdparty;
	private String format;
	private Long companyId;

	private List<UserDto> users;
	private List<Long> selectedUserIds;

	//used on the reports page
	private Boolean selected = false;
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public int getStaleDays() {
		return staleDays;
	}

	public void setStaleDays(int staleDays) {
		this.staleDays = staleDays;
	}

	public String getFileInputMethod() {
		return fileInputMethod;
	}

	public void setFileInputMethod(String fileInputMethod) {
		this.fileInputMethod = fileInputMethod;
	}

	public String getDataOutputMethod() {
		return dataOutputMethod;
	}

	public void setDataOutputMethod(String dataOutputMethod) {
		this.dataOutputMethod = dataOutputMethod;
	}

	public String getReportOutputMethod() {
		return reportOutputMethod;
	}

	public void setReportOutputMethod(String reportOutputMethod) {
		this.reportOutputMethod = reportOutputMethod;
	}

	public boolean isThirdparty() {
		return thirdparty;
	}

	public void setThirdparty(boolean thirdparty) {
		this.thirdparty = thirdparty;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}
	
	public List<Long> getSelectedUserIds() {
		return selectedUserIds;
	}

	public void setSelectedUserIds(List<Long> selectedUserIds) {
		this.selectedUserIds = selectedUserIds;
	}

	public Long getAccountServiceOption() {
		return accountServiceOption;
	}

	public void setAccountServiceOption(Long accountServiceOption) {
		this.accountServiceOption = accountServiceOption;
	}

	public String getPpDecision() {
		return ppDecision;
	}

	public void setPpDecision(String ppDecision) {
		this.ppDecision = ppDecision;
	}

	public Long getAccountCycleCutOff() {
		return accountCycleCutOff;
	}

	public void setAccountCycleCutOff(Long accountCycleCutOff) {
		this.accountCycleCutOff = accountCycleCutOff;
	}

}

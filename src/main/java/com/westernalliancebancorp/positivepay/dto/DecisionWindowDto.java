package com.westernalliancebancorp.positivepay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DecisionWindowDto {

	private String bankName;

	private Long bankId;

	private String companyName;

	private Long companyId;

	private String start;

	private String end;

	private String timezone;

	private Long[] company;
	
	private String startHour;

	private String startMin;
	
	private String startMeridiem;
	
	private String endHour;
	
	private String endMin;
	
	private String endMeridiem;
	
	private String date;
	
	private boolean outSideWindow;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getStartMin() {
		return startMin;
	}

	public void setStartMin(String startMin) {
		this.startMin = startMin;
	}

	public String getStartMeridiem() {
		return startMeridiem;
	}

	public void setStartMeridiem(String startMeridiem) {
		this.startMeridiem = startMeridiem;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getEndMin() {
		return endMin;
	}

	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}

	public String getEndMeridiem() {
		return endMeridiem;
	}

	public void setEndMeridiem(String endMeridiem) {
		this.endMeridiem = endMeridiem;
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

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public void setCompany(Long[] company) {
		this.company = company;
	}

	public Long[] getCompany() {
		return company;
	}

	public boolean isOutSideWindow() {
		return outSideWindow;
	}

	public void setOutSideWindow(boolean outSideWindow) {
		this.outSideWindow = outSideWindow;
	}
}

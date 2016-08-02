package com.westernalliancebancorp.positivepay.dto;

import java.util.Date;

public class ReportOptionalParameterDto {
	private String name;
	private String displayName;
	private String type;
	private String operator;
	private String valueChar;
	private String valueDateFrom;
	private String valueDateTo;
	private boolean isValueDateSymbolic;
	private String valueDateFromSymbolicValue;
	private String valueDateToSymbolicValue;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getValueChar() {
		return valueChar;
	}
	
	public void setValueChar(String valueChar) {
		this.valueChar = valueChar;
	}
	
	public String getValueDateFrom() {
		return valueDateFrom;
	}
	
	public void setValueDateFrom(String valueDateFrom) {
		this.valueDateFrom = valueDateFrom;
	}
	
	public String getValueDateTo() {
		return valueDateTo;
	}
	
	public void setValueDateTo(String valueDateTo) {
		this.valueDateTo = valueDateTo;
	}
	
	public boolean isValueDateSymbolic() {
		return isValueDateSymbolic;
	}
	
	public void setValueDateSymbolic(boolean isValueDateSymbolic) {
		this.isValueDateSymbolic = isValueDateSymbolic;
	}
	
	public String getValueDateFromSymbolicValue() {
		return valueDateFromSymbolicValue;
	}
	
	public void setValueDateFromSymbolicValue(String valueDateFromSymbolicValue) {
		this.valueDateFromSymbolicValue = valueDateFromSymbolicValue;
	}
	
	public String getValueDateToSymbolicValue() {
		return valueDateToSymbolicValue;
	}
	
	public void setValueDateToSymbolicValue(String valueDateToSymbolicValue) {
		this.valueDateToSymbolicValue = valueDateToSymbolicValue;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
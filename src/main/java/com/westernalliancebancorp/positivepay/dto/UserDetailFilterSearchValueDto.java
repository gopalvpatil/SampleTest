package com.westernalliancebancorp.positivepay.dto;


public class UserDetailFilterSearchValueDto {
    private int paramSequence;
    private String parameterValue;
    private Long userDetailDefinedFilterId;
    private Long searchParameterId;
    
    //GP
    private String relationalOperator;
    
	public int getParamSequence() {
		return paramSequence;
	}
	public void setParamSequence(int paramSequence) {
		this.paramSequence = paramSequence;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}
	public Long getUserDetailDefinedFilterId() {
		return userDetailDefinedFilterId;
	}
	public void setUserDetailDefinedFilterId(Long userDetailDefinedFilterId) {
		this.userDetailDefinedFilterId = userDetailDefinedFilterId;
	}
	public Long getSearchParameterId() {
		return searchParameterId;
	}
	public void setSearchParameterId(Long searchParameterId) {
		this.searchParameterId = searchParameterId;
	}
	public String getRelationalOperator() {
		return relationalOperator;
	}
	public void setRelationalOperator(String relationalOperator) {
		this.relationalOperator = relationalOperator;
	}
	
	@Override
	public String toString() {
		return "UserDetailFilterSearchValueDto [paramSequence=" + paramSequence
				+ ", parameterValue=" + parameterValue
				+ ", userDetailDefinedFilterId=" + userDetailDefinedFilterId
				+ ", searchParameterId=" + searchParameterId + "]";
	}

}

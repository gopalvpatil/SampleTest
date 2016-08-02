package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

public class SearchParameterDto implements Serializable{

	private static final long serialVersionUID = -6033906891787896997L;
	
	private String relationalOperator;	
	private String parameterCsv;
	 
	public String getRelationalOperator() {
		return relationalOperator;
	}
	public void setRelationalOperator(String relationalOperator) {
		this.relationalOperator = relationalOperator;
	}
	public String getParameterCsv() {
		return parameterCsv;
	}
	public void setParameterCsv(String parameterCsv) {
		this.parameterCsv = parameterCsv;
	}

}

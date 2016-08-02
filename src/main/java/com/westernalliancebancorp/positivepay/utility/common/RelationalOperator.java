package com.westernalliancebancorp.positivepay.utility.common;

/**
 *
 * @author Nishant Arya
 */

public enum RelationalOperator {
	
	CONTAINS("contains"),
	DOES_NOT_CONTAIN("does not contain"),
	IS_ONE_OF("is one of"),
	IS_NOT_ONE_OF("is not one of"),
	EQUALS("equals"),
	IS_GREATER_THAN("is greater than"),
	IS_LESS_THAN("is less than"),
	IS_NOT_GREATER_THAN("is not greater than"),
	IS_NOT_LESS_THAN("is not less than"),
	IS_AFTER("is after"),
	IS_BEFORE("is before"),
	IS_NOT_AFTER("is not after"),
	IS_NOT_BEFORE("is not before"),
	IS_BETWEEN("is between");
	
	private String description;

	public String getDescription() {
		return description;
	}
	
	private RelationalOperator(String description) {
		this.description = description;
	}
	
	public static RelationalOperator getOperatorByDescription(String desc) {
		for(RelationalOperator ro : values()) {
			if(ro.getDescription().equalsIgnoreCase(desc)) {
				return ro;
			}
		}
		return null;
	}
}

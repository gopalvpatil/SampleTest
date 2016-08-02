package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

public class ActionDto implements Serializable{
private static final long serialVersionUID = 1689416422629851168L;
	
	private String name;
	private String description;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}


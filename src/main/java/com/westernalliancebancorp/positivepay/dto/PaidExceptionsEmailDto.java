package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;

public class PaidExceptionsEmailDto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String firstName;
	
	private String lastName;

	public PaidExceptionsEmailDto(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	


	
	
}

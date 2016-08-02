package com.westernalliancebancorp.positivepay.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Embeddable address table
 * @author umeshram
 *
 */
@Embeddable
public class Address {
	
	@Column(name = "ADDRESS1", length = 100, nullable = false)
	private String address1;
	
	@Column(name = "ADDRESS2", length = 50)
	private String address2;
	
	@Column(name = "CITY", length = 50, nullable = false)
	private String city;
	
	@Column(name = "STATE", columnDefinition="char(2)", nullable = false, length = 2)
	private String state;	
	
	@Column(name = "ZIP_CODE", length = 10, nullable = false)
	private String zipCode;

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
}

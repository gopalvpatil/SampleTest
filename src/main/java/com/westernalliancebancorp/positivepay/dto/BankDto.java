package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 
 * @author umeshram
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BankDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Short bankId;//Mapped to assigned bank number
	private String bankName;
	private String bankNumber;//mapped to routing number
	private String streetAddress;
	private String streetAddress2;
	private String websiteUrl;
	private String city;
	private String state;
	private String zipCode;
	private String logoPathFilename;
	private List<CompanyDTO> companies;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Short getBankId() {
		return bankId;
	}
	public void setBankId(Short bankId) {
		this.bankId = bankId;
	}
	
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankNumber() {
		return bankNumber;
	}
	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
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
	
	public String getLogoPathFilename() {
		return logoPathFilename;
	}
	public void setLogoPathFilename(String logoPathFilename) {
		this.logoPathFilename = logoPathFilename;
	}
	public List<CompanyDTO> getCompanies() {
		if(companies == null)
			companies = new ArrayList<CompanyDTO>();
		return companies;
	}
	
	public void setCompanies(List<CompanyDTO> companies) {
		this.companies = companies;
	}
	
	
}

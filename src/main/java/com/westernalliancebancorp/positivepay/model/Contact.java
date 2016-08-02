package com.westernalliancebancorp.positivepay.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing a contact
 * @author Boris Tubak
 */

@javax.persistence.Table(name = "COMPANY_CONTACT")
@EntityListeners(AuditListener.class)
@Entity
public class Contact implements Auditable {
	@javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;
	
	@javax.persistence.Column(name = "NAME", length=100)
    private String name;
	
	@javax.persistence.Column(name = "PHONE_NUMBER", columnDefinition = "char(10)", length = 10)
    private String phone;
	
	@javax.persistence.Column(name = "FAX_NUMBER", columnDefinition = "char(10)", length = 10)
	private String fax;
	
	@javax.persistence.Column(name = "EMAIL", length=100)
    private String email;

	@javax.persistence.Column(name = "ADDRESS1", length=100)
    private String address1;

	@javax.persistence.Column(name = "ADDRESS2", length=100)
    private String address2;

	@javax.persistence.Column(name = "ZIP_CODE", length=10)
    private String zip;

	@javax.persistence.Column(name = "CITY", length=50)
    private String city;

	@javax.persistence.Column(name = "STATE", columnDefinition = "char(2)", length = 2)
    private String state;
	
	 @Column(name = "IS_PRIMARY_CONTACT", nullable = false)
	 private Boolean isPrimaryContact = false; 
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
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

    public Boolean getPrimaryContact() {
        return isPrimaryContact;
    }

    public void setPrimaryContact(Boolean primaryContact) {
        isPrimaryContact = primaryContact;
    }

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();
    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.dto.DashboardDto;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 9:50 PM
 */
@javax.persistence.Table(name = "BANK")
@EntityListeners(AuditListener.class)
@Entity
public class Bank implements Serializable,Auditable,Comparable<Bank> {

	private static final long serialVersionUID = 1L;

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "ROUTING_NUMBER", length = 20)
    private String routingNumber;
    
    @javax.persistence.Column(name = "ASSIGNED_BANK_NUMBER")
    private Short assignedBankNumber;
    
    @OneToMany(mappedBy = "bank", targetEntity = Company.class)
    @JsonIgnore
    private Set<Company> companies = new HashSet<Company>();
    
    @ManyToOne(targetEntity = Bank.class, optional = true)
    @JoinColumn(name = "PARENT_BANK_ID", nullable = true)
    private Bank parent;

    @OneToMany(mappedBy = "bank", targetEntity = Account.class)
    @JsonIgnore
    private Set<Account> accounts = new HashSet<Account>(); //TODO remove it. Account should be reference from Company
    
    @Embedded
    private Address address;
    
    @Column(name = "CONTACT_PHONE", columnDefinition = "char(10)", length = 10)
    private String contactPhone;
    
    @Column(name = "WEBSITE_URL", length = 255)
    private String websiteUrl;
    
    @Column(name = "LOGO_PATH_FILENAME", length = 100)
    private String logoPathFilename;
        
    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active = true; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public Bank getParent() {
        return parent;
    }

    public void setParent(Bank parent) {
        this.parent = parent;
    }
    
    @JsonIgnore
    public void setCompany(Set<Company> companies) {
    	this.companies = companies;
    }
    
    @JsonIgnore
    public Set<Company> getCompanies() {
		return companies;
	}
    
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}

	public Short getAssignedBankNumber() {
		return assignedBankNumber;
	}

	public void setAssignedBankNumber(Short assignedBankNumber) {
		this.assignedBankNumber = assignedBankNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getLogoPathFilename() {
		return logoPathFilename;
	}

	public void setLogoPathFilename(String logoPathFilename) {
		this.logoPathFilename = logoPathFilename;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bank bank = (Bank) o;

        if (id != null ? !id.equals(bank.id) : bank.id != null) return false;
        if (name != null ? !name.equals(bank.name) : bank.name != null) return false;
        if (routingNumber != null ? !routingNumber.equals(bank.routingNumber) : bank.routingNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (routingNumber != null ? routingNumber.hashCode() : 0);
        return result;
    }
    
    @Override
	public int compareTo(Bank o) {
		return this.name.compareTo(o.name);
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

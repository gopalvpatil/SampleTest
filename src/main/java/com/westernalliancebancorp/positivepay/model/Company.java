package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing Company
 * @author Anand Kumar
 */
@javax.persistence.Table(name = "COMPANY")
@EntityListeners(AuditListener.class)
@Entity
public class Company implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "FEDERAL_TAX_ID", length = 30, nullable = true)
    private String federalTaxId;

    @javax.persistence.Column(name = "BRANCH_NAME", length = 50, nullable = true)
    private String branchName;

    @javax.persistence.Column(name = "ACCOUNT_FOR_ANALYSIS", length = 30, nullable = true)
    private String accountForAnalysis;

    @javax.persistence.Column(name = "TIME_ZONE", length = 50, nullable = false)
    private String timeZone;

    @javax.persistence.Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "company", targetEntity = Account.class)
    @JsonIgnore 
    private Set<Account> accounts = new HashSet<Account>();

    @OneToMany(mappedBy = "company", targetEntity = Contact.class, orphanRemoval = true)
    @JsonIgnore 
    private Set<Contact> contacts = new HashSet<Contact>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BANK_ID", nullable = false)
    private Bank bank;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DECISION_WINDOW_ID", nullable = true)
    private DecisionWindow decisionWindow;

    @OneToMany(mappedBy = "company", targetEntity = FileMapping.class)
    private Set<FileMapping> fileMappings = new HashSet<FileMapping>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public Bank getBank() {
		return bank;
	}
	
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
	public Set<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(Set<Account> accounts) {
		this.accounts = accounts;
	}	

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}

	public String getFederalTaxId() {
		return federalTaxId;
	}

	public void setFederalTaxId(String federalTaxId) {
		this.federalTaxId = federalTaxId;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAccountForAnalysis() {
		return accountForAnalysis;
	}

	public void setAccountForAnalysis(String accountForAnalysis) {
		this.accountForAnalysis = accountForAnalysis;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public DecisionWindow getDecisionWindow() {
		return decisionWindow;
	}

	public void setDecisionWindow(DecisionWindow decisionWindow) {
		this.decisionWindow = decisionWindow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Company other = (Company) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
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

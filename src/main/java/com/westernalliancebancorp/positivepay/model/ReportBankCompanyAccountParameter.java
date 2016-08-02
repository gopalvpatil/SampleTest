package com.westernalliancebancorp.positivepay.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing a report parameter that stores bank, company or account
 * @author Boris Tubak
 */
@javax.persistence.Table(name = "REPORT_BANK_COMPANY_ACCOUNT_PARAMETER")
@EntityListeners(AuditListener.class)
@Entity
public class ReportBankCompanyAccountParameter implements Auditable {
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    
    @OneToOne(optional = true)
    @JoinColumn(name = "BANK_ID")
    private Bank bank;

    @OneToOne(optional = true)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @OneToOne(optional = true)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @ManyToOne(optional = false)
    @JoinColumn(name = "REPORT_ID")
    private Report report;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
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

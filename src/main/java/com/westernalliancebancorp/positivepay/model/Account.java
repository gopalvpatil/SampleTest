package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;
import org.hibernate.annotations.Type;

/**
 * Account is
 *
 * @author Giridhar Duggirala
 */
@Table(name = "ACCOUNT", uniqueConstraints = {@UniqueConstraint(columnNames = { "number", "bank_id" })})
@EntityListeners(AuditListener.class)
@Entity
public class Account implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NUMBER", length = 20, nullable = false)
    private String number;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @Column(name = "STALE_DAYS", length = 4, nullable = false)
    private int staleDays;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    @JoinColumn(name = "ACCOUNT_TYPE_ID", nullable = true)
    private AccountType accountType;
    
    @Column(name = "OPEN_DATE", nullable = true)
    @Type(type="date")
    private Date openDate; 
    
    @Column(name = "FILE_INPUT_METHOD", length = 10, nullable = true)
    private String fileInputMethod;
    
    @Column(name = "REPORT_OUTPUT_METHOD", length = 10, nullable = true)
    private String reportOutputMethod;
    
    @Column(name = "DATA_OUTPUT_METHOD", length = 10, nullable = true)
    private String dataOutputMethod;
    
    @Column(name = "DEFAULT_PP_DECISION", length = 10, nullable = false)
    private String defaultPpDecision;    
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    @JoinColumn(name = "PAYMENT_TYPE_ID", nullable = true)
    private PaymentType paymentType;	
    
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    @JoinColumn(name = "ACCOUNT_CYCLE_CUTOFF_ID", nullable = true)
    private AccountCycleCutOff accountCycleCutOff; 

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnore
    @JoinColumn(name = "ACCOUNT_SERVICE_OPTION_ID", nullable = true)
    private AccountServiceOption accountServiceOption;
	
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive = true;

    @ManyToOne(optional = true)
    @JsonIgnore
    @JoinColumn(name = "BANK_ID", nullable = true)
    private Bank bank;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account", targetEntity = Check.class)
    @JsonIgnore
    private Set<Check> check = new HashSet<Check>();

    @OneToMany(mappedBy = "account", targetEntity = ReferenceData.class)
    @JsonIgnore
    private Set<ReferenceData> referenceDataSet = new HashSet<ReferenceData>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "accounts")
    @JsonIgnore
    public Set<UserDetail> userDetails = new HashSet<UserDetail>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStaleDays() {
		return staleDays;
	}

	public void setStaleDays(int staleDays) {
		this.staleDays = staleDays;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	public String getFileInputMethod() {
		return fileInputMethod;
	}

	public void setFileInputMethod(String fileInputMethod) {
		this.fileInputMethod = fileInputMethod;
	}

	public String getReportOutputMethod() {
		return reportOutputMethod;
	}

	public void setReportOutputMethod(String reportOutputMethod) {
		this.reportOutputMethod = reportOutputMethod;
	}

	public String getDataOutputMethod() {
		return dataOutputMethod;
	}

	public void setDataOutputMethod(String dataOutputMethod) {
		this.dataOutputMethod = dataOutputMethod;
	}

	public String getDefaultPpDecision() {
		return defaultPpDecision;
	}

	public void setDefaultPpDecision(String defaultPpDecision) {
		this.defaultPpDecision = defaultPpDecision;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
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

	public Set<Check> getCheck() {
		return check;
	}

	public void setCheck(Set<Check> check) {
		this.check = check;
	}

    public Set<ReferenceData> getReferenceDataSet() {
        return referenceDataSet;
    }

    public void setReferenceDataSet(Set<ReferenceData> referenceDataSet) {
        this.referenceDataSet = referenceDataSet;
    }

    @JsonIgnore
    public Set<UserDetail> getUserDetails() {
        return userDetails;
    }

    @JsonIgnore
    public void setUserDetails(Set<UserDetail> userDetails) {
        this.userDetails = userDetails;
    }

    public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public AccountCycleCutOff getAccountCycleCutOff() {
		return accountCycleCutOff;
	}

	public void setAccountCycleCutOff(AccountCycleCutOff accountCycleCutOff) {
		this.accountCycleCutOff = accountCycleCutOff;
	}

	public AccountServiceOption getAccountServiceOption() {
		return accountServiceOption;
	}

	public void setAccountServiceOption(AccountServiceOption accountServiceOption) {
		this.accountServiceOption = accountServiceOption;
	}

	@Embedded
    private AuditInfo auditInfo = new AuditInfo();
    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", name='" + name + '\'' +
                ", staleDays=" + staleDays +
                ", openDate=" + openDate +
                ", fileInputMethod='" + fileInputMethod + '\'' +
                ", reportOutputMethod='" + reportOutputMethod + '\'' +
                ", dataOutputMethod='" + dataOutputMethod + '\'' +
                ", defaultPpDecision='" + defaultPpDecision + '\'' +
                ", isActive=" + isActive +
                ", auditInfo=" + auditInfo +
                '}';
    }
}

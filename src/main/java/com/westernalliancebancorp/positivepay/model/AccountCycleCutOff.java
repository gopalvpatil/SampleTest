package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * @author Gopal Patil
 *
 */
@Table(name = "ACCOUNT_CYCLE_CUTOFF")
@EntityListeners(AuditListener.class)
@Entity
public class AccountCycleCutOff implements Serializable, Auditable {
	
	private static final long serialVersionUID = 3019238396116067529L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;
	
    @Column(name = "DESCRIPTION", length = 255, nullable = false)
    private String description;
	
    @Column(name="IS_ACTIVE", nullable = false)
    private boolean isActive = true;
    
    @OneToMany(mappedBy = "accountCycleCutOff", fetch = FetchType.LAZY, targetEntity = Account.class)
    private Set<Account> account = new HashSet<Account>();
    
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Set<Account> getAccount() {
		return account;
	}

	public void setAccount(Set<Account> account) {
		this.account = account;
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

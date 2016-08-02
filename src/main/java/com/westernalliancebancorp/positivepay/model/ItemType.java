/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing a Issue Code
 * @author Anand Kumar
 *
 */
@XmlRootElement(name = "itemType")
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "ITEM_TYPE")
public class ItemType implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;

    public enum CODE {
        I("Issued"), V("Void"),
        S("Stop"),P("Paid"),D("Deposit");
        private String description;
        CODE(String description) {
            this.description = description;
        }
        
        public String getName() {
            return this.name();
        }

        public String getDescription() {
            return this.description;
        }
    }

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false, unique = true)
	private String name;
	
	@Column(name = "DESCRIPTION", length = 255, nullable = false)
	private String description;
	
	@Column(name = "DEBIT_CREDIT_SIGN", length = 10, nullable = true)
	private String debitCreditSign;
	
	@Column(name = "ITEM_CODE", length = 1, nullable = true, unique = true)
	private String itemCode;
	
	@Column(name = "IS_ACTIVE", nullable = false)
	private boolean isActive;
	
	@JsonIgnore
	@OneToMany(mappedBy = "itemType", targetEntity = Check.class, fetch = FetchType.LAZY)
    private Set<Check> checks = new HashSet<Check>();

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

	public String getDebitCreditSign() {
        return debitCreditSign;
    }

	public void setDebitCreditSign(String debitCreditSign) {
		this.debitCreditSign = debitCreditSign;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

    public Set<Check> getChecks() {
        return checks;
    }

    public void setChecks(Set<Check> checks) {
        this.checks = checks;
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

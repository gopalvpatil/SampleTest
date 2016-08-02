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
 * Model representing various check status
 *
 * @author Anand Kumar
 */
@XmlRootElement(name = "checkstatus")
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "CHECK_STATUS", uniqueConstraints = {@UniqueConstraint(columnNames = { "NAME", "VERSION" })})
public class CheckStatus implements Serializable, Auditable {
    private static final long serialVersionUID = -8201515362298788164L;
    
    public static final String START_STATUS_NAME = "start";
    public static final String ISSUED_STATUS_NAME = "issued";
    public static final String PAID_STATUS_NAME = "paid";
    public static final String STOP_STATUS_NAME = "stop";
    public static final String STALE_STATUS_NAME = "stale";
    public static final String VOID_STATUS_NAME = "void";
    public static final String VOID_PAID_STATUS_NAME = "voidPaid";
    public static final String VOID_NOT_ISSUED = "voidNotIssued";
    public static final String DUPLICATE_PAID_STATUS_NAME = "duplicatePaid";
    public static final String DUPLICATE_STOP_STATUS_NAME = "duplicateStop";
    public static final String PAID_NOT_ISSUED = "paidNotIssued";
    public static final String STOP_AFTER_VOID = "stopAfterVoid";
    public static final String STALE_PAID = "stalePaid";
    public static final String STALE_STOP = "staleStop";
    public static final String INVALID_AMOUNT_PAID = "invalidAmountPaid";
    public static final String INVALID_AMOUNT_STOP = "invalidAmountStop";
    
    public enum DISPLAYABLE_CHECK_STATUS{
    	//START_STATUS_NAME("start", "Start"), 
    	VOID_STATUS_NAME("void", "Void"),
    	ISSUED_STATUS_NAME("issued", "Issued"),
    	PAID_STATUS_NAME("paid", "Paid"),
    	STOP_STATUS_NAME("stop", "Stop"), //VOID_PAID_STATUS_NAME("voidPaid", "Void Paid"), VOID_NOT_ISSUED("voidNotIssued", "Void Not Issued"), DUPLICATE_STOP_STATUS_NAME("duplicateStop", "Duplicate Stop"),
    	STALE_STATUS_NAME("stale", "Stale"), //PAID_NOT_ISSUED("paidNotIssued", "Paid Not Issued"),DUPLICATE_PAID_STATUS_NAME("duplicatePaid", "Duplicate Paid"),
    	//NO_PAY_NAME("noPay", "No Pay");
    	INACTIVE_STATUS_NAME("inactive", "Inactive");
        private String name;
        private String description;
        
		public String getDescription() {
			return description;
		}
		DISPLAYABLE_CHECK_STATUS(String name, String description) {
        	 this.name = name;
        	 this.description = description;
        	  }
        public String getName() {
        	 return this.name;
        }

        
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;

    @Column(name = "VERSION", nullable = false)
    private Integer version;
    
    @JsonIgnore
    @OneToMany(mappedBy = "checkStatus", targetEntity = Check.class)
    private Set<Check> checks = new HashSet<Check>();

    @Column(name = "IS_IN_EXCEPTION", nullable = false)
    private Boolean isExceptionalStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String checkStatus) {
        this.name = checkStatus;
    }

    public Set<Check> getChecks() {
        return checks;
    }

    public void setChecks(Set<Check> checks) {
        this.checks = checks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Boolean getExceptionalStatus() {
        return resolveBooleanObject(this.isExceptionalStatus);
    }

    public void setExceptionalStatus(Boolean exceptionalStatus) {
        isExceptionalStatus = resolveBooleanObject(exceptionalStatus);
    }

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    private boolean resolveBooleanObject (Boolean bool) {
        return (bool != null && bool);
    }
}

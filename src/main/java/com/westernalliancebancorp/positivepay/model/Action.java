package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Action is
 *
 * @author Giridhar Duggirala
 */

@javax.persistence.Table(name = "ACTION", uniqueConstraints = {@UniqueConstraint(columnNames = { "action_type", "name", "version" })})
@EntityListeners(AuditListener.class)
@Entity
public class Action implements Auditable{
    public enum ACTION_TYPE {
        WORK_FLOW_ACTION {
            public String toString() {
                return "WORK_FLOW_ACTION";
            }
        },
        NON_WORK_FLOW_ACTION {
            public String toString() {
                return "NON_WORK_FLOW_ACTION";
            }
        }
    }

    public enum ACTION_NAME {
        CHANGE_ACCOUNT_NUMBER("Change Account number", "changeAccountNumber", true),
        CHANGE_CHECK_NUMBER("Change Check number", "changeCheckNumber", true),
        CHANGE_CURRENT_ACCOUNT_NUMBER("Change current Account number", "changeCurrentAccountNumber", true),
        UNMATCH("Unmatch", "unmatch", false),
        CHANGE_CURRENT_CHECK_NUMBER("Change current check number", "changeCurrentCheckNumber", true),
        DELETE("Delete", "delete", true),
        ADJUST_AMOUNT("Adjust amount", "adjustAmount", false),
        NO_PAY("no pay", "noPay", false),
        CHANGE_PAYEE("Change payee", "changePayee", false),
        CHANGE_ISSUE_DATE("Change issued date", "changeIssuedDate", true),
        VOID("Void", "void", false),
        STOP("Stop", "stop", false),
        CREATED("Created", "created", false),
        ADJUST_AMOUNT_ISSUED("Adjust Amount Issued","adjustAmountIssued",true),
        ADJUST_AMOUNT_PAID("Adjust Amount Paid","adjustAmountPaid",true),
        MATCH("Match", "match", false),
        DUPLICATE_STOP_PAID_CREATED("Duplicate Paid or stop created","duplicatePaidOrStopCreated",true),
        DUPLICATE_STOP_PAID_RESOLVED("Duplicate Paid or stop resolved","duplicatePaidOrStopResolved",true),
        ADJUST_AMOUNT_STOP("Adjust Amount Stop","adjustAmountStop",true),
        REMOVE_STOP_VOID("Remove Void or Stop","removeVoidOrStop",true);

        private String description, xmlName;
        private boolean isAnAdminAction;

        ACTION_NAME(String description, String xmlName, boolean isAnAdminAction) {
            this.description = description;
            this.xmlName = xmlName;
            this.isAnAdminAction = isAnAdminAction;
        }

        public String getName() {
            return this.xmlName;
        }

        public String getDescription() {
            return this.description;
        }

        public boolean isAnAdminAction() {
            return this.isAnAdminAction;
        }
    }
    
    public String toString() {
    	  return this.name;
    	  }

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION_TYPE", length = 75, nullable = true)
    private ACTION_TYPE actionType;

    @Column (name = "VERSION", nullable = true)
    private Integer version;

    @Column(name = "IS_AN_ADMIN_ACTION", nullable = false)
    private Boolean isAnAdminAction;

    @OneToMany(mappedBy = "action", targetEntity = CheckHistory.class)
    private Set<CheckHistory> checkHistories = new HashSet<CheckHistory>();

    @OneToMany(mappedBy = "action", targetEntity = Check.class)
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

    public ACTION_TYPE getActionType() {
        return actionType;
    }

    public void setActionType(ACTION_TYPE actionType) {
        this.actionType = actionType;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<CheckHistory> getCheckHistories() {
		return checkHistories;
	}

	public void setCheckHistories(Set<CheckHistory> checkHistories) {
		this.checkHistories = checkHistories;
	}

    public Boolean isAdminAction() {
        return isAnAdminAction;
    }

    public void isAdminAction(Boolean anAdminAction) {
        isAnAdminAction = anAdminAction;
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

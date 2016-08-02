package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 4/4/14
 * Time: 3:47 PM
 */
@javax.persistence.Table(name = "CHECK_DETAIL_LINKAGE_TYPE")
@EntityListeners(AuditListener.class)
@Entity
public class LinkageType implements Auditable {
    public enum NAME {
        ACCOUNT_NUMBER_CHANGED("Account number changed"), CHECK_NUMBER_CHANGED("Check number changed"),
        ADJ_ISSUED_AMOUNT_EXCEEDED("Adj. Issued Amount Exceeded"), ADJ_PAID_AMOUNT_EXCEEDED("Adj. Paid Amount Exceeded"),
        ADJ_AMOUNT("Adjustment amount");
        private String description;

        NAME(String description) {
            this.description = description;
        }

        public String getName() {
            return this.name();
        }

        public String getDescription() {
            return this.description;
        }

        public String toString() {
            return this.name();
        }
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", length = 50, unique = true, nullable = false)
    private NAME name;

    @Column(name = "DESCRIPTION", length = 255, unique = false, nullable = false)
    private String description;

    @OneToMany(mappedBy = "linkageType", targetEntity = Check.class)
    private Set<Check> checks = new HashSet<Check>();

    @OneToMany(mappedBy = "linkageType", targetEntity = Check.class)
    private Set<AdjustmentCheck> adjustmentChecks = new HashSet<AdjustmentCheck>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NAME getName() {
        return name;
    }

    public void setName(NAME name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Set<Check> getChecks() {
        return checks;
    }

    public void setChecks(Set<Check> checks) {
        this.checks = checks;
    }

    public Set<AdjustmentCheck> getAdjustmentChecks() {
        return adjustmentChecks;
    }

    public void setAdjustmentChecks(Set<AdjustmentCheck> adjustmentChecks) {
        this.adjustmentChecks = adjustmentChecks;
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

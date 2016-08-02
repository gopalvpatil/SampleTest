package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 4/4/14
 * Time: 3:50 PM
 */

//@javax.persistence.Table(name = "CHECK_LINKAGE")
@EntityListeners(AuditListener.class)
//@Entity
public class CheckLinkage implements Auditable {

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "LINKAGE_TYPE_ID", nullable = false)
    private LinkageType linkageType;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "CHECK_ID", nullable = true)
    private Check linkedCheck;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "CHECK_ID", nullable = true)
    private Check actualCheck;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LinkageType getLinkageType() {
        return linkageType;
    }

    public void setLinkageType(LinkageType linkageType) {
        this.linkageType = linkageType;
    }

    public Check getLinkedCheck() {
        return linkedCheck;
    }

    public void setLinkedCheck(Check linkedCheck) {
        this.linkedCheck = linkedCheck;
    }

    public Check getActualCheck() {
        return actualCheck;
    }

    public void setActualCheck(Check actualCheck) {
        this.actualCheck = actualCheck;
    }

    private AuditInfo auditInfo = new AuditInfo();
    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}

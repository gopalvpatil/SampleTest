package com.westernalliancebancorp.positivepay.model;

/**
 * User: gduggirala
 * Date: 10/7/14
 * Time: 6:51 PM
 */

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "CHECK_DETAIL_ADJUSTMENT")
public class AdjustmentCheck implements Serializable, Auditable {
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_DETAIL_ID", updatable = true)
    private Check check;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINKAGE_TYPE_ID", updatable = true)
    private LinkageType linkageType;

    @Column(name = "ISSUED_AMOUNT", precision = 19, scale = 2, nullable = true)
    private BigDecimal amount;

    @Column(name = "DUPLICATE_IDENTIFIER", length = 255, nullable = true)
    private String digest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Check getCheck() {
        return check;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public LinkageType getLinkageType() {
        return linkageType;
    }

    public void setLinkageType(LinkageType linkageType) {
        this.linkageType = linkageType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
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

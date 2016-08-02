package com.westernalliancebancorp.positivepay.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Sso is
 *
 * @author Giridhar Duggirala
 */
@javax.persistence.Table(name = "SSO")
@Entity
public class Sso {
    public enum Status { CREATED, VALIDATED_SUCCESS, VALIDATED_FAILED, BOTTOM_LINE_KILL, LOGGED_OUT };

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "UID", unique = true)
    private String uid;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sso")
    private Set<SsoAttribute> ssoAttributes = new HashSet<SsoAttribute>();

    @javax.persistence.Column(name = "CREATE_TIME_IN_MILLIS", unique = true)
    private Long createTimeInMillis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uidKey) {
        this.uid = uidKey;
    }


    public Set<SsoAttribute> getSsoAttributes() {
        return ssoAttributes;
    }

    public void setSsoAttributes(Set<SsoAttribute> ssoAttributes) {
        this.ssoAttributes = ssoAttributes;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getCreateTimeInMillis() {
        return createTimeInMillis;
    }

    public void setCreateTimeInMillis(Long createTimeInMillis) {
        this.createTimeInMillis = createTimeInMillis;
    }
}

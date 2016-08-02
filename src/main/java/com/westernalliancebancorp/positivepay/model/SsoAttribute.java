package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;

import javax.persistence.*;

/**
 * SsoAttribute is
 *
 * @author Giridhar Duggirala
 */
@javax.persistence.Table(name = "SSO_ATTRIBUTE")
@Entity
public class SsoAttribute {
    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SSO_ID", nullable = false)
    private Sso sso;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "VALUE",  nullable = false)
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Sso getSso() {
        return sso;
    }

    public void setSso(Sso sso) {
        this.sso = sso;
    }
}

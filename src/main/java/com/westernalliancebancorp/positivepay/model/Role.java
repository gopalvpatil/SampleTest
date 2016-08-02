package com.westernalliancebancorp.positivepay.model;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 8:46 PM
 */
@Table(name = "ROLE")
@EntityListeners(AuditListener.class)
@Entity
public class Role implements Auditable{
    public static enum Roles {
        ROLE_BANK_ADMIN("Bank Admin"), ROLE_CORPORATE_ADMIN("Corporate Admin"), ROLE_CORPORATE_USER("Corporate User");
        private String description;

        Roles(String description) {
            this.description = description;
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

    /**
     * This is used to identify the role programatically. The name should be like "ADMIN", "LOCAL_USER"
     */
    @Column(name = "NAME", length = 50, unique = true, nullable = false)
    private String name;

    /**
     * This will be used to show to the user this may look like "Admin", "Local UserDetail"
     */
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive = true;
    
    @Column(name = "LABEL", length = 50, unique = false, nullable = true)
    private String label;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ROLE_PERMISSION", joinColumns = {
            @JoinColumn(name = "ROLE_ID", nullable = false, updatable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "PERMISSION_ID", nullable = false, updatable = true)
            })
    private Set<Permission> permissions = new HashSet<Permission>();

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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

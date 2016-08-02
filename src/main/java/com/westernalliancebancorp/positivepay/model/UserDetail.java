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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 2:47 PM
 */
//@Table(name = "USER_DETAIL", uniqueConstraints = {@UniqueConstraint(columnNames = { "CORPORATE_USERNAME", "INSTITUTION_ID" })})
@Table(name = "USER_DETAIL")
@EntityListeners(AuditListener.class)
@Entity
public class UserDetail implements Auditable {
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "FIRST_NAME", length = 50, nullable = true)
    private String firstName;
    
    @Column(name = "LAST_NAME", length = 50, nullable = true)
    private String lastName;
    
    @Column(name = "USERNAME", length = 20, unique = true, nullable = false)
    private String userName;
    
    @Column(name = "CORPORATE_USERNAME", length = 20, unique = false, nullable = true)
    private String corporateUserName;
    
    @Column(name = "PASSWORD", nullable = true)
    private String password;
    
    @Column(name = "EMAIL_ADDRESS", length = 100, unique = true, nullable = false)
    private String email;
    
    @Column(name = "IS_ACTIVE")
    private boolean isActive;
    
    @Column(name = "IS_LOCKED")
    private boolean locked;
    
    @Column(name = "BOTTOMLINE_INSTITUTION_ID", nullable = false)
    private String institutionId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_DETAIL_ACCOUNT", joinColumns = {
            @JoinColumn(name = "USER_DETAIL_ID", nullable = false, updatable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "ACCOUNT_ID", nullable = false, updatable = true)
            })
    @JsonIgnore
    private Set<Account> accounts = new HashSet<Account>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_DETAIL_PERMISSION", joinColumns = {
            @JoinColumn(name = "USER_DETAIL_ID", nullable = false, updatable = true)},
            inverseJoinColumns = {
            @JoinColumn(name = "PERMISSION_ID", nullable = false, updatable = true)
    })
    
    @JsonIgnore
    private Set<Permission> permissions = new HashSet<Permission>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    @JsonIgnore
    private Role baseRole;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

	public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

	public String getCorporateUserName() {
        return corporateUserName;
    }

    public void setCorporateUserName(String corporateUserName) {
        this.corporateUserName = corporateUserName;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Role getBaseRole() {
        return baseRole;
    }

    public void setBaseRole(Role baseRole) {
        this.baseRole = baseRole;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
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

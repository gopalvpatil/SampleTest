package com.westernalliancebancorp.positivepay.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * User: gduggirala
 * Date: 28/4/14
 * Time: 10:00 AM
 */
@Table(name = "Permission")
@EntityListeners(AuditListener.class)
@Entity
public class Permission implements Auditable {
    public enum TYPE {
        ITEMS("Items"), MANUAL_ENTRY("Manual Entry"),
        USER_ROLE_MANAGEMENT("User/Role Management"), OTHER_PERMISSIONS("Other Permissions"),
        PAYMENTS("Payments");
                
        private String description;

        TYPE(String description) {
            this.description = description;
        }

        public String getName() {
            return this.name();
        }

        public String getDescription() {
            return this.description;
        }

        public String value() { 
        	return this.name(); 
        }
    };
    
    public enum NAME {
    	ADJUST_AMOUNT("Ability to change the amount of the item"),
    	CHANGE_ACCOUNT_NUMBER("Ability to change the account number of the item"), 
    	CHANGE_ITEM_CODE("Ability to manually key in issue or void items"),
    	CHANGE_PAYEE("Ability to change the payee of the item"), 
    	CHANGE_REASON("Ability to change the reason/comments of an item"),
    	CHANGE_CHECKNUMBER("Ability to change the check number"),
    	DELETE("Ability to delete an item"), 
    	NO_PAY("Ability to tell the bank to return the check (do not pay)"),
    	ISSUED("Ability to identify as 'sent for payment'"), 
    	STOP("Allow user to enter Stop item types in the Manual Entry screen"),
    	VOID("Allow user to enter Void item types in the Manual Entry screen"),
    	REACTIVATE_USER("Ability to activate users"), 
    	ADD_ROLE("Ability to create a role"),
    	ADD_USER("Ability to create a user"), 
    	MOVE_USER("Ability to move a user from end-user to admin role"),
    	SAVE_ACCOUNTS("Ability to add a new checking account to the system"), 
    	SAVE_MANUAL_ENTRY_ITEM_TYPES("Ability to manually key in issue or void items"),
    	SAVE_SCREEN_AND_ACTION("Ability to save security setting changes"), 
    	SAVE_USER("Abilty to edit a user and save changes"),
    	DOWNLOAD_FILES("Ability to download files from the file server to their computer"), 
    	RESOLVE_EXCEPTIONS("Ability for customer to view and resolve their non-matched items"),
    	RUN_REPORTS("Ability to run Reports and Extracts"), 
    	CHANGE_ITEM_DATE("Ability to change the date of an item"),
    	DEPOSIT("Abilty to select Deposit of the item"), 
    	PAID("Ability to select Paid for the item"),
    	ADD_COMMENT("Abilty to add comments to payment related to action to taken"), 
    	DELETE_PAYMENT("Ability to delete a payment"),
    	MAKE_STALE("Ability to manually change the status of a check to stale"), 
    	REMOVE_STOP("Ability to take a stop off of a check"),
    	REMOVE_VOID("Ability to remove a void on a payment"), 
    	STOP_PAYMENT("Ability to stop a payment"),
    	UNMATCH("Ability to unmatch a payment"), 
    	VOID_PAYMENT("Ability to void a payment"),
    	ARCHIVE_USER("Ability to deactivate a user"), 
    	VIEW_AUDIT_TRAIL("Ability for user to view an audit trail"),
    	CREATE_PAID("Ability to manually create a Paid item"), 
    	CREATE_DEPOSIT("Ability to manually enter a deposit"),
    	UPLOAD_FILES("Ability to upload files");
                
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

        public String value() { 
        	return this.name(); 
        }
    };

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", length = 50, nullable = false)
    private NAME name;

    @Column(name = "DESCRIPTION", length = 255, unique = false, nullable = false)
    private String description;
    
    @Column(name = "LABEL", length = 50, unique = false, nullable = true)
    private String label;
  
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    public Set<UserDetail> userDetails = new HashSet<UserDetail>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions")
    @JsonIgnore
    public Set<Role> roles = new HashSet<Role>();

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 50, nullable = false)
    private TYPE type;

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

    public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<UserDetail> getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(Set<UserDetail> userDetails) {
        this.userDetails = userDetails;
    }

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
        	return true;
        
        if (o == null || getClass() != o.getClass()) 
        	return false;

        Permission that = (Permission) o;

        if (!description.equals(that.description)) 
        	return false;
        
        if (type != that.type) 
        	return false;
        
        if (!id.equals(that.id)) 
        	return false;
        
        if (!name.equals(that.name)) 
        	return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}

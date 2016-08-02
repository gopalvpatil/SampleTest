package com.westernalliancebancorp.positivepay.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;

/**
 * UserActivity is
 *
 * @author Giridhar Duggirala
 */
@Table(name = "USER_DETAIL_ACTIVITY")
@EntityListeners(AuditListener.class)
@Entity
public class UserActivity {
    public static enum Activity {
        LOG_IN("Log In"), LOG_OUT("Logout"), INACTIVATED("Inactivated"), ACTIVATED("Activated"), EMAIL_CHANGE("Email Change"), USER_NAME_CHANGE("User name change"), PASSWORD_RESET("Password Reset"),
        EMULATED_COOKIE_CREATED("Emulated cookie created"), EMULATED_COOKIE_DELETED("Emulated cookie deleted"), SYSTEM_MESSAGE_READ("User read the system message");
        private String description;
        
        Activity(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return this.description;
        }
    }
    
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "NAME", unique = true, length = 50, nullable = false)
    private String name;
    
    @Column(name = "DESCRIPTION", unique = false, nullable = false)
    private String description;

    @OneToMany(mappedBy = "userActivity", targetEntity = UserHistory.class)
    private Set<UserHistory> userHistorySet = new HashSet<UserHistory>();

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

    public Set<UserHistory> getUserHistorySet() {
        return userHistorySet;
    }
}

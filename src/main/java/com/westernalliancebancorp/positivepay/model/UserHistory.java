package com.westernalliancebancorp.positivepay.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * UserHistory is
 *
 * @author Giridhar Duggirala
 */
@Table(name = "USER_DETAIL_HISTORY")
@EntityListeners(AuditListener.class)
@Entity
public class UserHistory implements Auditable {
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    //Consider this for Action on UserDetail (If "A" has inactivted "B" then "B" will be put into this column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_DETAIL_ID", nullable = false)
    private UserDetail userDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_DETAIL_ACTIVITY_ID", nullable = false)
    private UserActivity userActivity;

    @Column(name = "USER_COMMENT", unique = false, nullable = false)
    private String userComment;

    @Column(name = "SYSTEM_COMMENT", unique = false, nullable = false)
    private String systemComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDetail getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetail userDetail) {
        this.userDetail = userDetail;
    }

    public UserActivity getUserActivity() {
        return userActivity;
    }

    public void setUserActivity(UserActivity userActivity) {
        this.userActivity = userActivity;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getSystemComment() {
        return systemComment;
    }

    public void setSystemComment(String systemComment) {
        this.systemComment = systemComment;
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

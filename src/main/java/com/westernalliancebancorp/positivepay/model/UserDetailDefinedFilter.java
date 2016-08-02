package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

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
 * Search Parameter
 * @author Anand Kumar
 */

@Table(name = "USER_DETAIL_DEFINED_FILTER")
@EntityListeners(AuditListener.class)
@Entity
public class UserDetailDefinedFilter implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_DETAIL_ID", nullable = false)
    private UserDetail userDetail;

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

	public UserDetail getUserDetail() {
		return userDetail;
	}

	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
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
	public String toString() {
		return "UserDetailDefinedFilter [id=" + id + ", name=" + name
				+ ", description=" + description + ", userDetail=" + userDetail
				+ ", auditInfo=" + auditInfo + "]";
	}
}

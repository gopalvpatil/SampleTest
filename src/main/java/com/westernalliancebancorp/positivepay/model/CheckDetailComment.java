package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

@javax.persistence.Table(name = "CHECK_DETAIL_COMMENT")
@EntityListeners(AuditListener.class)
@Entity
public class CheckDetailComment implements Serializable, Auditable {
	
	private static final long serialVersionUID = -2857106631568212943L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "COMMENT", length = 255, nullable = false)
	private String comment;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_DETAIL_ID", updatable = false, nullable = false)
    private Check check;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Check getCheck() {
		return check;
	}

	public void setCheck(Check check) {
		this.check = check;
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

package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Search Parameter
 * @author Anand Kumar
 */
@Table(name = "SEARCH_PARAMETER")
@EntityListeners(AuditListener.class)
@Entity
public class SearchParameter implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;

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
		return "SearchParameter [id=" + id + ", name=" + name + ", auditInfo="
				+ auditInfo + "]";
	}
}

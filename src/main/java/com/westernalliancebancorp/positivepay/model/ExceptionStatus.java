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
 * Exception Status Model
 *
 * @author Anand Kumar
 */
@Table(name = "exception_status")
@EntityListeners(AuditListener.class)
@Entity
public class ExceptionStatus implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;

    public enum STATUS {
        OPEN("Exception has been resolved"),
        CLOSED("Exception has not been resolved");
        private String description;

        STATUS(String description) {
            this.description = description;
        }

        public String getName() {
            return this.name();
        }

        public String toString() {
            return this.name();
        }

        public String getDescription() {
            return this.description;
        }
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "NAME", length = 50, nullable = false)
    private String name;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive = true;

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
		return "ExceptionStatus [id=" + id + ", name=" + name
				+ ", description=" + description + ", isActive=" + isActive
				+ ", auditInfo=" + auditInfo + "]";
	}
}

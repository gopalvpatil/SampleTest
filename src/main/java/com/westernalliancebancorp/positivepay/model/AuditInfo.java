package com.westernalliancebancorp.positivepay.model;

import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * UserDetail: gduggirala
 * Date: 11/21/13
 * Time: 4:10 PM
 */
@Embeddable
public class AuditInfo {
    @javax.persistence.Column(name = "DATE_CREATED", nullable = false, insertable = true, updatable = false)
    private Date dateCreated;
    @javax.persistence.Column(name = "DATE_MODIFIED", nullable = false, updatable = true)
    private Date dateModified;
    @javax.persistence.Column(name = "CREATED_BY", length = 20, nullable = false, insertable = true, updatable = false)
    private String createdBy;
    @javax.persistence.Column(name = "MODIFIED_BY", length = 20, nullable = false, updatable = true)
    private String modifiedBy;

    public void setDateCreated(Date createdDate) {
        this.dateCreated = createdDate;
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String toString() {
        return "AuditInfo{" +
                "dateCreated=" + dateCreated +
                ", dateModified=" + dateModified +
                ", createdBy='" + createdBy + '\'' +
                ", modifiedBy='" + modifiedBy + '\'' +
                '}';
    }
}

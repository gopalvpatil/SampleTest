package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User: gduggirala
 * Date: 12/5/14
 * Time: 11:05 AM
 */

@Table(name = "SYSTEM_MESSAGE")
@EntityListeners(AuditListener.class)
@Entity
public class SystemMessage implements Serializable, Auditable {

    public enum TYPE {
        LOGIN {
            public String toString() {
                return "LOGIN";
            }
        },
        POSTLOGIN {
            public String toString() {
                return "POSTLOGIN";
            }
        }
    }

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10, nullable = false)
    private TYPE type;

    @Column(name = "START_DATETIME")
    private Date startDateTime;

    @Column(name = "END_DATETIME")
    private Date endDateTime;

    @Column(name = "MESSAGE", length = 1000)
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

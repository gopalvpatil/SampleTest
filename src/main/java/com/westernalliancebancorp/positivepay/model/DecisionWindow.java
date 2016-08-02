package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

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
 * Class representing the decision window
 *
 * @author Moumita Ghosh
 */
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "DECISION_WINDOW")
public class DecisionWindow implements Serializable, Auditable {

    private static final long serialVersionUID = -2221215362298788164L;

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "START_WINDOW",nullable = false)
    private Time startWindow;
    
    @Column(name = "END_WINDOW", nullable = false)
    private Time endWindow;

    @Column(name = "TIME_ZONE", length = 50, nullable = false)
    private String timeZone;
    
    @Column(name = "DECISION_WINDOW_DATE",nullable = true)
    private Date decisionWindowDate;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
	 * @return the startWindow
	 */
	public Time getStartWindow() {
		return startWindow;
	}

	/**
	 * @param startWindow the startWindow to set
	 */
	public void setStartWindow(Time startWindow) {
		this.startWindow = startWindow;
	}

	/**
	 * @return the endWindow
	 */
	public Time getEndWindow() {
		return endWindow;
	}

	/**
	 * @param endWindow the endWindow to set
	 */
	public void setEndWindow(Time endWindow) {
		this.endWindow = endWindow;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the decisionWindowDate
	 */
	public Date getDecisionWindowDate() {
		return decisionWindowDate;
	}

	/**
	 * @param decisionWindowDate the decisionWindowDate to set
	 */
	public void setDecisionWindowDate(Date decisionWindowDate) {
		this.decisionWindowDate = decisionWindowDate;
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

package com.westernalliancebancorp.positivepay.model;

/**
 * User: gduggirala
 * Date: 3/12/14
 * Time: 12:59 PM
 */

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;


@EntityListeners(AuditListener.class)
@Entity
@Table(name = "CHECK_DETAIL_EXCEPTION")
public class ExceptionalCheck implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;	
	public enum CHECK_STATUS {
        ISSUED {
            public String toString() {
                return "ISSUED";
            }
        },
        VOID { 
            public String toString() {
                return "VOID";
            }
        }
    }
	
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ACCOUNT_NUMBER", length = 100, nullable = false)
    private String accountNumber;

    @Column(name = "ROUTING_NUMBER", length = 20, nullable = true)
    private String routingNumber;
    
    @Column(name = "CHECK_NUMBER", length = 20, nullable = false)
    private String checkNumber;
    
    @Column(name = "ISSUE_CODE", length = 20, nullable = false)
    private String issueCode;
    
    @Column(name = "ISSUE_DATE", length = 20, nullable = false)
    private String issueDate;
    
    @Column(name = "ISSUED_AMOUNT", length = 20, nullable = false)
	private String issuedAmount;
    
    @Column(name = "PAYEE", length = 255, nullable = true)
    private String payee;
    
    @Column(name = "FILE_IMPORT_LINE_NUMBER", length = 20, nullable = true)
    private String lineNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCEPTION_STATUS_ID", nullable = false)
    private ExceptionStatus exceptionStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "CHECK_STATUS", nullable = true)
    private CHECK_STATUS checkStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_META_DATA_ID", nullable = true)
    private FileMetaData fileMetaData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXCEPTION_TYPE_ID", nullable = true)
    private ExceptionType exceptionType;

    public CHECK_STATUS getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(CHECK_STATUS checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(String issueCode) {
        this.issueCode = issueCode;
    }

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getIssuedAmount() {
		return issuedAmount;
	}

	public void setIssuedAmount(String issuedAmount) {
		this.issuedAmount = issuedAmount;
	}

	public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public ExceptionStatus getExceptionStatus() {
		return exceptionStatus;
	}

	public void setExceptionStatus(ExceptionStatus exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}

	public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }

    public void setFileMetaData(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

    public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
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

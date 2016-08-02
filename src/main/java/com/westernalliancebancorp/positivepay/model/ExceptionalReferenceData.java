package com.westernalliancebancorp.positivepay.model;

/**
 * Class representing exceptional or bad reference data
 * @author Anand Kumar
 */

import java.io.Serializable;

import javax.persistence.*;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "REFERENCE_DATA_EXCEPTION")
public class ExceptionalReferenceData implements Serializable, Auditable {
	private static final long serialVersionUID = 1L;
	public enum EXCEPTION_STATUS {
    	OPEN {
            public String toString() {
                return "OPEN";
            }
        },
        CLOSE {
            public String toString() {
                return "CLOSE";
            }
        }
    }

    public enum EXCEPTION_TYPE {
        DUPLICATE_DATA_IN_DB {
            public String toString() {
                return "DUPLICATE_DATA_IN_DB";
            }
        },
        DUPLICATE_DATA_IN_FILE {
            public String toString() {
                return "DUPLICATE_DATA_IN_FILE";
            }
        },
        ACCOUNT_NOT_FOUND {
            public String toString() {
                return "ACCOUNT_NOT_FOUND";
            }
        },
        DATA_IN_WRONG_FORMAT {
            public String toString() {
                return "DATA_IN_WRONG_FORMAT";
            }
        },
        WRONG_ITEM_TYPE {
            public String toString() {
                return "WRONG_ITEM_TYPE";
            }
        },
        ZERO_NUMBERED_CHECK {
            public String toString() {
                return "ZERO_NUMBERED_CHECK";
            }
        }
    }
	
	@javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "ASSIGNED_BANK_NUMBER", nullable = false)
    private Short assignedBankNumber;

    @Column(name = "AMOUNT", length = 25, nullable = false)
    private String amount;

    @Column(name = "CHECK_NUMBER", length = 75, nullable = false)
    private String checkNumber;

    @Column(name = "TRACE_NUMBER", length = 100, nullable = true)
    private String traceNumber;

    @Column(name = "PAID_DATE", length = 25, nullable = true)
    private String paidDate;

    @Column(name = "STOP_DATE", length = 25, nullable = true)
    private String stopDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_TYPE", length = 20, nullable = false)
    private ReferenceData.ITEM_TYPE itemType;

    @Column(name = "FILE_IMPORT_LINE_NUMBER", length = 20, nullable = false)
    private String lineNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "EXCEPTION_STATUS", length = 50, nullable = false)
    private EXCEPTION_STATUS exceptionStatus;

    @Column(name = "ACCOUNT_NUMBER", length = 20,nullable = false)
    private String accountNumber;
    
    @Column(name = "STOP_PRESENTED_DATE", length = 25, nullable = true)
    private String stopPresentedDate;
    
    @Column(name = "STOP_PRESENTED_REASON", length = 255, nullable = true)
    private String stopPresentedReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_META_DATA_ID", nullable = true)
    private FileMetaData fileMetaData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EXCEPTION_TYPE_ID", nullable = true)
    private ExceptionType exceptionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REFERENCE_DATA_ID", nullable = true)
    private ReferenceData referenceData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Short getAssignedBankNumber() {
		return assignedBankNumber;
	}

	public void setAssignedBankNumber(Short assignedBankNumber) {
		this.assignedBankNumber = assignedBankNumber;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	public String getTraceNumber() {
		return traceNumber;
	}

	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getStopDate() {
		return stopDate;
	}

	public void setStopDate(String stopDate) {
		this.stopDate = stopDate;
	}

    public ReferenceData.ITEM_TYPE getItemType() {
        return itemType;
    }

    public void setItemType(ReferenceData.ITEM_TYPE itemType) {
        this.itemType = itemType;
    }

    public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public EXCEPTION_STATUS getExceptionStatus() {
		return exceptionStatus;
	}

	public void setExceptionStatus(EXCEPTION_STATUS exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getStopPresentedDate() {
		return stopPresentedDate;
	}

	public void setStopPresentedDate(String stopPresentedDate) {
		this.stopPresentedDate = stopPresentedDate;
	}

	public String getStopPresentedReason() {
		return stopPresentedReason;
	}

	public void setStopPresentedReason(String stopPresentedReason) {
		this.stopPresentedReason = stopPresentedReason;
	}

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public ReferenceData getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(ReferenceData referenceData) {
        this.referenceData = referenceData;
    }

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    /*public EXCEPTION_TYPE getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(EXCEPTION_TYPE exceptionType) {
        this.exceptionType = exceptionType;
    }*/

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }

    public void setFileMetaData(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

    @Override
	public String toString() {
        return new StringBuilder().append("ExceptionalReferenceData [id=").
                append(id).append(", amount=")
                .append(amount)
                .append(", checkNumber=")
                .append(checkNumber)
                .append(", traceNumber=")
                .append(traceNumber)
                .append(", paidDate=")
                .append(paidDate)
                .append(", stopDate=")
                .append(stopDate)
                .append(", itemType=")
                .append(itemType)
                .append(", lineNumber=")
                .append(lineNumber)
                .append(", exceptionStatus=")
                .append(exceptionStatus.name())
                .append(", accountNumber=")
                .append(accountNumber)
                .append(", stopPresentedDate=")
                .append(stopPresentedDate)
                .append(", stopPresentedReason=")
                .append(stopPresentedReason)
                .append(", auditInfo=")
                .append(auditInfo)
                .append(", assignedBankNumber=")
                .append(assignedBankNumber)
                .append("]").toString();
    }
}

package com.westernalliancebancorp.positivepay.model;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/18/14
 * Time: 10:52 PM
 */
@Table(name = "REFERENCE_DATA", uniqueConstraints = {@UniqueConstraint(columnNames = { "CHECK_NUMBER", "ACCOUNT_ID", "ITEM_TYPE" })})
@EntityListeners(AuditListener.class)
@Entity
public class ReferenceData implements Auditable {

    public enum STATUS{
        PROCESSED{
            public String toString() {
                return "PROCESSED";
            }
        },
        NOT_PROCESSED{
            public String toString() {
                return "NOT_PROCESSED";
            }
        },
        DUPLICATE_EXCEPTION {
            public String toString() {
                return "DUPLICATE_EXCEPTION";
            }
        },
        DELETED{
            public String toString() {
                return "DELETED";
            }
        }
    }

    public enum ITEM_TYPE {
        PAID {
            private String code;
            public String toString() {
                return "PAID";
            }
            public String getCode() {
                return "P";
            }
        },
        STOP {
            private String code;
            public String toString() {
                return "STOP";
            }

        },
        STOP_PRESENTED {
            private String code;
            public String toString() {
                return "STOP_PRESENTED";
            }
            public String getCode() {
                return "S";
            }
        }
    }

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "ASSIGNED_BANK_NUMBER", nullable = true)
    private Short assignedBankNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID", updatable = true, nullable = false)
    private Account account;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "CHECK_NUMBER", length = 75, nullable = false)
    private String checkNumber;

    @Column(name = "TRACE_NUMBER", length = 100, nullable = true)
    private String traceNumber;

    @Column(name = "PAID_DATE", nullable = true)
    private Date paidDate;

    @Column(name = "STOP_DATE", nullable = true)
    private Date stopDate;
    
    @Column(name = "DUPLICATE_IDENTIFIER", nullable = true)
	private String digest;

    @OneToMany(mappedBy = "referenceData", targetEntity = CheckHistory.class)
    private Set<CheckHistory> checkHistorySet = new HashSet<CheckHistory>();

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 25, nullable = true)
    private STATUS status;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_TYPE", length = 20, nullable = false)
    private ITEM_TYPE itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_META_DATA_ID", nullable = true)
    private FileMetaData fileMetaData;

    @Column(name = "FILE_IMPORT_LINE_NUMBER", length = 20, nullable = true)
    private String lineNumber;
    

    @Column(name = "STOP_PRESENTED_DATE", nullable = true)
    private Date stopPresentedDate;
    
    @Column(name = "STOP_PRESENTED_REASON", nullable = true)
    private String stopPresentedReason;

    @OneToMany(mappedBy = "referenceData", targetEntity = ExceptionalReferenceData.class)
    private Set<ExceptionalReferenceData> exceptionalReferenceDatas  = new HashSet<ExceptionalReferenceData>();

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();
    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
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

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public Set<CheckHistory> getCheckHistorySet() {
        return checkHistorySet;
    }

    public void setCheckHistorySet(Set<CheckHistory> checkHistorySet) {
        this.checkHistorySet = checkHistorySet;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public ITEM_TYPE getItemType() {
        return itemType;

    }

    public void setItemType(ITEM_TYPE itemType) {
        this.itemType = itemType;
    }

    public FileMetaData getFileMetaData() {
        return fileMetaData;
    }

    public void setFileMetaData(FileMetaData fileMetaData) {
        this.fileMetaData = fileMetaData;
    }

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Date getStopPresentedDate() {
		return stopPresentedDate;
	}

	public void setStopPresentedDate(Date stopPresentedDate) {
		this.stopPresentedDate = stopPresentedDate;
	}
	
	public String getStopPresentedReason() {
		return stopPresentedReason;
	}

	public void setStopPresentedReason(String stopPresentedReason) {
		this.stopPresentedReason = stopPresentedReason;
	}

    public Set<ExceptionalReferenceData> getExceptionalReferenceDatas() {
        return exceptionalReferenceDatas;
    }

    public void setExceptionalReferenceDatas(Set<ExceptionalReferenceData> exceptionalReferenceDatas) {
        this.exceptionalReferenceDatas = exceptionalReferenceDatas;
    }

    @Override
    public String toString() {
        return "ReferenceData{" +
                "id=" + id +
                ", assignedBankNumber=" + assignedBankNumber +
                ", amount=" + amount +
                ", traceNumber='" + traceNumber + '\'' +
                ", digest='" + digest + '\'' +
                ", checkNumber='" + checkNumber + '\'' +
                ", itemType=" + itemType.name() +
                ", status=" + status.name() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceData that = (ReferenceData) o;

        if (!digest.equals(that.digest)) return false;
        if (itemType != that.itemType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = digest.hashCode();
        result = 31 * result + itemType.hashCode();
        return result;
    }
}

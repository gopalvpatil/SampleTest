/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing a check
 * @author Anand Kumar
 *
 */
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "CHECK_DETAIL", uniqueConstraints = {@UniqueConstraint(columnNames = { "CHECK_NUMBER", "ACCOUNT_ID" })})
public class Check implements Serializable, Auditable {
	
	private static final long serialVersionUID = -8201515362298788164L;
	
	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_STATUS_ID", updatable = true)
    private CheckStatus checkStatus;
	
	@Column(name = "ROUTING_NUMBER", length = 20, nullable = true)
	private String routingNumber;
	
	@Column(name = "CHECK_NUMBER", length = 75, nullable = false)
	private String checkNumber;
	
	@Column(name = "ISSUE_DATE", nullable = true)
	private Date issueDate;
	
    @Column(name = "VOID_DATE", nullable = true)
    private Date voidDate;
    
	@Column(name = "ISSUED_AMOUNT", precision = 19, scale = 2, nullable = true)
	private BigDecimal issuedAmount;

	@Column(name = "VOID_AMOUNT", precision = 19, scale = 2, nullable = true)
	private BigDecimal voidAmount;
	
	@Column(name = "PAYEE", length = 255, nullable = true)
	private String payee;

    @Column(name = "DUPLICATE_IDENTIFIER", length = 255, nullable = true)
	private String digest;

    @Column(name = "MATCH_STATUS", length = 20, nullable = true)
	private String matchStatus;

    @Column(name = "STALE_DATE", nullable = true)
    private Date staleDate;
    
	@OneToMany(mappedBy = "check", targetEntity = CheckHistory.class)
    private Set<CheckHistory> checkHistorySet = new HashSet<CheckHistory>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "WORKFLOW_ID", updatable = false, nullable = false)
    private Workflow workflow;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTION_ID", updatable = true, nullable = true) //Column nullable in Positive Pay database
    private Action action;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ACCOUNT_ID", updatable = true, nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_META_DATA_ID", nullable = true)
    private FileMetaData fileMetaData;

    @OneToOne
    @JoinColumn(name = "REFERENCE_DATA_ID", nullable = true)
    private ReferenceData referenceData;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", nullable = true)
    private Check parentCheck;
    
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCheck", orphanRemoval = true)
    private Set<Check> childChecks = new HashSet<Check>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "check", orphanRemoval = true)
    private Set<CheckDetailComment> comments = new HashSet<CheckDetailComment>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_DETAIL_LINKAGE_TYPE_ID", nullable = true)
    private LinkageType linkageType;

    @Column(name = "FILE_IMPORT_LINE_NUMBER", length = 20, nullable = true)
    private String lineNumber;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ITEM_TYPE_ID", nullable = false)
    private ItemType itemType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EXCEPTION_TYPE_ID", nullable = true)
    private ExceptionType exceptionType;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EXCEPTION_STATUS_ID", nullable = true)
    private ExceptionStatus exceptionStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "check", orphanRemoval = true)
    private Set<AdjustmentCheck> adjustmentChecks = new HashSet<AdjustmentCheck>();

    @Column(name = "PAYMENT_STATUS", length = 20, nullable = true)
    private String paymentStatus;

    @Column(name = "EXCEPTION_TYPE_CREATION_DATE", nullable = true)
    private Date exceptionCreationDate;

    @Column(name = "EXCEPTION_TYPE_RESOLVED_DATE", nullable = true)
    private Date exceptionResolvedDate;

	public String getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(String lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	

	public ExceptionStatus getExceptionStatus() {
	    return exceptionStatus;
	}

	public void setExceptionStatus(ExceptionStatus exceptionStatus) {
	    this.exceptionStatus = exceptionStatus;
	}

	public Check getParentCheck() {
		return parentCheck;
	}
	
	public void setParentCheck(Check parentCheck) {
		this.parentCheck = parentCheck;
	}
	
	public Set<Check> getChildChecks() {
		return childChecks;
	}
	
	public void setChildChecks(Set<Check> childChecks) {
		this.childChecks = childChecks;
	}

	public Set<CheckDetailComment> getComments() {
		return comments;
	}
	
	public void setComments(Set<CheckDetailComment> comments) {
		this.comments = comments;
	}

	public LinkageType getLinkageType() {
		return linkageType;
	}
	
	public void setLinkageType(LinkageType linkageType) {
		this.linkageType = linkageType;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public CheckStatus getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(CheckStatus checkStatus) {
		this.checkStatus = checkStatus;
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

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public BigDecimal getIssuedAmount() {
		return issuedAmount;
	}

	public void setIssuedAmount(BigDecimal issuedAmount) {
		this.issuedAmount = issuedAmount;
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public Set<CheckHistory> getCheckHistorySet() {
		return checkHistorySet;
	}

	public void setCheckHistorySet(Set<CheckHistory> checkHistorySet) {
		this.checkHistorySet = checkHistorySet;
	}

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getMatchStatus() {
		return matchStatus;
	}

	public void setMatchStatus(String matchStatus) {
		this.matchStatus = matchStatus;
	}

	public Date getStaleDate() {
		return staleDate;
	}

	public void setStaleDate(Date staleDate) {
		this.staleDate = staleDate;
	}
	
	public FileMetaData getFileMetaData() {
		return fileMetaData;
	}

	public void setFileMetaData(FileMetaData fileMetaData) {
		this.fileMetaData = fileMetaData;
	}

    public ReferenceData getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(ReferenceData referenceData) {
        this.referenceData = referenceData;
    }

    public Date getVoidDate() {
        return voidDate;
    }

    public void setVoidDate(Date voidDate) {
        this.voidDate = voidDate;
    }
    

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public BigDecimal getVoidAmount() {
        return voidAmount;
    }

    public void setVoidAmount(BigDecimal voidAmount) {
        this.voidAmount = voidAmount;
    }
    
    public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}

    public Set<AdjustmentCheck> getAdjustmentChecks() {
        return adjustmentChecks;
    }

    public void setAdjustmentChecks(Set<AdjustmentCheck> adjustmentChecks) {
        this.adjustmentChecks = adjustmentChecks;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getExceptionCreationDate() {
        return exceptionCreationDate;
    }

    public void setExceptionCreationDate(Date exceptionCreationDate) {
        this.exceptionCreationDate = exceptionCreationDate;
    }

    public Date getExceptionResolvedDate() {
        return exceptionResolvedDate;
    }

    public void setExceptionResolvedDate(Date exceptionResolvedDate) {
        this.exceptionResolvedDate = exceptionResolvedDate;
    }

    @Override
    public String toString() {
        return "Check{" +
                "id=" + id +
                ", routingNumber='" + routingNumber + '\'' +
                ", checkNumber='" + checkNumber + '\'' +
                ", issueDate=" + issueDate + '\'' +
                ", issuedAmount=" + issuedAmount +
                ", payee='" + payee + '\'' +
                ", digest='" + digest + '\'' +
                '}';
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((digest == null) ? 0 : digest.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Check other = (Check) obj;
		if (digest == null) {
			if (other.digest != null)
				return false;
		} else if (!digest.equals(other.digest))
			return false;
		return true;
	}

	@Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public enum MATCH_STATUS{
        MATCHED("Matched"), 
        NOTMATCHED("Not Matched");
        
        private String name;
        
        MATCH_STATUS(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

       
    }


   }
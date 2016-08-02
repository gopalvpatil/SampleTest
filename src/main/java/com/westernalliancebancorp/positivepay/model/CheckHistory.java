/**
 *
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing the history of a check
 *
 * @author Anand Kumar
 */
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "CHECK_DETAIL_HISTORY")
public class CheckHistory implements Serializable, Auditable, Comparable<CheckHistory> {

    private static final long serialVersionUID = -2221215362298788164L;

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "ROUTING_NUMBER", length = 20, nullable = true)
    private String routingNumber;
    
    @Column(name = "CHECK_NUMBER", length = 75, nullable = false)
    private String checkNumber;

    @Column(name = "ISSUE_DATE", nullable = true)
    private Date issueDate;

    @Column(name = "CHECK_AMOUNT", precision = 19, scale = 2, nullable = false)
    private BigDecimal checkAmount;

    @Column(name = "ISSUED_AMOUNT", precision = 19, scale = 2, nullable = true)
    private BigDecimal issuedAmount;
    
    @Column(name = "PAYEE", length = 255, nullable = true)
    private String payee;

    @Column(name = "SYSTEM_COMMENT", length = 255, nullable = true)
    private String systemComment;
    
    @Column(name = "USER_COMMENT", length = 255, nullable = true)
    private String userComment;

    @Column(name = "MATCH_STATUS", length = 20, nullable = true)
    private String matchStatus;

    @Column(name = "STALE_DATE", nullable = true)
    private Date staleDate;

    @Column(name = "VOID_DATE", nullable = true)
    private Date voidDate;

    @Column(name = "VOID_AMOUNT", precision = 19, scale = 2, nullable = true)
    private BigDecimal voidAmount;
    
    @Column(name = "SOURCE", length = 20, nullable = false)
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_STATUS_ID", updatable = true, nullable = true)
    private CheckStatus checkStatus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FORMER_CHECK_STATUS_ID", updatable = false)
    private CheckStatus formerCheckStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_CHECK_STATUS_ID", updatable = false, nullable = false)
    private CheckStatus targetCheckStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_DETAIL_ID", updatable = false, nullable = false)
    private Check check;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTION_ID", updatable = false, nullable = false)
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REFERENCE_DATA_ID", updatable = true, nullable = true)
    private ReferenceData referenceData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHECK_DETAIL_LINKAGE_TYPE_ID", nullable = true)
    private LinkageType linkageType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", updatable = false, nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ITEM_TYPE_ID", nullable = false)
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EXCEPTION_TYPE_ID", nullable = true)
    private ExceptionType exceptionType;

    @Column(name = "PAYMENT_STATUS", length = 20, nullable = true)
    private String paymentStatus;

    @Column(name = "EXCEPTION_TYPE_CREATION_DATE", nullable = true)
    private Date exceptionCreationDate;

    @Column(name = "EXCEPTION_TYPE_RESOLVED_DATE", nullable = true)
    private Date exceptionResolvedDate;

    @OneToOne
    @JoinColumn(name = "CHECK_DETAIL_ADJUSTMENT_ID", nullable = true)
    private AdjustmentCheck adjustmentCheck;

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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getCheckAmount() {
		return checkAmount;
	}

	public void setCheckAmount(BigDecimal checkAmount) {
		this.checkAmount = checkAmount;
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
    

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public CheckStatus getFormerCheckStatus() {
		return formerCheckStatus;
	}

	public void setFormerCheckStatus(CheckStatus formerCheckStatus) {
		this.formerCheckStatus = formerCheckStatus;
	}

	public CheckStatus getTargetCheckStatus() {
		return targetCheckStatus;
	}

	public void setTargetCheckStatus(CheckStatus targetCheckStatus) {
		this.targetCheckStatus = targetCheckStatus;
	}

	public CheckStatus getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(CheckStatus checkStatus) {
		this.checkStatus = checkStatus;
	}

	public String getSystemComment() {
        return systemComment;
    }

    public void setSystemComment(String systemComment) {
        this.systemComment = systemComment;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public Check getCheck() {
        return check;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void setCheck(Check check) {
        this.check = check;
    }

    public Date getStaleDate() {
        return staleDate;
    }

    public void setStaleDate(Date staleDate) {
        this.staleDate = staleDate;
    }

    public Date getVoidDate() {
        return voidDate;
    }

    public void setVoidDate(Date voidDate) {
        this.voidDate = voidDate;
    }

    public BigDecimal getVoidAmount() {
        return voidAmount;
    }

    public void setVoidAmount(BigDecimal voidAmount) {
        this.voidAmount = voidAmount;
    }

    public AdjustmentCheck getAdjustmentCheck() {
        return adjustmentCheck;
    }

    public void setAdjustmentCheck(AdjustmentCheck adjustmentCheck) {
        this.adjustmentCheck = adjustmentCheck;
    }

    /* (non-Javadoc)
                 * @see java.lang.Object#toString()
                 */
    @Override
    public String toString() {
        return "CheckHistory [id=" + id + ", checkStatus="
                + formerCheckStatus + ", check=" + check + ", systemComment="
                + systemComment + ", userComment=" + userComment
                + ", auditInfo=" + auditInfo + "]";
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public ReferenceData getReferenceData() {
        return referenceData;
    }

    public void setReferenceData(ReferenceData referenceData) {
        this.referenceData = referenceData;
    }

    public ExceptionType getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(ExceptionType exceptionType) {
		this.exceptionType = exceptionType;
	}

    public LinkageType getLinkageType() {
        return linkageType;
    }

    public void setLinkageType(LinkageType linkageType) {
        this.linkageType = linkageType;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
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

    @Embedded
    private AuditInfo auditInfo = new AuditInfo();

    public AuditInfo getAuditInfo() {
        return auditInfo;
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }

	@Override
	public int compareTo(CheckHistory o) {
		return getAuditInfo().getDateCreated().compareTo(o.getAuditInfo().getDateCreated());
	}
}

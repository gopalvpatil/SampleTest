/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 * Class representing File Mapping for a user
 * @author Anand Kumar
 *
 */
@XmlRootElement(name = "filemapping")
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "FILE_MAPPING")
@JsonIgnoreProperties({ "userDetail", "fileMetaDatas" })
public class FileMapping implements Serializable, Auditable {
	private static final long serialVersionUID = -8201515362298788164L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "ROUTING_NUMBER_POSITION", length = 20, nullable = false)
	private String routingNumberPosition;
	
	@Column(name = "ACCOUNT_NUMBER_POSITION", length = 20, nullable = false)
	private String accountNumberPosition;
	
	@Column(name = "CHECK_NUMBER_POSITION", length = 20, nullable = false)
	private String checkNumberPosition;
	
	@Column(name = "ISSUE_CODE_POSITION", length = 20, nullable = false)
	private String issueCodePosition;
	
	@Column(name = "ISSUE_DATE_POSITION", length = 20, nullable = false)
	private String issueDatePosition;
	
	@Column(name = "CHECK_AMOUNT_POSITION", length = 20, nullable = false)
	private String checkAmountPosition;
	
	@Column(name = "PAYEE_POSITION", length = 20, nullable = false)
	private String payeePosition;
	
	@Column(name = "FILE_TYPE", length = 20, nullable = false)
	private String fileType;
	
	@Column(name = "FILE_MAPPING_NAME", length = 255, nullable = false)
	private String fileMappingName;
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

	@ManyToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "DELIMITER_ID", nullable = true)
	private Delimiter delimiter;
	
	@OneToMany(mappedBy = "fileMapping", targetEntity = FileMetaData.class)
    private Set<FileMetaData> fileMetaDatas = new HashSet<FileMetaData>();
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoutingNumberPosition() {
		return routingNumberPosition;
	}

	public void setRoutingNumberPosition(String routingNumberPosition) {
		this.routingNumberPosition = routingNumberPosition;
	}

	public String getAccountNumberPosition() {
		return accountNumberPosition;
	}

	public void setAccountNumberPosition(String accountNumberPosition) {
		this.accountNumberPosition = accountNumberPosition;
	}

	public String getCheckNumberPosition() {
		return checkNumberPosition;
	}

	public void setCheckNumberPosition(String checkNumberPosition) {
		this.checkNumberPosition = checkNumberPosition;
	}

	public String getIssueCodePosition() {
		return issueCodePosition;
	}

	public void setIssueCodePosition(String issueCodePosition) {
		this.issueCodePosition = issueCodePosition;
	}
	
	public String getIssueDatePosition() {
		return issueDatePosition;
	}

	public void setIssueDatePosition(String issueDatePosition) {
		this.issueDatePosition = issueDatePosition;
	}

	public String getCheckAmountPosition() {
		return checkAmountPosition;
	}

	public void setCheckAmountPosition(String checkAmountPosition) {
		this.checkAmountPosition = checkAmountPosition;
	}

	public String getPayeePosition() {
		return payeePosition;
	}

	public void setPayeePosition(String payeePosition) {
		this.payeePosition = payeePosition;
	}
	
	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileMappingName() {
		return fileMappingName;
	}

	public void setFileMappingName(String fileMappingName) {
		this.fileMappingName = fileMappingName;
	}
	
	public Delimiter getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(Delimiter delimiter) {
		this.delimiter = delimiter;
	}

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Set<FileMetaData> getFileMetaDatas() {
		return fileMetaDatas;
	}

	public void setFileMetaDatas(Set<FileMetaData> fileMetaDatas) {
		this.fileMetaDatas = fileMetaDatas;
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

/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;


/**
 * Class representing file meta data
 * @author Anand Kumar
 *
 */
@XmlRootElement(name = "filemetadata")
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "FILE_META_DATA")
public class FileMetaData implements Serializable, Auditable {
    public enum STATUS {
        PROCESSED("Processed"), UNPROCESSED("Unprocessed");
        private String description;

        STATUS(String description) {
            this.description = description;
        }

        public String getName() {
            return this.name();
        }

        public String getDescription() {
            return this.description;
        }

        public String value() { return this.name(); }
    };

    public static final String EXCEPTIONAL_REFERENCE_DATA_FILE_NAME = "EXCEPTIONAL_REFERENCE_DATA_FILE_NAME";
    public static final String EXCEPTIONAL_REFERENCE_DATA_ORIGINAL_FILE_NAME = "MEXCEPTIONAL_REFERENCE_DATA_ORIGINAL_FILE";
    public static final String EXCEPTIONAL_REFERENCE_DATA_UPLOAD_DIRECTORY = "N/A";
    public static final String MANUAL_ENTRY_FILE_NAME = "MANUAL_ENTRY_FILE";
    public static final String MANUAL_ENTRY_ORIGINAL_FILE_NAME = "MANUAL_ENTRY_ORIGINAL_FILE";
    public static final String MIGRATED_FILE_NAME = "MIGRATED_FILE";
    public static final String MIGRATED_ORIGINAL_FILE_NAME = "MIGRATED_ORIGINAL_FILE";
    public static final String MANUAL_ENTRY_UPLOAD_DIRECTORY = "N/A";
    public static final String MANUAL_ENTRY_CHECKSUM = "CHECKSUM";
    public static final String EXCEPTIONAL_REFERENCE_DATA_CHECKSUM = "CHECKSUM";
	private static final long serialVersionUID = -8201515362298788164L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "FILE_NAME", length = 255, nullable = false)
	private String fileName;
	
	@Column(name = "UPLOAD_DIRECTORY", length = 255, nullable = false)
	private String uploadDirectory;
	
	@Column(name = "FILE_SIZE", nullable = false)
	private long fileSize;
	
	@Column(name = "ORIGINAL_FILE_NAME", length = 255, nullable = false)
	private String originalFileName;
	
	@Column(name = "items_received", nullable = false)
	private Long itemsReceived;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 20, nullable = false)
	private STATUS status;

    @Column(name = "CHECKSUM", length = 255, nullable = false)
	private String checksum;
	
	@OneToMany(mappedBy = "fileMetaData", targetEntity = Check.class)
    private Set<Check> checks = new HashSet<Check>();

    @OneToMany(mappedBy = "fileMetaData", targetEntity = ReferenceData.class)
    private Set<ReferenceData> referenceDatas = new HashSet<ReferenceData>();

    @OneToMany(mappedBy = "fileMetaData", targetEntity = Check.class)
    private Set<ExceptionalCheck> exceptionalChecks= new HashSet<ExceptionalCheck>();

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_MAPPING_ID", nullable = true)
    private FileMapping fileMapping;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FILE_TYPE_ID", nullable = false)
    private FileType fileType;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUploadDirectory() {
		return uploadDirectory;
	}

	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public Set<Check> getChecks() {
		return checks;
	}

	public void setChecks(Set<Check> checks) {
		this.checks = checks;
	}

	public FileMapping getFileMapping() {
		return fileMapping;
	}

	public void setFileMapping(FileMapping fileMapping) {
		this.fileMapping = fileMapping;
	}

    public Set<ReferenceData> getReferenceDatas() {
        return referenceDatas;
    }

    public void setReferenceDatas(Set<ReferenceData> referenceDatas) {
        this.referenceDatas = referenceDatas;
    }

    public Set<ExceptionalCheck> getExceptionalChecks() {
        return exceptionalChecks;
    }

    public void setExceptionalChecks(Set<ExceptionalCheck> exceptionalChecks) {
        this.exceptionalChecks = exceptionalChecks;
    }

	public Long getItemsReceived() {
		return itemsReceived;
	}

	public void setItemsReceived(Long itemsReceived) {
		this.itemsReceived = itemsReceived;
	}
	
	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
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

package com.westernalliancebancorp.positivepay.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;

/**
 *
 * @author Moumita Ghosh
 */

@javax.persistence.Table(name = "FILE_TYPE")
@EntityListeners(AuditListener.class)
@Entity
public class FileType implements Auditable{
    public enum FILE_TYPE {
    	MANUAL_ENTRY {
            public String toString() {
                return "Manual Entry";
            }
        },
        EXCEPTIONAL_REFERENCE_DATA {
            public String toString() {
                return "Exceptional Reference Data";
            }
        },
        NO_FILE_TYPE {
            public String toString() {
                return "Manual Entry";
            }
        },
        CRS_PAID {
            public String toString() {
                return "CRS paid file";
            }
        },
        DAILY_STOP {
            public String toString() {
                return "Daily stop file";
            }
        },
        STOP_PRESENTED {
            public String toString() {
                return "stop presented file";
            }
        },
        CUSTOMER_UPLOAD {
            public String toString() {
                return "Customer uploaded file";
            }
        }
    }

    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "DESCRIPTION", length = 255, nullable = true)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", length = 50, nullable = true)
    private FILE_TYPE name;

    @OneToMany(mappedBy = "fileType", targetEntity = FileMetaData.class)
    private Set<FileMetaData> fileMetaDataSet = new HashSet<FileMetaData>();
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public FILE_TYPE getName() {
		return name;
	}

	public void setName(FILE_TYPE name) {
		this.name = name;
	}

	public Set<FileMetaData> getFileMetaDataSet() {
		return fileMetaDataSet;
	}

	public void setFileMetaDataSet(Set<FileMetaData> fileMetaDataSet) {
		this.fileMetaDataSet = fileMetaDataSet;
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

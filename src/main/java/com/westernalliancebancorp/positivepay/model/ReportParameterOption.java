package com.westernalliancebancorp.positivepay.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * User: gduggirala
 * Date: 17/5/14
 * Time: 1:30 PM
 */
@Table(name = "REPORT_PARAMETER_OPTION")
@Entity
public class ReportParameterOption implements Comparable<ReportParameterOption>{
    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "NAME", unique = true, length = 50, nullable = false)
    private String name;

    @Column(name = "DISPLAY_NAME", unique = true, length = 50, nullable = false)
    private String displayName;

    @Column(name = "DATA_TYPE", length = 20, nullable = false)
    private String dataType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "REPORT_TEMPLATE_PARAMETER_OPTION", joinColumns = {
            @JoinColumn(name = "REPORT_PARAMETER_OPTION_ID", nullable = false, updatable = true)},
            inverseJoinColumns = {
                    @JoinColumn(name = "REPORT_TEMPLATE_ID", nullable = false, updatable = true)
            })
    private Set<ReportTemplate> reportTemplates = new HashSet<ReportTemplate>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    /**
     *
     * @param reportParameterOption
     */
    public int compareTo(ReportParameterOption reportParameterOption) {
        int compareValue = 0;
        if (this.name != null && reportParameterOption != null && reportParameterOption.getName() != null) {
            compareValue = this.name.compareTo(reportParameterOption.getName());
        }
        return compareValue;
    }
}

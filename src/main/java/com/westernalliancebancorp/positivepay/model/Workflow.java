package com.westernalliancebancorp.positivepay.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Workflow is
 *
 * @author Giridhar Duggirala
 */
@javax.persistence.Table(name = "WORKFLOW")
@Entity
public class Workflow {
    @javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "NAME", length = 50, nullable = false)
    private String name;

    @javax.persistence.Column(name = "VERSION")
    private Integer version;

    @OneToMany(mappedBy = "workflow", targetEntity = Check.class)
    private Set<Check> checks = new HashSet<Check>();

    @Lob
    @Type(type = "text")
    @javax.persistence.Column(name = "XML", length = Integer.MAX_VALUE)
    private String xml;

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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Set<Check> getChecks() {
        return checks;
    }

    public void setChecks(Set<Check> checks) {
        this.checks = checks;
    }
}

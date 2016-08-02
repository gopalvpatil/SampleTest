/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;
import com.westernalliancebancorp.positivepay.model.interceptor.Auditable;


/**
 * Class representing a Delimiter
 * @author Anand Kumar
 *
 */
@XmlRootElement(name = "delimiter")
@EntityListeners(AuditListener.class)
@Entity
@Table(name = "DELIMITER")
public class Delimiter implements Serializable, Auditable {

	private static final long serialVersionUID = 1L;
	public static final String COMMA = ",";
	public static final String SINGLE_SPACE = " ";
	public static final String DOUBLE_SPACE = "  ";
	public static final String PIPE = "|";

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
	
	@Column(name = "NAME", length = 50, nullable = false)
	private String name;

	@Column(name = "VALUE", length = 20, nullable = false)
	private String value;
	
	@Column(name = "SYMBOL", length = 10, nullable = false)
	private String symbol;
		
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
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

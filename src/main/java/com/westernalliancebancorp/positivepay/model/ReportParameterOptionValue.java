package com.westernalliancebancorp.positivepay.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;

@javax.persistence.Table(name = "REPORT_PARAMETER_OPTION_VALUE")
@EntityListeners(AuditListener.class)
@Entity
public class ReportParameterOptionValue {
	@javax.persistence.Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;

    @javax.persistence.Column(name = "OPERATOR", length = 50, nullable = true)
    private String operator;
	
    @javax.persistence.Column(name = "VALUE_CHAR", length = 50, nullable = true)
    private String valueChar;

    @javax.persistence.Column(name = "VALUE_DATE_START", nullable = true)
    @Type(type="date")
    private Date valueDateStart;

    @javax.persistence.Column(name = "VALUE_DATE_END", nullable = true)
    @Type(type="date")
    private Date valueDateEnd;

    @javax.persistence.Column(name = "VALUE_DATE_IS_SYMBOLIC", nullable = true)
    private Boolean valueDateIsSymbolic;

    @javax.persistence.Column(name = "VALUE_DATE_START_SYMBOLIC_VALUE", length = 50, nullable = true)
    private String valueDateStartSymbolicValue;

    @javax.persistence.Column(name = "VALUE_DATE_END_SYMBOLIC_VALUE", length = 50, nullable = true)
    private String valueDateEndSymbolicValue;

    @ManyToOne
    @JoinColumn(name = "REPORT_ID", nullable = false)
    private Report report;

    @OneToOne
    @JoinColumn(name = "REPORT_PARAMETER_OPTION_ID", nullable = false)
    private ReportParameterOption reportParameterOption;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValueChar() {
		return valueChar;
	}

	public void setValueChar(String valueChar) {
		this.valueChar = valueChar;
	}

	public Date getValueDateStart() {
		return valueDateStart;
	}

	public void setValueDateStart(Date valueDateStart) {
		this.valueDateStart = valueDateStart;
	}

	public Date getValueDateEnd() {
		return valueDateEnd;
	}

	public void setValueDateEnd(Date valueDateEnd) {
		this.valueDateEnd = valueDateEnd;
	}

	public Boolean isValueDateSymbolic() {
		return valueDateIsSymbolic;
	}

	public void setValueDateIsSymbolic(Boolean valueDateIsSymbolic) {
		this.valueDateIsSymbolic = valueDateIsSymbolic;
	}

	public String getValueDateStartSymbolicValue() {
		return valueDateStartSymbolicValue;
	}

	public void setValueDateStartSymbolicValue(String valueDateStartSymbolicValue) {
		this.valueDateStartSymbolicValue = valueDateStartSymbolicValue;
	}

	public String getValueDateEndSymbolicValue() {
		return valueDateEndSymbolicValue;
	}

	public void setValueDateEndSymbolicValue(String valueDateEndSymbolicValue) {
		this.valueDateEndSymbolicValue = valueDateEndSymbolicValue;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ReportParameterOption getReportParameterOption() {
		return reportParameterOption;
	}

	public void setReportParameterOption(ReportParameterOption reportParameterOption) {
		this.reportParameterOption = reportParameterOption;
	}
}
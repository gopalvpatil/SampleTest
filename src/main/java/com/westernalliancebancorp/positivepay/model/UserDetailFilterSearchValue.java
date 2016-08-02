package com.westernalliancebancorp.positivepay.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.westernalliancebancorp.positivepay.model.interceptor.AuditListener;

/**
 * Search Parameter
 * @author Anand Kumar
 */

@Table(name = "USER_DETAIL_FILTER_SEARCH_VALUES", uniqueConstraints = {@UniqueConstraint(columnNames = { "USER_DETAIL_DEFINED_FILTER_ID", "SEARCH_PARAMETER_ID", "PARAM_SEQUENCE" })})
@EntityListeners(AuditListener.class)
@Entity
public class UserDetailFilterSearchValue implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "ID")
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "PARAM_SEQUENCE", length = 50, nullable = false)
    private int paramSequence;
    @Column(name = "PARAMETER_VALUE", nullable = false)
    private String parameterValue;
    @Column(name = "RELATIONAL_OPERATOR", nullable = false)
    private String relationalOperator;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_DETAIL_DEFINED_FILTER_ID", nullable = false)
    private UserDetailDefinedFilter userDetailDefinedFilter;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEARCH_PARAMETER_ID", nullable = false)
    private SearchParameter searchParameter;    
    
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getParamSequence() {
		return paramSequence;
	}

	public void setParamSequence(int paramSequence) {
		this.paramSequence = paramSequence;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public UserDetailDefinedFilter getUserDetailDefinedFilter() {
		return userDetailDefinedFilter;
	}

	public void setUserDetailDefinedFilter(
			UserDetailDefinedFilter userDetailDefinedFilter) {
		this.userDetailDefinedFilter = userDetailDefinedFilter;
	}

	public SearchParameter getSearchParameter() {
		return searchParameter;
	}

	public void setSearchParameter(SearchParameter searchParameter) {
		this.searchParameter = searchParameter;
	}

	public String getRelationalOperator() {
		return relationalOperator;
	}

	public void setRelationalOperator(String relationalOperator) {
		this.relationalOperator = relationalOperator;
	}

		@Override
	public String toString() {
		return "UserDetailFilterSearchValue [id=" + id + ", paramSequence="
				+ paramSequence + ", parameterValue=" + parameterValue
				+ ", userDetailDefinedFilter=" + userDetailDefinedFilter
				+ ", searchParameter=" + searchParameter  + "]";
	}
}

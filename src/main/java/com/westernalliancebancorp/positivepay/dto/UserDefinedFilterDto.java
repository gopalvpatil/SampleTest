package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.westernalliancebancorp.positivepay.model.UserDetail;

public class UserDefinedFilterDto implements Serializable{
	
	private static final long serialVersionUID = 8119346499164556621L;
	
	private Long id;
	private String filterName;
	private String filterDescription;
	private UserDetail userDetail;
	private String searchCriteria;
	Map<String, SearchParameterDto> searchParametersMap = new TreeMap<String, SearchParameterDto>();
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the filterName
	 */
	public String getFilterName() {
		return filterName;
	}
	/**
	 * @param filterName the filterName to set
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	/**
	 * @return the filterDescription
	 */
	public String getFilterDescription() {
		return filterDescription;
	}
	/**
	 * @param filterDescription the filterDescription to set
	 */
	public void setFilterDescription(String filterDescription) {
		this.filterDescription = filterDescription;
	}
	/**
	 * @return the userDetail
	 */
	public UserDetail getUserDetail() {
		return userDetail;
	}
	/**
	 * @param userDetail the userDetail to set
	 */
	public void setUserDetail(UserDetail userDetail) {
		this.userDetail = userDetail;
	}
	/**
	 * @return the searchParametersMap
	 */
	public Map<String, SearchParameterDto> getSearchParametersMap() {
		return searchParametersMap;
	}
	/**
	 * @param searchParametersMap the searchParametersMap to set
	 */
	public void setSearchParametersMap(Map<String, SearchParameterDto> searchParametersMap) {
		this.searchParametersMap = searchParametersMap;
	}
	/**
	 * @return the searchCriteria
	 */
	public String getSearchCriteria() {
		return searchCriteria;
	}
	/**
	 * @param searchCriteria the searchCriteria to set
	 */
	public void setSearchCriteria(String searchCriteria) {
		this.searchCriteria = searchCriteria;
	}
}

package com.westernalliancebancorp.positivepay.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.westernalliancebancorp.positivepay.model.JobActionType;

/**
 * @author Gopal Patil
 *
 */
public class JobTypeDto implements Serializable {

	private static final long serialVersionUID = 6516972001893225991L;
	
    private Long id;
	
	private String name;
	
    private List<JobActionType> jobActionTypes = new ArrayList<JobActionType>();

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

	public List<JobActionType> getJobActionTypes() {
		return jobActionTypes;
	}

	public void setJobActionTypes(List<JobActionType> jobActionTypes) {
		this.jobActionTypes = jobActionTypes;
	}
    

}

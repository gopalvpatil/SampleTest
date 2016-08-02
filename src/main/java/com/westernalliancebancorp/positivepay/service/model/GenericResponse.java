package com.westernalliancebancorp.positivepay.service.model;

import java.io.Serializable;

public class GenericResponse implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private String message;
	
	private String code;

	public GenericResponse(String m)
	{
		this.message = m;
	}
	
	public GenericResponse(String m, long id)
	{
		this.message = m;
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}	
	
}

package com.westernalliancebancorp.positivepay.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author umeshram
 *
 */
public class HttpStatusCodedResponseException extends Exception {

	private static final long serialVersionUID = 1L;

	private final HttpStatus statusCode;

	private final Object responseBody;

	private final HttpHeaders responseHeaders;

	public HttpStatusCodedResponseException(HttpStatus statusCode, String errorMessage) {
		this(statusCode, errorMessage, null, null);
	}

	public HttpStatusCodedResponseException(HttpStatus statusCode,
			String errorMessage, Object responseBody) {
		this(statusCode, errorMessage, null, responseBody);
	}

	public HttpStatusCodedResponseException(HttpStatus statusCode, String errorMessage,
			HttpHeaders responseHeaders, Object responseBody) {
		super(errorMessage);
		this.statusCode = statusCode;
		this.responseHeaders = responseHeaders;
		this.responseBody = responseBody ;
	}


	public HttpStatus getStatusCode() {
		return this.statusCode;
	}

	public HttpHeaders getResponseHeaders() {
		return this.responseHeaders;
	}

	public Object getResponseBody() {
		return responseBody;
	}

}


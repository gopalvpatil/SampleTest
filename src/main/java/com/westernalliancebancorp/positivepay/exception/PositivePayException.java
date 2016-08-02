package com.westernalliancebancorp.positivepay.exception;

/**
 * 
 * @author umeshram
 *
 */
public class PositivePayException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public PositivePayException(String message) {
		super(message);
	}
	
	public PositivePayException(Throwable cause) {
		super(cause);
	}
	
	public PositivePayException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

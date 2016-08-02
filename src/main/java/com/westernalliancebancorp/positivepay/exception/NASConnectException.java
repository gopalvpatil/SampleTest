package com.westernalliancebancorp.positivepay.exception;

/**
 * 
 * @author Gopal Patil
 *
 */
public class NASConnectException extends Exception {

	private static final long serialVersionUID = -3139800912494766627L;
	
	public NASConnectException() {
		super();
	}
	
	public NASConnectException(String message) {
        super(message);
    }
	
    public NASConnectException(Throwable cause) {
        super(cause);
    }
	 
    public NASConnectException(String message, Throwable cause) {
        super(message, cause);
    }

}

package com.westernalliancebancorp.positivepay.exception;

/**
 * 
 * @author Gopal Patil
 *
 */
public class SftpConnectException extends Exception {

	private static final long serialVersionUID = -2006202014143735047L;
	
	public SftpConnectException() {
		super();
	}
	
	public SftpConnectException(String message) {
        super(message);
    }
	
    public SftpConnectException(Throwable cause) {
        super(cause);
    }
	 
    public SftpConnectException(String message, Throwable cause) {
        super(message, cause);
    }

}

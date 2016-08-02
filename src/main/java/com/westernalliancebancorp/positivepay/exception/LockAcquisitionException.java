package com.westernalliancebancorp.positivepay.exception;

public class LockAcquisitionException extends Exception {

	private static final long serialVersionUID = 8067206803882379100L;
	
	private Exception e;

	@Override
	public Throwable getCause() {
		return e;
	}
	
	@Override
	public String getMessage() {
		return "Lock Acquisition exception caused by : "+e.getMessage();
	}
	
	public LockAcquisitionException(Exception e) {
		this.e = e;
	}
	
	public LockAcquisitionException(String message) {
		this.e = new RuntimeException(message);
	}

}

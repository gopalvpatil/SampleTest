package com.westernalliancebancorp.positivepay.exception;

/**
 * @author Gopal Patil
 *
 */
public class SchedulerException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private Exception e;

	@Override
	public Throwable getCause() {
		return e;
	}
	
	@Override
	public String getMessage() {
		return "Scheduler exception caused by : "+e.getMessage();
	}
	
	public SchedulerException(Exception e) {
		this.e = e;
	}
	
	public SchedulerException(String message) {
		this.e = new RuntimeException(message);
	}

}

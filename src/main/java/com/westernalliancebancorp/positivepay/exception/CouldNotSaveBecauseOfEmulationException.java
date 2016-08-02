package com.westernalliancebancorp.positivepay.exception;

/**
 * CouldNotSaveBecauseOfEmulationException is thrown via EmulationAspect when the emulated user tries to save the data to the database.
 *
 * @author Anand Kumar
 */

public class CouldNotSaveBecauseOfEmulationException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 978510297849130760L;

	public CouldNotSaveBecauseOfEmulationException(String message) {
        super(message);
    }

    public CouldNotSaveBecauseOfEmulationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouldNotSaveBecauseOfEmulationException(Throwable cause) {
        super(cause);
    }
}

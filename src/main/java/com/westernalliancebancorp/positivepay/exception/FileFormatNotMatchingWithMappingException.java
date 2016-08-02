package com.westernalliancebancorp.positivepay.exception;

/**
 * FileFormatNotMatchingWithMappingException is
 *
 * @author Giridhar Duggirala
 */

public class FileFormatNotMatchingWithMappingException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 978510297849130760L;

	public FileFormatNotMatchingWithMappingException(String message) {
        super(message);
    }

    public FileFormatNotMatchingWithMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileFormatNotMatchingWithMappingException(Throwable cause) {
        super(cause);
    }
}

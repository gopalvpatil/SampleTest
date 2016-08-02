package com.westernalliancebancorp.positivepay.exception;

/**
 * PositivePayRuleVoilationException is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayRuleVoilationException extends RuntimeException {
    public PositivePayRuleVoilationException(Exception e) {
        super(e);
    }

    public PositivePayRuleVoilationException(String message) {
        super(message);
    }

    public PositivePayRuleVoilationException(String message, Throwable clause) {
        super(message, clause);
    }
}

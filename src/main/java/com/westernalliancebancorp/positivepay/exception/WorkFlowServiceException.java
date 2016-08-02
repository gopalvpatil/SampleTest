package com.westernalliancebancorp.positivepay.exception;

/**
 * WorkFlowServiceException is
 *
 * @author Giridhar Duggirala
 */

public class WorkFlowServiceException extends Exception {
    private Exception e;

    @Override
    public Throwable getCause() {
        return e;
    }

    @Override
    public String getMessage() {
        return "Scheduler exception caused by : "+e.getMessage();
    }

    public WorkFlowServiceException(Exception e) {
        this.e = e;
    }

    public WorkFlowServiceException(String message) {
        this.e = new RuntimeException(message);
    }

    public WorkFlowServiceException(String message, Throwable clause) {
        this.e = new RuntimeException(message, clause);
    }
}

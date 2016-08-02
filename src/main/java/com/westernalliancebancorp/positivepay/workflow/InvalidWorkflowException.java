package com.westernalliancebancorp.positivepay.workflow;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/20/14
 * Time: 10:08 PM
 */
public class InvalidWorkflowException extends RuntimeException {
    public InvalidWorkflowException(String message) {
        super(message);

    }
}

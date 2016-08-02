package com.westernalliancebancorp.positivepay.workflow;

/**
 * StatusDepartureCallback is called right after updating the status of the check.
 * You don't need to start a new transaction as this is being already called under a transaction.
 * If there are any uncaught exception then the whole actions will be rolled back
 *
 * @author Giridhar Duggirala
 */

public interface StatusDepartureCallback {
    boolean executeStatusDepartureCallback(CallbackContext callbackContext) throws CallbackException;
}

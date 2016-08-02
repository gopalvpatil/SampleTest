package com.westernalliancebancorp.positivepay.workflow;

import java.util.Map;

/**
 * StatusArrivalCallback is called right after updating the status of the check.
 * You don't need to start a new transaction as this is being already called under a transaction.
 * If there are any uncaught exception then the whole actions will be rolled back
 *
 * @author Giridhar Duggirala
 */

public interface StatusArrivalCallback {
    boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException;
}

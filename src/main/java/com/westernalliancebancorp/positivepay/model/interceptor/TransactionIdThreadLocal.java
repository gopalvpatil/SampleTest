package com.westernalliancebancorp.positivepay.model.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TransactionIdThreadLocal is
 *
 * @author Giridhar Duggirala
 */

public class TransactionIdThreadLocal {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<String>();
    protected static final Log logger = LogFactory.getLog(TransactionIdThreadLocal.class);
    public static void set(String name) {
        if (threadLocal.get() == null) {
            threadLocal.set(name);
        } else {
            logger.error(
                    String.format("TransactionId already exists in the ThreadLocal a new request for setting it has come, existing transaction id is %s and the new one is%s Please check",
                            threadLocal.get(), name));
        }
    }

    public static String get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}

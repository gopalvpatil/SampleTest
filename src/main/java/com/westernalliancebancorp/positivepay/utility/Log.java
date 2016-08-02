package com.westernalliancebancorp.positivepay.utility;

import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Calendar;

/**
 * Log is
 *
 * @author Giridhar Duggirala
 */

public class Log {
    public static String event(Event eventName, String message, String transactionId) {
        String xml = "<event><name>%s</name><timestamp>%s</timestamp><transactionid>%s</transactionid><message>%s</message></event>";
        Calendar calendar = Calendar.getInstance();
        return String.format(xml, eventName.name(), calendar.getTime(), transactionId, message);
    }

    public static String event(Event eventName, String message, String transactionId, Exception ex) {
        String xml = "<event><name>%s</name><timestamp>%s</timestamp><transactionid>%s</transactionid><message>%s</message><stacktrace>%s</stacktrace></event>";
        Calendar calendar = Calendar.getInstance();
        return String.format(xml, eventName.name(), calendar.getTime(), transactionId, message, ExceptionUtils.getFullStackTrace(ex));
    }

    public static String event(Event eventName, String message, Exception ex) {
        return event(eventName, message, TransactionIdThreadLocal.get(), ex);
    }

    public static String event(Event eventName, String message) {
        return event(eventName, message, TransactionIdThreadLocal.get());
    }
}

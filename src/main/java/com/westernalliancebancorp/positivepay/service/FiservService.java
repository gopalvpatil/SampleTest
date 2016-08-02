package com.westernalliancebancorp.positivepay.service;

/**
 * User: gduggirala
 * Date: 6/5/14
 * Time: 7:13 PM
 */
public interface FiservService {
    String getFiServUrl(Long checkId, String side);

	String getFiServUrlForExceptionId(Long exceptionId, String side);

    String getFiServUrl(String number, String amount, String traceNumber, String side);
}

package com.westernalliancebancorp.positivepay.service;

import java.util.Map;

import com.westernalliancebancorp.positivepay.exception.NASConnectException;

/**
 * User: gduggirala
 * Date: 22/5/14
 * Time: 10:39 AM
 */
public interface CrsPaidJobService {
    Map<String, Integer> pullCrsPaidFile() throws NASConnectException;
}

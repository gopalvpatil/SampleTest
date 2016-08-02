package com.westernalliancebancorp.positivepay.service;

import java.util.Map;

import com.westernalliancebancorp.positivepay.exception.NASConnectException;

/**
 * User: gduggirala
 * Date: 28/5/14
 * Time: 1:58 PM
 */
public interface DailyStopFileJobService {
    Map<String, Integer> pullStopFile() throws NASConnectException;
}

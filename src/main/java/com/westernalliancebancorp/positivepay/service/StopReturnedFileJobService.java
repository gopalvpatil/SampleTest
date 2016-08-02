package com.westernalliancebancorp.positivepay.service;

import java.util.Map;

import com.westernalliancebancorp.positivepay.exception.NASConnectException;

/**
 * Date: 28/5/14
 * Time: 1:58 PM
 */
public interface StopReturnedFileJobService {
    Map<String, Integer> pullStopReturnedFile() throws NASConnectException;
}

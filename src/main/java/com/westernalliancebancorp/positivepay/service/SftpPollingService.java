package com.westernalliancebancorp.positivepay.service;

import java.util.Map;

import com.westernalliancebancorp.positivepay.exception.SftpConnectException;

/**
 * 
 * @author Gopal Patil
 *
 */
public interface SftpPollingService {
	 Map<String, Integer> pullFiles(String fileType) throws SftpConnectException;
}

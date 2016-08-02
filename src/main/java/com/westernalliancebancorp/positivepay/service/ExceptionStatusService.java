package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.ExceptionStatus;

/**
 * User:	Moumita Ghosh
 * Date:	May 28, 2014
 * Time:	5:28:30 PM
 */
public interface ExceptionStatusService {
	 void processExceptionChecks();
	 List<ExceptionStatus> getAllExceptionStatus();
}

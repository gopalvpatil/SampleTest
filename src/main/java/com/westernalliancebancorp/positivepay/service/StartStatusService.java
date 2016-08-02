package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User:	Gopal Patil
 * Date:	Apr 2, 2014
 * Time:	5:28:30 PM
 */
public interface StartStatusService {
	void processStartChecks(List<Check> checks) throws CallbackException, WorkFlowServiceException;
    void processStartChecks() throws CallbackException, WorkFlowServiceException;
}

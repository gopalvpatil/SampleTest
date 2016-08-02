package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 16/4/14
 * Time: 2:18 PM
 */
public interface StopReturnService {
    boolean processStopReturnReference(List<Long> referenceIds) throws WorkFlowServiceException, CallbackException;
}

package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.workflow.CallbackContext;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User:	Moumita Ghosh
 * Date:	June 18, 2014
 * Time:	5:28:30 PM
 */
public interface ReferenceDataProcessorService {
    void processNonDuplicateReferenceData(List<ReferenceData> referenceDataList);
    void processNonDuplicateReferenceData(ReferenceData referenceDataList) throws CallbackException, WorkFlowServiceException;
    void processNonDuplicateReferenceData(ReferenceData referenceData, Long checkId) throws CallbackException, WorkFlowServiceException;
    void processNonDuplicateReferenceData(ReferenceData referenceData, Check check) throws CallbackException, WorkFlowServiceException;
    void processNonDuplicateReferenceData(ReferenceData referenceData,CallbackContext callbackContext) throws CallbackException,WorkFlowServiceException;
    void processNonDuplicateReferenceData(ReferenceData referenceData,Check check, CallbackContext callbackContext) throws CallbackException, WorkFlowServiceException;
}

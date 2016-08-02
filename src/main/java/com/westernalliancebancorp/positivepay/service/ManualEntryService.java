package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 30/5/14
 * Time: 12:52 PM
 */
public interface ManualEntryService {
    Map<String, List<CheckDto>> saveManualEntries(List<CheckDto> checks) throws CallbackException, WorkFlowServiceException;
    List<ItemType> retrievePermittedItemTypes(String userName);
}

package com.westernalliancebancorp.positivepay.workflow;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * User: moumita 
 * Date: 03/6/14 
 * Time: 3:01 PM
 */
@Component("staleVoidStatusArrivalCallback")
public class StaleVoidStatusArrivalCallback implements
		StatusArrivalCallback {
	@Loggable
	Logger logger;

	@Autowired
	WorkflowService workflowService;

	@Override
	public boolean executeOnStatusArrival(CallbackContext callbackContext)
			throws CallbackException {
	    Check check = callbackContext.getCheck();
	    Check manualEntryCheck = (Check)callbackContext.getUserData().get(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name());
	    if(manualEntryCheck != null)
	    {
		check.setVoidDate(manualEntryCheck.getVoidDate());
		check.setVoidAmount(manualEntryCheck.getVoidAmount());
                callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name());
	    }
	    else
	    {
	    ExceptionalCheck exCheck = (ExceptionalCheck)callbackContext.getUserData().get(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name());
	    if(exCheck != null)
	    {
            try {
		check.setVoidDate(DateUtils.getDateFromString(StringUtils.trim(exCheck.getIssueDate())));
	    } catch (ParseException e) {
		throw new CallbackException(e);
	    }
                check.setVoidAmount(new BigDecimal(StringUtils.trim(exCheck.getIssuedAmount())));
                callbackContext.getUserData().remove(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name());
	    }
	    }
		try {
			workflowService.performAction(check, "void",
					callbackContext.getUserData());
		} catch (WorkFlowServiceException e) {
			throw new CallbackException(e);
		}
		return Boolean.TRUE;
	}
}

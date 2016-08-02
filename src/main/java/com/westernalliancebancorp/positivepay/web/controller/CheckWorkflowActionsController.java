package com.westernalliancebancorp.positivepay.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User: gduggirala
 * Date: 3/4/14
 * Time: 12:41 PM
 */
@Controller
public class CheckWorkflowActionsController {
    @Loggable
    private Logger logger;

    @Autowired
    CheckDao checkDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    WorkflowService workflowService;

    /**
     * A Generic one where no user data is required and we just would lke to perform the action which the user has requested.
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param checkId
     * @param actionName
     * @return
     * @throws CallbackException
     * @throws WorkFlowServiceException
     */
    @RequestMapping(value = "/user/workflow/generic", method = RequestMethod.POST)
    public @ResponseBody String performWorkflowAction(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        @RequestParam(value = "checkId") Long checkId, @RequestParam(value = "actionName") String actionName) throws CallbackException, WorkFlowServiceException {
        Map<String, Object> userData = new HashMap<String, Object>();
        workflowService.performAction(checkId, actionName, userData);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }
    
    @RequestMapping(value = "/user/exceptions/resolve", method = RequestMethod.POST)
    public @ResponseBody List<CheckDto> resolveExceptions(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
    		@RequestBody List<CheckDto> checks) throws CallbackException, WorkFlowServiceException {
    	for(CheckDto check: checks) {
    		Map<String, Object> userData = new HashMap<String, Object>();
    		userData.put(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name(), check.getReason());
            workflowService.performAction(check.getId(), check.getDecision(), userData);
    	}        
        return checks;
    }

    /**
     * This method can be used when user/admin is performing change account or change check number actions are being performed.
     * @param httpServletRequest
     * @param checkId
     * @param actionName
     * @return
     * @throws CallbackException
     * @throws WorkFlowServiceException
     */
    @PositivePaySecurity(resource = "CHANGE_ACCOUNT_NUMBER", errorMessage = "doesn't have permission to change account number", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/user/workflow/changeAccountNumber", method = RequestMethod.POST)
    public @ResponseBody String performChangeAccountNumber(HttpServletRequest httpServletRequest,
                                       @RequestParam(value = "checkId") Long checkId, @RequestParam(value = "actionName") String actionName) throws CallbackException, WorkFlowServiceException {
        Map<String, Object> userData = new HashMap<String, Object>();
        if(httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name()) != null) {
            userData.put(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name(),httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name()));
            userData.put(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name(), "Account number changed to "+ httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name()));
        }
        workflowService.performAction(checkId, actionName, userData);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }

    @PositivePaySecurity(resource = "CHANGE_CHECKNUMBER", errorMessage = "doesn't have permission to change check number", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/user/workflow/changeCheckNumber", method = RequestMethod.POST)
    public @ResponseBody String performChangeCheckNumber(HttpServletRequest httpServletRequest,
                                       @RequestParam(value = "checkId") Long checkId, @RequestParam(value = "actionName") String actionName) throws CallbackException, WorkFlowServiceException {
        Map<String, Object> userData = new HashMap<String, Object>();
        if(httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name()) != null) {
            userData.put(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name(), PPUtils.stripLeadingZeros(httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name())));
            userData.put(WorkflowService.STANDARD_MAP_KEYS.SYSTEM_COMMENT.name(), "Check number changed to "+ httpServletRequest.getParameter(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_NEW.name()));
        }
        workflowService.performAction(checkId, actionName, userData);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }

    @PositivePaySecurity(resource = "ADJUST_AMOUNT", errorMessage = "doesn't have permission to adjust the amount", group = Permission.TYPE.ITEMS)
    @RequestMapping(value = "/user/workflow/adjustAmount", method = RequestMethod.GET)
    public String performAdjustAmountAction(@RequestParam(value = "checkId") Long checkId, @RequestParam(value = "actionName") String actionName) throws CallbackException, WorkFlowServiceException {
        Map<String, Object> userData = new HashMap<String, Object>();
        workflowService.performAction(checkId, actionName, userData);
        //TODO: UI Developer please use the appropriate response.
        return "";
    }
    
    @ExceptionHandler(Exception.class)
	public @ResponseBody
	GenericResponse handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) {
        logger.error(Log.event(Event.WORKFLOW_EXECUTION_EXCEPTION, "Unknown error occurred so I have been routed to global exception handler", ex),ex);
        String message = "{\"error\":\""+ex.getMessage()+"\",\"transactionId\":\""+TransactionIdThreadLocal.get()+"\"}";
		GenericResponse genericResponse = new GenericResponse(message);
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return genericResponse;
	}
}

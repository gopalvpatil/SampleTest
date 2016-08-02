package com.westernalliancebancorp.positivepay.web.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.DecisionWindowDto;
import com.westernalliancebancorp.positivepay.dto.PaymentDetailDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.model.Reason;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.DecisionWindowService;
import com.westernalliancebancorp.positivepay.service.PaymentsAndItemsService;
import com.westernalliancebancorp.positivepay.service.ReasonService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * Spring Controller for exceptions page UI 
 * @author Anand Kumar
 */
@Controller
public class ExceptionsController {

	@Loggable
	private Logger logger;
	@Autowired
	ReasonService reasonService;
	@Autowired
	UserService userervice;
	@Autowired
	CheckService checkService;	
	@Autowired 
	PaymentsAndItemsService paymentsAndItemsService;	
	@Autowired
    WorkflowManagerFactory workflowManagerFactory;
	@Autowired
	DecisionWindowService decisionWindowService;
	
	@RequestMapping(value = "/user/exceptions", method = RequestMethod.GET)
	public String showExceptionsPage(Model model, HttpServletRequest request) throws ParseException{
		//get All accounts
		List<Account> userAccounts = userervice.getUserAccounts();
		model.addAttribute("userAccounts", userAccounts);
		//find the decision window
		Company company = userervice.getLoggedInUserCompany();
		DecisionWindow decisionWindow = company.getDecisionWindow();
		if (decisionWindowService.isWithinDecisionWindow(decisionWindow)) {
			//Inside decisioning window so, find all exceptions
			List<CheckDto> checksInException = checkService.findAllChecksInExceptionForUserCompany();
			logger.info("checksInException = "+checksInException.toString());
			model.addAttribute("checksInException", checksInException);
			//find all available actions for every check, add onto a map and return the map to UI.
			Map<Long, Map<String, String>> availableActionsForChecksMap = new HashMap<Long, Map<String,String>>();
			for(CheckDto checkInException: checksInException) {
				WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInException.getWorkflowId());
				Map<String, String> availableActions = new HashMap<String, String>();
				if(workflowManager != null) {
					availableActions = workflowManager.getNonAdminActionsForStatus(checkInException.getStatusName());
				}
				availableActionsForChecksMap.put(checkInException.getId(), availableActions);
			}
			model.addAttribute("availableActionsForChecksMap", availableActionsForChecksMap);
		} else {
			//outside the window so show message.
			model.addAttribute("outsideResolutionWindow", true);
		}
		//change to normal time from military time
		DecisionWindowDto decisonWindowDto = new DecisionWindowDto();
		decisonWindowDto.setStart(DateUtils.convertFromMilitaryToNormalTime(decisionWindow.getStartWindow().toString()));
		decisonWindowDto.setEnd(DateUtils.convertFromMilitaryToNormalTime(decisionWindow.getEndWindow().toString()));
		decisonWindowDto.setTimezone(decisionWindow.getTimeZone());
		model.addAttribute("decisionWindow", decisonWindowDto);
		//forward to exceptions page
		return "site.exception.resolution.page";
	}
	
	@RequestMapping(value = "/user/exceptions/reasons", method = RequestMethod.GET)
	public @ResponseBody List<Reason> getReasons(@RequestParam boolean isPay)
	{
		return reasonService.findAllActiveReasons(isPay);
	} 
	
	@RequestMapping(value = "/user/exceptions/payments", method = RequestMethod.GET)
	public @ResponseBody List<PaymentDetailDto> getPaymentList()
	{
		return paymentsAndItemsService.findAllPaymentsForUserCompany();
	}
	
	@RequestMapping(value = "/user/exceptions/checkdetails", method = RequestMethod.GET)
	public @ResponseBody CheckDto getCheckDetails(@RequestParam Long checkId)
	{
		return checkService.getCheckDetails(checkId);
	}
	
	@RequestMapping(value = "/user/exceptions/zerocheckdetails", method = RequestMethod.GET)
	public @ResponseBody CheckDto getZeroCheckDetails(@RequestParam String traceNumber)
	{
		return checkService.findCheckByTraceNumber(traceNumber);
	}
}

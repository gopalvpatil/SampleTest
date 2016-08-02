package com.westernalliancebancorp.positivepay.web.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.SystemMessageService;
import com.westernalliancebancorp.positivepay.service.UserService;

/**
 * User: gduggirala Date: 12/5/14 Time: 12:53 PM
 */
@Controller
public class SystemMessageController {
	@Loggable
	private Logger logger;

	@Autowired
	SystemMessageService systemMessageService;

	@Autowired
	UserService userService;

	@Autowired
	UserDetailsService userDetailsService;
	
	@RequestMapping(value = "/admin/system")
	public String adminSystem(Model model, HttpServletRequest request) throws Exception
	{

		return "site.admin.system.page";
	}

	@RequestMapping(value = "/messages/system", method = RequestMethod.GET)
	public @ResponseBody
	SystemMessage getLoginSystemMessages(HttpServletRequest request)
			throws Exception {
		List<SystemMessage> systemMessageList = systemMessageService
				.getSystemMessages(SystemMessage.TYPE.LOGIN);
		if (systemMessageList.size() > 0) {
			return systemMessageList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Modified @Sameer Shukla, Modified as per Check Constraint
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/messages/maintenance", method = RequestMethod.GET)
	public @ResponseBody
	SystemMessage getPostLoginSystemMessages(HttpServletRequest request)
			throws Exception {

		
		List<SystemMessage> systemMessageList = systemMessageService
				.getSystemMessages(SystemMessage.TYPE.POSTLOGIN);
		//SAMEER SHUKLA : COMMENTED AS PER WALPP-332.
		//22nd July Uncommented as per WALPP-332
		String userName = "";
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if (systemMessageList.size() > 0) {
			if (securityContext.getAuthentication() != null) {
				Authentication authentication = securityContext
						.getAuthentication();
				userName = (String) authentication.getPrincipal();
			}
			UserDetail userDetail = userService.findByName(userName);
			long id = 0;
			if(userDetail != null)
				id = userDetail.getId();
			List<Long> entry = systemMessageService
					.systemMessageInUserDetailHistory(id,
							systemMessageList.get(0).getId());
			Long value = entry.get(0);
			SystemMessage systemMessage = systemMessageList.get(0); 
			if (value == 0) {
				return systemMessage;
			} else {
				//systemMessageList.get(0).setMessage("-1");
				String message = systemMessage.getMessage();
				systemMessage.setMessage(message + "N/A");
				return systemMessage;
			}
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/messages/clearSystemMessage", method = RequestMethod.POST)
	public @ResponseBody
	Boolean clearSystemMessage() throws Exception {
		systemMessageService.deleteAllSystemMessages(SystemMessage.TYPE.LOGIN);
		return true;
	}

	@RequestMapping(value = "/messages/clearMaintenanceMessage", method = RequestMethod.POST)
	public @ResponseBody
	Boolean setMaintenanceMessage() throws Exception {
		systemMessageService
				.deleteAllSystemMessages(SystemMessage.TYPE.POSTLOGIN);
		return true;
	}

	@RequestMapping(value = "/messages/saveSystemMessage", method = RequestMethod.POST)
	public @ResponseBody
	SystemMessage setSystemMessage(
			@RequestParam(value = "message", required = true) String message,
			@RequestParam(value = "fromDate", required = true) Date fromDate,
			@RequestParam(value = "toDate", required = true) Date toDate,
			@RequestParam(value = "logintimezone", required = true) String logintimezone,
			HttpServletRequest request) throws Exception {
		systemMessageService.deleteAllSystemMessages(SystemMessage.TYPE.LOGIN);
		SystemMessage systemMessage = systemMessageService.setSystemMessage(
				message, SystemMessage.TYPE.LOGIN, fromDate, toDate, logintimezone);
		return systemMessage;
	}

	@RequestMapping(value = "/messages/saveMaintenanceMessage", method = RequestMethod.POST)
	public @ResponseBody
	SystemMessage setMaintenanceMessage(
			@RequestParam(value = "message", required = true) String message,
			@RequestParam(value = "fromDate", required = true) Date fromDate,
			@RequestParam(value = "toDate", required = true) Date toDate,
			@RequestParam(value = "systimezone", required = true) String systimezone,
			HttpServletRequest request) throws Exception {
		systemMessageService
				.deleteAllSystemMessages(SystemMessage.TYPE.POSTLOGIN);
		SystemMessage systemMessage = systemMessageService.setSystemMessage(
				message, SystemMessage.TYPE.POSTLOGIN, fromDate, toDate, systimezone);
		return systemMessage;
	}

}

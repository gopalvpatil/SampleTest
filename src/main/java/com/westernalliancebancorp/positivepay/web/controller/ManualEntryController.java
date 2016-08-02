package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.service.ItemTypeService;
import com.westernalliancebancorp.positivepay.service.ManualEntryService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Spring Controller to tackle Manual Check entry
 * @author Anand Kumar
 */
@Controller
public class ManualEntryController {

    @Loggable
	private Logger logger;

	@Autowired
	ManualEntryService manualEntryService;
    @Autowired
    UserService userService;
    @Autowired
    ItemTypeService itemTypeService;

	@RequestMapping(value = "/user/manualentry", method = RequestMethod.GET)
	public String showManualEntryPage(Model model, HttpServletRequest request) {
		String requestMapping = (String) request.getServletPath();
		logger.info("processing url : {}", requestMapping);
		return "site.manual.entry.page";
	}

	@RequestMapping(value = "/user/manualentry", method = RequestMethod.POST)
	public @ResponseBody Map<String, List<CheckDto>> postManualEntryPage(@RequestBody List<CheckDto> checks, HttpServletRequest request) throws CallbackException, WorkFlowServiceException, ParseException {
        //WALPP-266
		for(CheckDto checkDto:checks) {
            String dateReceivedFromBrowser = checkDto.getManualEntryDate();
            Date userDate = DateUtils.getDateFromString(dateReceivedFromBrowser);
            checkDto.setIssueDate(userDate);
        }
        return manualEntryService.saveManualEntries(checks);
	}

    @RequestMapping(value = "/user/manualentry/companies", method = RequestMethod.GET)
	public @ResponseBody List<CompanyDTO> getAllCompanies() {
		return getCompanies();
	}

	@RequestMapping(value = "/user/manualentry/accounts", method = RequestMethod.GET)
	public @ResponseBody List<String> getAccountsByCompanyId(@RequestParam String companyId)
	{
		List<Account> accounts = userService.getUserAccountsByCompanyId(companyId);
		List<String> accountNumbers = new ArrayList<String>();
		for(Account account: accounts) {
			accountNumbers.add(account.getNumber());
		}
		return accountNumbers;
	}

    @RequestMapping(value = "/user/manualentry/issuecodes", method = RequestMethod.GET)
    public @ResponseBody List<ItemType> getAllIssueCodes() {
        return manualEntryService.retrievePermittedItemTypes(SecurityUtility.getPrincipal());
    }

	private List<CompanyDTO> getCompanies() {
        List<CompanyDTO> companies = userService.getCompanies();
		return companies;
	}
}

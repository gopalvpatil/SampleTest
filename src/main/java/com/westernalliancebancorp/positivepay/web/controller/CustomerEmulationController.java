package com.westernalliancebancorp.positivepay.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.EmulatedCookieService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.AffidavitSecurityUtility;

/**
 * User: gduggirala
 * Date: 3/13/14
 * Time: 10:33 AM
 */
@Controller
public class CustomerEmulationController {
    @Loggable
    private Logger logger;

    @Value("${positivepay.affidavit.cookie.name}")
    private String ppAffidavitName = "PP_AFFIDAVIT_COOKIE";

    @Value("${positivepay.affidavit.cookie.domain}")
    private String getPpAffidavitDomainName = "wal.com";

    @Value("${positivepay.cookie.is.secure}")
    private boolean isSecureCookie = Boolean.TRUE; //2hrs

    @Autowired
    private EmulatedCookieService emulatedCookieService;  
    @Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private AffidavitSecurityUtility affidavitSecurityUtility;

    @RequestMapping(value = "/user/emulation/create", method = RequestMethod.GET)
    public String createEmulationCookie(@RequestParam(value = "userName", required = true)String userName, HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        String cookieValue = emulatedCookieService.createEmulationCookie(userName);
        setCookie(cookieValue, httpServletResponse);
        return "redirect:/user/dashboard";
    }
    
    @RequestMapping(value = "/user/emulation/exit", method = RequestMethod.GET)
    public String exitEmulation(HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
    	String cookieValue = emulatedCookieService.exitEmulationCookie();
    	setCookie(cookieValue, httpServletResponse);
        return "redirect:/user/dashboard";
    }
    
    @RequestMapping(value = "/user/emulation", method = RequestMethod.GET)
	public String showCustomerEmulationPage(Model model, HttpServletRequest request)
			throws Exception {
    	//find all banks to be show in the drop down
    	List<Bank> banks = bankService.findAll();
    	//Sorting banks based on name so that the bank options are sorted alphabetically
    	Collections.sort(banks);
    	model.addAttribute("banks", banks);
		return "site.customer.emulation.page";
	}
    
    @RequestMapping(value = "/user/emulation/companies", method = RequestMethod.GET)
	public @ResponseBody List<CompanyDTO> getCompaniesByBankId(@RequestParam Long bankId)
			throws Exception {
    	return companyService.findByBankId(bankId);
	}
    
    @RequestMapping(value = "/user/emulation/users", method = RequestMethod.GET)
	public @ResponseBody List<UserDto> getUsersByCompanyId(@RequestParam Long companyId)
			throws Exception {
    	return userService.getUsersByCompanyId(companyId);
	}
    
    private void setCookie(String cookieValue, HttpServletResponse httpServletResponse) {
    	Cookie cookie = new Cookie(ppAffidavitName, cookieValue);
        cookie.setPath("/");
        cookie.setDomain(getPpAffidavitDomainName);
        cookie.setMaxAge(-1);
        cookie.setSecure(isSecureCookie);
        httpServletResponse.addCookie(cookie);
        logger.debug(new StringBuilder().append("Cookie added with name '").append(ppAffidavitName).append("' and the value '").append(cookieValue).append("'").toString());
        logger.info("Cookie added.");
    }

}

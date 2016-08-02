package com.westernalliancebancorp.positivepay.web.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.CompanyDtoBuilder;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.UserService;

/**
 * 
 * @author umeshram
 * 
 */
@Controller
public class CompanyController {

	@Loggable
	private Logger logger;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private BankService bankService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/user/companysetup")
	public String manageBanks(
			Model model,
			@RequestParam(value = "bankId", required = false) Long bankId,
			@RequestParam(value = "companyId", required = false) Long companyId,
			HttpServletRequest request) {

		model.addAttribute("companyId", companyId == null ? "" : companyId);
		model.addAttribute("bankId", bankId == null ? "" : bankId);

		List<Bank> banks = bankService.findAll();
		//Sorting banks based on name so that the bank options are sorted alphabetically
    	Collections.sort(banks);
		model.addAttribute("banks", banks);
		model.addAttribute("companyDTO", new CompanyDTO());// TODO why it is
															// needed?

		/*
		 * List<AccountServiceOption> accountServiceOptions =
		 * accountService.findOptionByName();//TODO: cache values
		 * model.addAttribute("accountServiceOptions", accountServiceOptions);
		 * 
		 * List<AccountCycleCutOff> accountCycleCutOffs =
		 * accountService.findCycle();//TODO: cach values
		 * model.addAttribute("accountCycleCutOffs", accountCycleCutOffs);
		 */

		return "site.company.setup.page";
	}

	/**
	 * Get Company
	 * 
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/company/{companyId}", method = RequestMethod.GET)
	public @ResponseBody
	CompanyDTO getCompany(@PathVariable(value = "companyId") long companyId)
			throws Exception {
		CompanyDTO companyDto = null;
		try {
			Company company = companyService.getCompanyDetails(companyId);
			companyDto = CompanyDtoBuilder
					.getCompanyDtoSetupFromEntity(company);
			if(companyDto.getPhone()!=null)
			{
				String phone = companyDto.getPhone();
				phone = unmaskPhoneFax(phone);
				companyDto.setPhone(phone);
			}
			if(companyDto.getFax()!=null)
			{
				String fax = companyDto.getFax();
				fax = unmaskPhoneFax(fax);
				companyDto.setFax(fax);
			}
		} catch (Exception ex) {
			logger.error(
					"Error occurred while retrieving Company detail for company Id :"
							+ companyId, ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while fetching Company detail",
					ex.getMessage());
		}
		return companyDto;
	}

	/**
	 * Save Company
	 * 
	 * @param companyDTO
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/company/setup", method = RequestMethod.POST)
	public @ResponseBody
	Long saveCompany(@RequestBody CompanyDTO companyDTO) throws Exception {
		try {
			String phone = companyDTO.getPhone();
			if (phone != null) {
				phone = replaceSpecialCharactersFromNumber(phone);
				companyDTO.setPhone(phone);
			}
			String fax = companyDTO.getFax();
			if (fax != null) {
				fax = replaceSpecialCharactersFromNumber(fax);
				companyDTO.setFax(fax);
			}

			Long companyId = companyService.saveOrUpdateCompany(companyDTO);
			return companyId;
		} catch (Exception ex) {
			logger.error("Error occurred while saving Company detail. ", ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while saving Company detail.",
					ex.getMessage());
		}
	}

	@RequestMapping(value = "/user/company/{companyId}/users", method = RequestMethod.GET)
	public @ResponseBody
	List<UserDto> getUsers(@PathVariable(value = "companyId") long companyId)
			throws Exception {
		try {
			return userService.getUsersByCompanyId(companyId);
		} catch (Exception ex) {
			logger.error("Error occurred while fetching Users for company Id :"
					+ companyId, ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while fetching Users list", ex.getMessage());
		}
	}

	/**
	 * Replace characters ()- while saving
	 * @param number
	 * @return
	 */
	private String replaceSpecialCharactersFromNumber(String number) {
		number = number.replaceAll("[^a-zA-Z0-9]", "");
		return number;
	}
	
	/**
	 * 
	 * @param number
	 * @return
	 */
	private String unmaskPhoneFax(String number)
	{
		return String.format("(%s) %s-%s", number.substring(0, 3), number.substring(3, 6), 
			          number.substring(6, 10));
	}
}

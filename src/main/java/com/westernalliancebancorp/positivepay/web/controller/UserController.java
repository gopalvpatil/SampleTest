package com.westernalliancebancorp.positivepay.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ch.lambdaj.Lambda;
import ch.lambdaj.group.Group;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westernalliancebancorp.positivepay.dto.BankDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.CompanyDtoBuilder;
import com.westernalliancebancorp.positivepay.dto.FindUserDto;
import com.westernalliancebancorp.positivepay.dto.ManageUserDto;
import com.westernalliancebancorp.positivepay.dto.RoleDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.dto.UserPermissionDto;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.Role;
import com.westernalliancebancorp.positivepay.model.SystemMessage;
import com.westernalliancebancorp.positivepay.model.UserActivity;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.model.UserHistory;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.CrsPaidJobService;
import com.westernalliancebancorp.positivepay.service.RoleService;
import com.westernalliancebancorp.positivepay.service.SystemMessageService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * UserController is
 * 
 * @author Giridhar Duggirala
 */

@Controller
public class UserController {
	@Loggable
	private Logger logger;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private BankService bankService;

	@Autowired
	private CrsPaidJobService crsPaidJobService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private SystemMessageService systemMessageService;
	/*
	 * @InitBinder protected void initBinder(WebDataBinder binder) {
	 * binder.setValidator(new UserValidator()); }
	 */

	@RequestMapping(value = "/user/createuser", method = RequestMethod.POST)
	public void createUser(@ModelAttribute("userDto") UserDto userDto,
			Model model) {
		// UserDetail user = userService.createUser(userDTO);
		// model.addAttribute(user);
	}

	@RequestMapping(value = "/user/manageusers")
	public String manageUsers(Model model, HttpServletRequest request)
			throws Exception {

		Group<Permission> permissionGroup = userService.getLoggedInUserPermission();

		model.addAttribute("itemsList",permissionGroup.find(Permission.TYPE.ITEMS));
		model.addAttribute("manualEntryList",permissionGroup.find(Permission.TYPE.MANUAL_ENTRY));
		model.addAttribute("userRoleManagementList",permissionGroup.find(Permission.TYPE.USER_ROLE_MANAGEMENT));
		model.addAttribute("otherPermissionsList",permissionGroup.find(Permission.TYPE.OTHER_PERMISSIONS));
		model.addAttribute("paymentsList",permissionGroup.find(Permission.TYPE.PAYMENTS));
		
		
		//Get Available banks and companies
		List<BankDto> banks = new ArrayList<BankDto>();
		for(Bank bank : userService.getUserBanksByCompany()) {
			BankDto bankDto = new BankDto();
			bankDto.setId(bank.getId());
			bankDto.setBankName(bank.getName());
			banks.add(bankDto);
		}
		
		List<CompanyDTO> companies = new ArrayList<CompanyDTO>();
		for(Company company : userService.getUserCompanies()) {
			CompanyDTO companyDTO = new CompanyDTO();
			companyDTO.setId(company.getId());
			companyDTO.setName(company.getName());
			companyDTO.setBankId(company.getBank().getId());
			companies.add(companyDTO);
		}

		ObjectMapper objectMapper = new ObjectMapper(); // TODO Move code to some JSONUtility class
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		model.addAttribute("companies", objectMapper.writeValueAsString(companies));
		model.addAttribute("banks", objectMapper.writeValueAsString(banks));

		return "site.manage.users.page";
	}
	
	/**
	 * Load add user page
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/addUser", method = RequestMethod.GET)
	public String addUserPage(Model model, HttpServletRequest request)
			throws Exception {
		
		// Get Available banks and companies
		List<BankDto> banks = new ArrayList<BankDto>();
		for (Bank bank : userService.getUserBanksByCompany()) {
			BankDto bankDto = new BankDto();
			bankDto.setId(bank.getId());
			bankDto.setBankName(bank.getName());
			banks.add(bankDto);
		}

		List<CompanyDTO> companies = new ArrayList<CompanyDTO>();
		for (Company company : userService.getUserCompanies()) {
			CompanyDTO companyDTO = new CompanyDTO();
			companyDTO.setId(company.getId());
			companyDTO.setName(company.getName());
			companyDTO.setBankId(company.getBank().getId());
			companies.add(companyDTO);
		}

		ObjectMapper objectMapper = new ObjectMapper(); // TODO Move code to some JSONUtility class
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		model.addAttribute("companies",objectMapper.writeValueAsString(companies));
		model.addAttribute("banks", objectMapper.writeValueAsString(banks));
		
		List<Role> roles = roleService.findAll();
		List<RoleDto> roleDtos = new ArrayList<RoleDto>();
		for(Role role : roles){
			RoleDto roleDto = new RoleDto();
			roleDto.setRoleId(role.getId());
			roleDto.setRoleLabel(role.getLabel());
			roleDtos.add(roleDto);
		}
		model.addAttribute("roles", objectMapper.writeValueAsString(roleDtos));
		
		return "site.add.user.page";
	}
	
	/**
	 * 
	 * @param userDto
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/addUser", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(value=HttpStatus.OK)
	public void addUser(@RequestBody UserDto userDto) throws Exception {
		try {
			userService.addUser(userDto);
		} catch (Exception ex) {
			logger.error("Error occurred while adding user. ", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
					"Error occurred while adding user. " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param findUserDto
	 * @return
	 */
	@RequestMapping(value = "/user/manageUser/find", method = RequestMethod.POST)
	public @ResponseBody
	List<ManageUserDto> findUsers(@RequestBody FindUserDto findUserDto)
			throws Exception {
		try {
			//If logged in user is not bank admin then restrict search to only its allowed banks/companies
			if(!SecurityUtility.isLoggedInUserBankAdmin()) {
				List<Bank> banks = userService.getUserBanksByCompany();
				List<Company> companies = userService.getUserCompanies();
				List<Long> banksIds = Lambda.extract(banks, Lambda.on(Bank.class).getId());
				List<Long> companyIds = Lambda.extract(companies, Lambda.on(Company.class).getId());
				
				if(findUserDto.getBankId() != null && !banksIds.contains(findUserDto.getBankId()))
					throw new HttpStatusCodedResponseException(HttpStatus.FORBIDDEN, "Bank is not accesible for searching");
	
				if(findUserDto.getCompanyId() != null && !companyIds.contains(findUserDto.getCompanyId()))
					throw new HttpStatusCodedResponseException(HttpStatus.FORBIDDEN, "Company is not accesible for searching");
				
				findUserDto.setAllowedBankIds(banksIds);
				findUserDto.setAllowedCompanyIds(companyIds);
			}
			return userService.findUserBySearchCriteria(findUserDto);
		} catch(HttpStatusCodedResponseException ex) {
			throw ex;
		} catch (Exception ex) {
			logger.error("Error occurred while searching users ", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
					"Error occurred while searching users. " + ex.getMessage());
		}
	}

	/**
	 * Method to Find Users: : @author Sameer Shukla
	 * 
	 * @param model
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/findUser/{username}", method = RequestMethod.GET)
	public @ResponseBody
	UserDetail findUser(Model model, HttpServletRequest request,
			@PathVariable(value = "username") String username) throws Exception {
		try {
			// No need to check null condition, already validated on client
			// side....
			UserDetail userDetail = userService.findByName(username);
			return userDetail;
		} catch (Exception ex) {
			// Exception handler will take care....
			if (ex.getMessage().contains("No entity found"))
				throw new Exception("No user found!!!!");
			else
				throw new Exception(ex.getMessage());
		}
	}

	/**
	 * Method to associate userdetail with account.@author Sameer Shukla
	 * 
	 * @param model
	 * @param request
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/findUser/{username}/{email}/{company}", method = RequestMethod.POST)
	public @ResponseBody
	GenericResponse assignUser(Model model, HttpServletRequest request,
			@PathVariable(value = "username") String username,
			@PathVariable(value = "email") String email,
			@PathVariable(value = "company") long companyId) throws Exception {
		try {
			Set<UserDetail> userDetails = new HashSet<UserDetail>();
			UserDetail userDetail = userService.findByName(username);
			// Find company
			Company company = companyService.findById(companyId);
			if (company == null) {
				throw new Exception("No company found!!!!");
			}
			// associate company with userdetail
			// userDetail.setCompany(company);
			userDetails.add(userDetail);
			// find all accounts specific to company
			List<Account> accounts = accountService.findAllByCompanyId(String
					.valueOf(companyId));
			if (accounts != null && accounts.size() > 0) {
				Set<Account> uniqAccounts = new HashSet<Account>(accounts);
				userDetail.setAccounts(uniqAccounts);
				userService.saveOrUpdate(userDetail);
			} else {
				throw new Exception(
						"No accounts are associated with company!!!!!");
			}
			return new GenericResponse(
					"Success: User association created successfully!!!");
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * Get user detail history to show latest activity of user
	 * 
	 * @param id
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/history/get/{id}", method = RequestMethod.GET)
	public @ResponseBody
	List<UserDto> getUserDetailHistory(@PathVariable(value = "id") long id,
			@RequestParam("startIndex") Integer startIndex, @RequestParam("maxResult") Integer maxResult)
			throws Exception {
		List<UserDto> userDtoList = new ArrayList<UserDto>();
		try {
			List<UserHistory> userHistoryList = userService.getUserDetailHistoryBy(id, startIndex, maxResult);
			if (!userHistoryList.isEmpty()) {
				for (UserHistory userHistory : userHistoryList) {
					UserDto userDto = new UserDto();
					userDto.setUserId(userHistory.getUserDetail().getId());
					List<String> dateAndTime = DateUtils
							.splitDateAndTimeFromStamp(userHistory
									.getAuditInfo().getDateCreated());
					userDto.setUserActivityDate(dateAndTime.get(0));
					userDto.setUserActivityTime(dateAndTime.get(1));
					userDto.setUserActivityName(userHistory.getUserActivity()
							.getDescription());
					userDto.setUserSystemComments(userHistory
							.getSystemComment());
					userDtoList.add(userDto);
				}
			}

		} catch (RuntimeException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return userDtoList;
	}

	/**
	 * Method to fetch user permissions
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/{username}/permission", method = RequestMethod.GET)
	@ResponseBody
	public UserPermissionDto getUserRoleAndPermissions(
			@PathVariable(value = "username") String username) throws Exception {
		try {
			UserPermissionDto dto = userService
					.getUserRoleAndPermissions(username);
			return dto;
		} catch (Exception ex) {
			logger.error(
					"Error occurred while fetching user role and permission ",
					ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
					"Error occurred while fetching user role and permission. "
							+ ex.getMessage());
		}
	}

	@RequestMapping(value = "/user/manageUser/save", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public void saveManageUsers(@RequestBody List<ManageUserDto> manageUsers)
			throws Exception {
		try {
			if (manageUsers != null && !manageUsers.isEmpty()) {
				//If logged in user is not bank admin then restrict save to only its allowed banks/companies
				if(!SecurityUtility.isLoggedInUserBankAdmin()) {
					List<Bank> banks = userService.getUserBanksByCompany();
					List<Company> companies = userService.getUserCompanies();
					List<Long> allowedBanksIds = Lambda.extract(banks, Lambda.on(Bank.class).getId());
					List<Long> allowedCompanyIds = Lambda.extract(companies, Lambda.on(Company.class).getId());
					
					List<Long> bankIds = Lambda.extract(manageUsers, Lambda.on(ManageUserDto.class).getBankId());
					List<Long> companyIds = Lambda.extract(manageUsers, Lambda.on(ManageUserDto.class).getCompanyId());
					
					if(!allowedBanksIds.containsAll(bankIds) && !allowedCompanyIds.containsAll(companyIds)) {
						throw new HttpStatusCodedResponseException(HttpStatus.FORBIDDEN, "Users are not allowed to manage in selected Bank/Company");
					}
				}
				userService.saveManageUsersData(manageUsers);
			}
		} catch(HttpStatusCodedResponseException ex) {
			throw ex;
		}catch (Exception ex) {
			logger.error("Error occurred while saving users ", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),"Error occurred while saving users . " + ex.getMessage());
		}
	}

	/**
	 * Method to fetch user permissions
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/{username}/permission", method = RequestMethod.POST)
	@ResponseBody
	public void saveUserRoleAndPermissions(
			@PathVariable(value = "username") String username,
			@RequestBody UserPermissionDto request) throws Exception {
		try {
			userService.saveUserRoleAndPermissions(username, request);
		} catch (Exception ex) {
			logger.error(
					"Error occurred while saving user role and permission ", ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
					"Error occurred while saving user role and permission. "
							+ ex.getMessage());
		}
	}

	@RequestMapping(value = "/user/company/get/{companyId}", method = RequestMethod.GET)
	public @ResponseBody
	CompanyDTO getCompany(@PathVariable(value = "companyId") long companyId)
			throws Exception {
		CompanyDTO companyDto = null;
		try {
			Company company = companyService.getCompanyDetails(companyId);
			companyDto = CompanyDtoBuilder
					.getCompanyDtoSetupFromEntity(company);
		} catch (Exception exce) {
			throw new Exception(exce.getMessage());
		}
		return companyDto;
	}

	/**
	 * Exception handler that will return http response code. Mainly used for
	 * JSON request/response.
	 * 
	 * @param request
	 * @param response
	 * @param httpCodedException
	 * @return
	 */
	@ExceptionHandler(value = HttpStatusCodedResponseException.class)
	@ResponseBody
	public Object httpStatusCodedResponseException(HttpServletRequest request,
			HttpServletResponse response,
			HttpStatusCodedResponseException httpCodedException) {
		response.setStatus(httpCodedException.getStatusCode().value());
		if (httpCodedException.getResponseBody() != null)
			return httpCodedException.getResponseBody();
		else if (StringUtils.isNotBlank(httpCodedException.getMessage()))
			return httpCodedException.getMessage();
		else
			return "Error occurred while processing request";
	}

	/**
	 * @author Sameer Shukla The Exception Handler. As i can see all the methods
	 *         in the controller are catching Exception rather than concrete
	 *         exceptions, this method will return JSON response, for
	 *         ModelAndView we need to write another method.
	 */
	@ExceptionHandler(Exception.class)
	public @ResponseBody
	GenericResponse handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) {
		GenericResponse genericResponse = new GenericResponse("Failed: "
				+ ex.getMessage());
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return genericResponse;
	}

	@RequestMapping(value = "/user/dismissasread", method = RequestMethod.POST)
	public @ResponseBody Boolean dismissAsRead(HttpServletRequest request) {
		String userName = "";
		try {
			SecurityContext securityContext = SecurityContextHolder
					.getContext();
			if (securityContext.getAuthentication() != null) {
				Authentication authentication = securityContext
						.getAuthentication();
				userName = (String) authentication.getPrincipal();
			}

			
			/*Locale clientLocale = request.getLocale();  
			Calendar calendar = Calendar.getInstance(clientLocale);  
			TimeZone clientTimeZone = calendar.getTimeZone();*/
			List<SystemMessage> systemMessages = systemMessageService.getSystemMessages(SystemMessage.TYPE.POSTLOGIN);
			UserHistory userHistory = new UserHistory();
			userHistory
					.setSystemComment("User acknowledged System Maintenance Message");
			if(systemMessages!=null && systemMessages.size() > 0)
				userHistory.setUserComment("Message "+systemMessages.get(0).getId()+" has been read");
			else
				userHistory.setUserComment("");
			Boolean result = userService.saveUserHistory(
					UserActivity.Activity.SYSTEM_MESSAGE_READ, userHistory,
					userName);
			logger.debug("Result:"+result);
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
}

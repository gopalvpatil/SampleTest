package com.westernalliancebancorp.positivepay.web.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.dto.ActionDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.CheckHistoryDto;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.DataCriteriaDto;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;
import com.westernalliancebancorp.positivepay.dto.FileTypeDto;
import com.westernalliancebancorp.positivepay.dto.PaymentDetailDto;
import com.westernalliancebancorp.positivepay.dto.UserDefinedFilterDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.UserDetailDefinedFilter;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CheckHistoryService;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.ExceptionStatusService;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.ItemTypeService;
import com.westernalliancebancorp.positivepay.service.PaymentsAndItemsService;
import com.westernalliancebancorp.positivepay.service.ReasonService;
import com.westernalliancebancorp.positivepay.service.UserDetailDefinedFilterService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;


/**
 * Spring Controller for payments and items UI 
 * @author Anand Kumar
 */
@Controller
public class PaymentsAndItemsController {
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
	ExceptionStatusService exceptionStatusService;
	@Autowired
	ExceptionTypeService exceptionTypeService;
	@Autowired
	UserDetailDefinedFilterService userDetailDefinedFilterService;
    @Autowired
    private CompanyService companyService;
    @Autowired
	BankService bankService;
    @Autowired
	UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    CheckHistoryService checkHistoryService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    ItemTypeService itemTypeService;    
    @Autowired
	private BatchDao batchDao;
	
	@RequestMapping(value = "/user/paymentsanditems", method = RequestMethod.GET)
	public String showPaymentsAndItemsPage(Model model, HttpServletRequest request) throws ParseException{
		//forward to payments and items page
		return "site.payments.items.page";
	}
	
	@RequestMapping(value = "/user/paymentsSearch", method = RequestMethod.GET)
	public String showPaymentsPage(Model model, HttpServletRequest request) throws ParseException{
		//forward to payments and items page
		return "site.payments.list.page";
	}
	
	@RequestMapping(value = "/user/paymentsList", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<PaymentDetailDto> getPaymentList(@RequestBody UserDefinedFilterDto userDefinedFilterDto)
	{
		return paymentsAndItemsService.findAllPayments(userDefinedFilterDto);
	}
	
	@RequestMapping(value = "/user/itemsList", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<PaymentDetailDto> getItemList(@RequestBody UserDefinedFilterDto userDefinedFilterDto)
	{
		return paymentsAndItemsService.findAllItems(userDefinedFilterDto);
	}
	
	@RequestMapping(value = "/user/saveFilter", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody UserDetailDefinedFilter saveFilter(@RequestBody UserDefinedFilterDto userDefinedFilterDto)
	{
		return userDetailDefinedFilterService.save(userDefinedFilterDto);
	}
	
	@RequestMapping(value = "/user/duplicatecheckdetails", method = RequestMethod.GET)
	public @ResponseBody List<ExceptionalReferenceDataDto> getDuplicateCheckDetails(@RequestParam String checkNumber, @RequestParam String accountNumber, @RequestParam String itemType)
	{
		return paymentsAndItemsService.getDuplicateCheckDetails(checkNumber,accountNumber,itemType);
	}
	
	 @RequestMapping(value = "/user/companies/{bankId}", method = RequestMethod.GET)
		public @ResponseBody List<CompanyDTO> getCompaniesByBankId(@PathVariable(value = "bankId") Long bankId)
				throws Exception {
	    	return companyService.findByBankId(bankId);
		}
	 

		@RequestMapping(value = "/user/account/{companyid}", method = RequestMethod.GET)
		public @ResponseBody
		List<String> getAccountsByCompanyId(@PathVariable(value = "companyid") long companyid) throws Exception {
			List<Account> accounts = null;
			List<String> accountNumbers = new ArrayList<String>();
			try {				
				//accountHolderList = accountService.findByAccountNumberCompanyIdbank(bankid, companyid);			
				accounts = userService.getUserAccountsByCompanyId(String.valueOf(companyid));	

				for(Account account: accounts) {
					accountNumbers.add(account.getNumber());
				}
			} catch (Exception ex) {
				throw new Exception("Failed In fetching Account Numbers!!");
			}
			return accountNumbers;
		}
	 
	 @RequestMapping(value = "/user/bank", method = RequestMethod.GET)
		public @ResponseBody
		List<Bank> getBank() throws Exception {
			List<Bank> bankList = null;
			try {
				bankList = bankService.findAll();
			} catch (Exception ex) {
				throw new Exception("Failed in fetching Banks List.");
			}
			return bankList;
		}
	
	@RequestMapping(value = "/user/allpaymentstatus", method = RequestMethod.GET)
	public @ResponseBody
	List<CheckStatusDto> getAllPaymentStatus() throws Exception {
		List<CheckStatusDto> checkStatusList = null;
		try {
			checkStatusList = checkService.getDisplayableCheckStatus();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching check status List.");
		}
		return checkStatusList;
	}
	
	@RequestMapping(value = "/user/allitemtypes", method = RequestMethod.GET)
	public @ResponseBody
	List<ItemType> getAllItemTypes() throws Exception {
		List<ItemType> itemTypeList = null;
		try {
			itemTypeList = itemTypeService.findAll();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching item types List.");
		}
		return itemTypeList;
	}
	
	@RequestMapping(value = "/user/allcreatedmethods", method = RequestMethod.GET)
	public @ResponseBody
	List<FileTypeDto> getAllCreatedMethods() throws Exception {
		List<FileTypeDto> createdMethods = null;
		try {
			createdMethods = paymentsAndItemsService.getAllCreatedMethods();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching created methods List.");
		}
		return createdMethods;
	}
	
	@RequestMapping(value = "/user/allactions", method = RequestMethod.GET)
	public @ResponseBody
	List<ActionDto> getAllActions() throws Exception {
		List<ActionDto> allActions = null;
		try {
			allActions = paymentsAndItemsService.getAllAvailableActions();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching actions List.");
		}
		return allActions;
	}
	
	@RequestMapping(value = "/user/findallfilters", method = RequestMethod.GET)
	public @ResponseBody
	List<UserDefinedFilterDto> findAllfilterForLoggedInUser() throws Exception {
		return userDetailDefinedFilterService.findAllForUser();
	}
	
	@RequestMapping(value = "/user/allexceptionstatus", method = RequestMethod.GET)
	public @ResponseBody
	List<ExceptionStatus> getAllExceptionStatus() throws Exception {
		List<ExceptionStatus> exceptionStatusList = null;
		try {
			exceptionStatusList = exceptionStatusService.getAllExceptionStatus();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching check status List.");
		}
		return exceptionStatusList;
	}
	
	@RequestMapping(value = "/user/allexceptiontypes", method = RequestMethod.GET)
	public @ResponseBody
	List<ExceptionType> getAllExceptionTypes() throws Exception {
		List<ExceptionType> exceptionTypeList = null;
		try {
			exceptionTypeList = exceptionTypeService.getAllExceptionTypes();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching check status List.");
		}
		return exceptionTypeList;
	}
	
	@RequestMapping(value = "/user/allmatchstatus", method = RequestMethod.GET)
	public @ResponseBody
	List<String> getAllMatchStatus() throws Exception {
		List<String> matchList = new ArrayList<String>();
		try {
			for(Check.MATCH_STATUS matchStatus: Check.MATCH_STATUS.values()) {
				matchList.add(matchStatus.getName());
			}
		} catch (Exception ex) {
			throw new Exception("Failed in fetching check status List.");
		}
		return matchList;
	}
	
	@RequestMapping(value = "/user/paymenthistory/{checkId}", method = RequestMethod.GET)
	public @ResponseBody
	List<CheckHistoryDto> getPaymentHistory(@PathVariable(value = "checkId") long checkId) throws Exception {
		return checkHistoryService.findAllByCheckId(checkId);
	}
	
	@RequestMapping(value = "/user/itemdetails", method = RequestMethod.GET)
	public @ResponseBody PaymentDetailDto getItemDetails(@RequestParam String checkNumber, @RequestParam String accountNumber, @RequestParam String itemType)
	{
		return paymentsAndItemsService.getItemDetails(checkNumber,accountNumber,itemType);
	}
	
	
	
	@RequestMapping(value = "/user/paymentsinfo", method = RequestMethod.GET)
	public String showPaymentsInfoPage(HttpServletRequest request) throws ParseException, JsonProcessingException{
		//forward to payments and items page
		return "site.payments.info.page";
	}
	
	@RequestMapping(value = "/user/paymentsinfo/search", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<PaymentDetailDto> getPaymentListByDataCriteriaDto(@RequestBody DataCriteriaDto dataCriteriaDto)
	{
		return paymentsAndItemsService.findAllPaymentsByDataCriteria(dataCriteriaDto);
	}
	
	@RequestMapping(value = "/user/accountinfo/{accountNumber}", method = RequestMethod.GET)
	public @ResponseBody
	List<String> getAccountsByAccountNumber(@PathVariable(value = "accountNumber") long accountNumber) throws Exception {
		List<String> accountNumbers;
		try {				
			accountNumbers = accountService.getAccountInfoByAccountNumber(String.valueOf(accountNumber));
		} catch (Exception ex) {
			throw new Exception("Failed In fetching Account Numbers!!");
		}
		return accountNumbers;
	}
	
	@RequestMapping(value = "/user/saveaccountinfo", method = RequestMethod.POST,
	produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Long saveAccountInfo(@RequestBody CheckDto checkDTO)
	{
		return checkService.saveAccountInfo(checkDTO);
	}
	
	@RequestMapping(value = "/user/alladminactions", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String,String> getAllAdminActionsForStatus(@RequestBody PaymentDetailDto paymentDetailDto)  throws Exception 
	{
		  Map<String,String> actionsMap = new HashMap<String, String>();
		  CheckStatus checkstatus;
		  try {
			  checkstatus = checkService.getLatestCheckStatus(paymentDetailDto.getCheckId());
	          WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(paymentDetailDto.getWorkflowId());
	          actionsMap = workflowManager.getPresentableActionsForStatus(checkstatus.getName());
		  } catch (Exception ex) {
			  throw new Exception("Failed in fetching check status List.");
		  }
		  return addNonWorkFlowActionsForAdmin(checkstatus.getName(),actionsMap);
	}
	
	@RequestMapping(value = "/user/allnonworkflowactions", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getAvailableActions() {
        Map<String, String> availableActions = new HashMap<String, String>();
        availableActions.put("changeCheckNumber","Change check number");
        availableActions.put("changeAccountNumber","Change account number");
        availableActions.put("deleteDuplicate","Delete Duplicate");
        return availableActions;
    }
	
	private Map<String,String> addNonWorkFlowActionsForAdmin(String status, Map<String, String> actionsMap) {
		Map<String, String> defaultNonWorkFlowActions = new HashMap<String, String>();
		defaultNonWorkFlowActions.put("changePayee", "Change Payee");
		defaultNonWorkFlowActions.put("changeCheckDate", "Change Check Date");
		
		Map<String, String> voidNonWorkFlowActions = new HashMap<String, String>();
		voidNonWorkFlowActions.put("changePayee", "Change Payee");
		voidNonWorkFlowActions.put("changeCheckDate", "Change Check Date");
		voidNonWorkFlowActions.put("removeVoid", "Remove Void");
		
		Map<String, String> stopNonWorkFlowActions = new HashMap<String, String>();
		stopNonWorkFlowActions.put("changePayee", "Change Payee");
		stopNonWorkFlowActions.put("changeCheckDate", "Change Check Date");
		stopNonWorkFlowActions.put("removeStop", "Remove Stop");
		
		Map<String,Map<String, String>> statusWorkFlowActionsMap = new HashMap<String, Map<String, String>>();
		statusWorkFlowActionsMap.put("paidNotIssued", new HashMap<String, String>());
		statusWorkFlowActionsMap.put("void", voidNonWorkFlowActions);
		statusWorkFlowActionsMap.put("stop", stopNonWorkFlowActions);
        
        Map<String, String> nonworkFlowActions = statusWorkFlowActionsMap.get(status);
        if(nonworkFlowActions == null) {
        	defaultNonWorkFlowActions.putAll(actionsMap);
        	return defaultNonWorkFlowActions;
        } else {
        	nonworkFlowActions.putAll(actionsMap);
        	return nonworkFlowActions;
        }
	}
	
	@RequestMapping(value = "/user/fetchCompanies/{bankIds}/{fetchAll}", method = RequestMethod.GET)
	public @ResponseBody
	List<CompanyDTO> getSelectedCompanies(@PathVariable(value = "bankIds") String bankIds, @PathVariable(value = "fetchAll") Boolean fetchAll) throws Exception {
		if(bankIds == null)
			throw new Exception("Banks ids cannot be null");
		
		String[] bankArray = bankIds.split("-");
		List<CompanyDTO> companiesList = new ArrayList<CompanyDTO>();
		try {
			List<Long> ids = new ArrayList<Long>();
			for(String d : bankArray)
			{
				if(d.equals("-"))
				{
					break;
				}
				ids.add(Long.parseLong(d));
			}
			companiesList = batchDao.findSelectedCompanies(ids, fetchAll);
		} catch (Exception ex) {
			throw new Exception("Failed in fetching companies List.");
		}
		return companiesList;
	}
	
	@RequestMapping(value = "/user/accounts/{bankIds}/{compIds}", method = RequestMethod.GET)
	public @ResponseBody
	List<AccountDto> getSelectedAccounts(@PathVariable(value = "bankIds") String bankIds, @PathVariable(value = "compIds") String compIds) throws Exception {
		if(bankIds == null && compIds == null)
			throw new Exception("Company and Bank Ids cannot be null");
		
		String[] bankArray = bankIds.split("-");
		String[] compArray = compIds.split("-");
		List<AccountDto> accountsList = new ArrayList<AccountDto>();
		try {
			List<Long> bids = new ArrayList<Long>();
			List<Long> cids = new ArrayList<Long>();
			for(String d : bankArray)
			{
				if(d.equals("-"))
				{
					break;
				}
				bids.add(Long.parseLong(d));
			}
			for(String d : compArray)
			{
				if(d.equals("-"))
				{
					break;
				}
				cids.add(Long.parseLong(d));
			}
			accountsList = batchDao.findSelectedAccounts(cids, bids);
		} catch (Exception ex) {
			throw new Exception("Failed in fetching accounts List.");
		}
		return accountsList;
	}
	
	@RequestMapping(value = "/user/zeroitemdetails", method = RequestMethod.GET)
	public @ResponseBody PaymentDetailDto getZeroCheckDetails(@RequestParam String traceNumber)
	{
		return batchDao.findItemDetailsByTraceNumber(traceNumber);
	}
	
}

package com.westernalliancebancorp.positivepay.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.westernalliancebancorp.positivepay.dto.AccountInfoForCustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.AccountPaymentInfoDto;
import com.westernalliancebancorp.positivepay.dto.CustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.DashboardDto;
import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.dto.PaymentByDateDto;
import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.dto.ReportDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.DashboardService;
import com.westernalliancebancorp.positivepay.service.ExceptionalCheckService;
import com.westernalliancebancorp.positivepay.service.FileMetaDataService;
import com.westernalliancebancorp.positivepay.service.ReportService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.ExceptionalCheckComparator;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;


/**
 * User:	Gopal Patil
 * Date:	Feb 11, 2014
 * Time:	12:23:53 PM
 */
@Controller
public class DashboardController {

	@Loggable
	private Logger logger;

    @Autowired
    FileMetaDataService fileMetaDataService;

    @Autowired
    CheckService checkService;
    
	@Autowired
	ExceptionalCheckService exceptionalCheckService;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	UserService userService;

    @Autowired
    DashboardService dashboardService;
    
	@Autowired
	ReportService reportService;
	
    @Value("#{'${dashboard.customer.payment.status.list}'.split(',')}") 
    private List<String> paymentStatusList;
    
    @Value("#{'${dashboard.customer.payment.status.sub.list}'.split(',')}") 
    private List<String> paymentStatusSubList;
    
    @Value("#{'${dashboard.customer.chart.payment.byitem.list}'.split(',')}") 
    private List<String> paymentsByItemsType;

	@RequestMapping(value = "/user/dashboard")
	public String viewDashboard(Model model, HttpServletRequest request)
			throws Exception {
		if(SecurityUtility.isLoggedInUserBankAdmin()) {
			logger.info("User is Admin so forwarding to admin dashboard");
			return adminDashboard(model, request);
		} else {
			logger.info("User is customer so forwarding to customer dashboard");
			return customerDashboard(model, request);
		}
	} 
	
	public String customerDashboard(Model model, HttpServletRequest request)
			throws Exception {
		String loggerInUsername = SecurityUtility.getPrincipal();
		List<String> accountNumbers = dashboardService.getUsersActiveAccountNumbers(loggerInUsername);
		model.addAttribute("accountNumbers", accountNumbers);
		model.addAttribute("paymentStatusList", paymentStatusList);
		model.addAttribute("paymentStatusSubList", paymentStatusSubList);
		//Check file download permissions
  		if (SecurityUtility.hasPermission(Permission.NAME.DOWNLOAD_FILES)) {
  			logger.info("User can download files");
  			model.addAttribute("DOWNLOAD_FILES_PERMISSION", "TRUE");
  		}
		return "site.view.customer.dashboard.page";
	}

	public String adminDashboard(Model model, HttpServletRequest request)
			throws Exception {

		String companyName = null;
		FileMetaData.STATUS status = null;
		Date dateCreated = null;
		boolean isForDay = false;
		
		List<DashboardDto> dashboardDtoList = new ArrayList<DashboardDto>();
		List<FileMetaData> fileMetaDataList = fileMetaDataService.findDashboardFileMetaData(companyName, status, dateCreated, isForDay);
		if(!fileMetaDataList.isEmpty()) {
	        Map<Long, Integer> processedRecordCountMap = checkService.getProcessedItemsCountOfFile(fileMetaDataList);
	        Map<Long, Integer> unProcessedRecordCountMap = checkService.getUnProcessedItemsCountOfFile(fileMetaDataList);
	        for (FileMetaData fileMetaData : fileMetaDataList) {
	            if (!fileMetaData.getFileName().equals(FileMetaData.MANUAL_ENTRY_FILE_NAME) &&
	                    !fileMetaData.getFileName().equals(FileMetaData.MIGRATED_FILE_NAME) &&
                        !fileMetaData.getFileName().equals(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_FILE_NAME)) {
	                DashboardDto dashboardDto = new DashboardDto();
	                dashboardDto.setFileMetaDataId(fileMetaData.getId());
	                if(fileMetaData.getFileMapping() != null)
	                	dashboardDto.setCompanyName(fileMetaData.getFileMapping().getCompany().getName());
	                dashboardDto.setUserName(fileMetaData.getAuditInfo().getCreatedBy());
	                dashboardDto.setFileName(fileMetaData.getFileName());
	                dashboardDto.setOriginalFileName(fileMetaData.getOriginalFileName());
	                if(fileMetaData.getFileMapping() != null)
	                	dashboardDto.setFileType(fileMetaData.getFileMapping().getFileType());
	                dashboardDto.setUploadedDate(DateUtils.getStringFromDateTime(fileMetaData.getAuditInfo().getDateCreated()));
	                dashboardDto.setStatus(fileMetaData.getStatus().getDescription());
	                dashboardDto.setItemsReceived(fileMetaData.getItemsReceived()==null?"0":fileMetaData.getItemsReceived()+"");
	                if (fileMetaData.getId() == null || processedRecordCountMap.get(fileMetaData.getId()) == null) {
	                    dashboardDto.setItemsLoaded("0");
	                } else {
	                    dashboardDto.setItemsLoaded(processedRecordCountMap.get(fileMetaData.getId()) + "");
	                }
	                if (fileMetaData.getId() == null || unProcessedRecordCountMap.get(fileMetaData.getId()) == null) {
	                    dashboardDto.setErrorRecordsLoaded("0");
	                } else {
	                    dashboardDto.setErrorRecordsLoaded(unProcessedRecordCountMap.get(fileMetaData.getId()) + "");
	                }
	                dashboardDtoList.add(dashboardDto);
	            }
	        }
		}
        model.addAttribute("dashboardDtoList", dashboardDtoList);
		return "site.view.admin.dashboard.page";
	}

	/**
	 * This method is invoked when the user wants to save and complete Job
	 *
	 * @param request
	 * @param model
	 * @return next page
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/searchfiles")
	public String searchDashboardFileMetaData(@ModelAttribute("dashboardDto") DashboardDto dashboardDto, Model model, HttpServletRequest request)
			throws Exception {

		String companyName = null;
		Date dateCreated = null;
		boolean isForDay = false;
		FileMetaData.STATUS status = null;

		if(!dashboardDto.getCompanyNameSearchCriteria().equals("All")) {
			companyName = dashboardDto.getCompanyNameSearchCriteria();
		}
		if(!dashboardDto.getStatusSearchCriteria().equals("All")) {
			if(dashboardDto.getStatusSearchCriteria().equals(FileMetaData.STATUS.PROCESSED.getDescription())) {
				status = FileMetaData.STATUS.PROCESSED;
			}else{
				status = FileMetaData.STATUS.UNPROCESSED;						
			}
		}
		if(!dashboardDto.getDateRangeSearchCriteria().equals("All")) {
			int days = Integer.parseInt(dashboardDto.getDateRangeSearchCriteria());	
			// For Today and Yesterday:: date range condition is for a day only
			if(days == 0 || days == 1) {
				isForDay = true;
			}			
			dateCreated = DateUtils.nextDate(new Date(), -days, 0, 0, 0);
		}
		List<DashboardDto> dashboardDtoList = new ArrayList<DashboardDto>();
		List<FileMetaData> fileMetaDataList = fileMetaDataService.findDashboardFileMetaData(companyName, status, dateCreated, isForDay);
		if(!fileMetaDataList.isEmpty()) {
	        Map<Long, Integer> processedRecordCountMap = checkService.getProcessedItemsCountOfFile(fileMetaDataList);
	        Map<Long, Integer> unProcessedRecordCountMap = checkService.getUnProcessedItemsCountOfFile(fileMetaDataList);
			for(FileMetaData fileMetaData : fileMetaDataList) {
				DashboardDto dashboardDto1 = new DashboardDto();
				dashboardDto1.setFileMetaDataId(fileMetaData.getId());
				if(fileMetaData.getFileMapping() != null)
					dashboardDto1.setCompanyName(fileMetaData.getFileMapping().getCompany().getName());
				dashboardDto1.setUserName(fileMetaData.getAuditInfo().getCreatedBy());
				dashboardDto1.setFileName(fileMetaData.getFileName());
				dashboardDto1.setOriginalFileName(fileMetaData.getOriginalFileName());
				if(fileMetaData.getFileMapping() != null)
					dashboardDto1.setFileType(fileMetaData.getFileMapping().getFileType());
				dashboardDto1.setUploadedDate(DateUtils.getStringFromDateTime(fileMetaData.getAuditInfo().getDateCreated()));
				dashboardDto1.setStatus(fileMetaData.getStatus().getDescription());
				dashboardDto1.setItemsReceived(fileMetaData.getItemsReceived()==null?"0":fileMetaData.getItemsReceived()+"");
	            if (fileMetaData.getId() == null || processedRecordCountMap.get(fileMetaData.getId()) == null) {
	            	dashboardDto1.setItemsLoaded("0");
	            } else {
	            	dashboardDto1.setItemsLoaded(processedRecordCountMap.get(fileMetaData.getId()) + "");
	            }
	            if (fileMetaData.getId() == null || unProcessedRecordCountMap.get(fileMetaData.getId()) == null) {
	            	dashboardDto1.setErrorRecordsLoaded("0");
	            } else {
	            	dashboardDto1.setErrorRecordsLoaded(unProcessedRecordCountMap.get(fileMetaData.getId()) + "");
	            }
				dashboardDtoList.add(dashboardDto1);
			}
		}
		model.addAttribute("dashboardDtoList", dashboardDtoList);
		model.addAttribute("companyNameSearchCriteria", dashboardDto.getCompanyNameSearchCriteria());
		model.addAttribute("statusSearchCriteria", dashboardDto.getStatusSearchCriteria());
		model.addAttribute("dateRangeSearchCriteria", dashboardDto.getDateRangeSearchCriteria());		
		return "site.view.admin.dashboard.page";
	}
	
	@RequestMapping(value = "/user/errorrecords/get/{fileMetaDataId}", method = RequestMethod.GET)
	public @ResponseBody
	List<ItemErrorRecordsDto> getItemErrorRecords(@PathVariable(value = "fileMetaDataId") long fileMetaDataId)
			throws Exception {
		List<ItemErrorRecordsDto> itemErrorRecordsList = new ArrayList<ItemErrorRecordsDto>();
		try {
			List<ExceptionalCheck> exceptionalChecksList = exceptionalCheckService.findErrorRecordsUploadedBy(fileMetaDataId);
			if (!exceptionalChecksList.isEmpty()) {				
				ExceptionalCheckComparator exceptionalCheckComparator = new ExceptionalCheckComparator();				
				Collections.sort(exceptionalChecksList, exceptionalCheckComparator);
				
				for (ExceptionalCheck exceptionalCheck : exceptionalChecksList) {
					ItemErrorRecordsDto itemErrorRecordsDto = new ItemErrorRecordsDto();
					itemErrorRecordsDto.setFileLineNumber(exceptionalCheck.getLineNumber());
					itemErrorRecordsDto.setAccountNumber(exceptionalCheck.getAccountNumber());
					itemErrorRecordsDto.setRoutingNumber(exceptionalCheck.getRoutingNumber());
					itemErrorRecordsDto.setCheckNumber(exceptionalCheck.getCheckNumber());
					itemErrorRecordsDto.setIssueCode(exceptionalCheck.getIssueCode());
					itemErrorRecordsDto.setIssueAmount(exceptionalCheck.getIssuedAmount());
					itemErrorRecordsDto.setIssueDate(exceptionalCheck.getIssueDate());
					itemErrorRecordsDto.setPayee(exceptionalCheck.getPayee());
					itemErrorRecordsDto.setExceptionTypeName(exceptionalCheck.getExceptionType().getDescription());				
					itemErrorRecordsList.add(itemErrorRecordsDto);
				}
			}
		} catch (RuntimeException ex) {
			throw new RuntimeException(ex.getMessage());
		}
		return itemErrorRecordsList;
	}
	
	@RequestMapping(value = "/user/company", method = RequestMethod.GET)
	public @ResponseBody
	List<Company> getCompany() throws Exception {
		List<Company> companyList = null;
		try {
			companyList = companyService.findAll();
		} catch (Exception ex) {
			throw new Exception("Failed in fetching Companies List.");
		}
		return companyList;
	}
	
	@RequestMapping(value = "/user/recentfiles", method = RequestMethod.GET)
    public @ResponseBody List<RecentFileDto> getRecentFilesForCustomerDashboard() {
    	List<FileMetaData> fileMetaDataList = fileMetaDataService.findRecentFilesUploaded();
    	List<RecentFileDto> allFiles = new ArrayList<RecentFileDto>();
    	if(!fileMetaDataList.isEmpty()) {
    		Map<Long, Integer> processedRecordCountMap = checkService.getProcessedItemsCountOfFile(fileMetaDataList);
	        Map<Long, Integer> unProcessedRecordCountMap = checkService.getUnProcessedItemsCountOfFile(fileMetaDataList);
	    	for(FileMetaData fileMetaData: fileMetaDataList) {
	    		RecentFileDto recentFile = new RecentFileDto();
	    		recentFile.setFileMetaDataId(fileMetaData.getId());
	    		recentFile.setFileName(fileMetaData.getOriginalFileName());
	    		recentFile.setNoOfRecords(fileMetaData.getItemsReceived());
	    		recentFile.setUploadDate(fileMetaData.getAuditInfo().getDateCreated());
	    		recentFile.setFileUid(fileMetaData.getFileName());
	    		if(fileMetaData.getFileMapping() != null)
	    			recentFile.setCompanyName(fileMetaData.getFileMapping().getCompany().getName());
	    		//Get All accountNumbers for that file
	    		//List<String> accountNumbers = fileMetaDataService.getAccountNumbersAssociatedWithUploadedFile(fileMetaData.getId());
	    		//recentFile.setAccountNumbersInFile(accountNumbers);
	    		if (fileMetaData.getId() == null || processedRecordCountMap.get(fileMetaData.getId()) == null) {
	    			recentFile.setItemsLoaded(new Long(0));
                } else {
                	recentFile.setItemsLoaded(processedRecordCountMap.get(fileMetaData.getId()).longValue());
                }
                if (fileMetaData.getId() == null || unProcessedRecordCountMap.get(fileMetaData.getId()) == null) {
                	recentFile.setErrorRecordsLoaded(new Long(0));
                } else {
                	recentFile.setErrorRecordsLoaded(unProcessedRecordCountMap.get(fileMetaData.getId()).longValue());
                }
                allFiles.add(recentFile);
	    	}
    	}
    	return allFiles;
    }
	
	@RequestMapping(value = "/user/accountInfo", method = RequestMethod.GET)
    public @ResponseBody AccountInfoForCustomerDashboardDto getAccountInfoForCustomerDashboard() {
        return checkService.getCustomerAccountInfo();
    }
	
	
	@RequestMapping(value = "/user/dashboard/reports")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody List<ReportDto> getReportsAndExtractsData() {
		List<ReportDto> list = new ArrayList<ReportDto>();
		
		List<ReportTemplate> reportTemplateList = reportService.findReportTemplatesForLoggedInUser();
		for(ReportTemplate template : reportTemplateList) {
			ReportDto reportDtoNew = new ReportDto();

			String packageName = "";
			if(template.getAccountServiceOption().compareTo("F") == 0) {
				packageName = "Full Recon";
			} else if(template.getAccountServiceOption().compareTo("P") == 0) {
				packageName = "Positive Pay";
			}
			
			reportDtoNew.setPackageName(packageName);
			reportDtoNew.setTemplateId(template.getId());
			reportDtoNew.setTemplateName(template.getName());
			reportDtoNew.setTemplateFileName(template.getTemplateFileName());
			reportDtoNew.setReportType(template.getReportTemplateType().getName());
			
			list.add(reportDtoNew);
		}
		
		return list;
	}

    /**
     * Get the payment information of each account that a user has
     * grouped by item type
     * @return
     * @throws ParseException 
     */
	@RequestMapping(value="/user/dashboard/allaccounts/payments")
	@ResponseStatus(value=HttpStatus.OK)
	public @ResponseBody List<CustomerDashboardDto.PaymentData> getAllAccountsPaymentsData() throws ParseException{
		
		
		String loggerInUsername = SecurityUtility.getPrincipal();
		List<String> accountNumbers = dashboardService.getUsersActiveAccountNumbers(loggerInUsername);
		
		//Initialize payment data with empty values
		List<CustomerDashboardDto.PaymentData> paymentDatas = initializePaymentDataForCharts(accountNumbers);
		
		//Get Date from DB and merge in paymentDatas
		List<AccountPaymentInfoDto> accountPaymentInfoDtos = dashboardService.getLoggedInnUserAllAccountsPaymentsData(accountNumbers,paymentsByItemsType);
		for(CustomerDashboardDto.PaymentData paymentData : paymentDatas) {
			for(AccountPaymentInfoDto accountPaymentInfoDto : accountPaymentInfoDtos) {
				if(accountPaymentInfoDto.getAccountNumber().equals(paymentData.getAccountNumber())) {
					paymentData.getAmountByStatus().put(accountPaymentInfoDto.getCheckStatusName(), accountPaymentInfoDto.getTotalAmount());
					paymentData.getCountByStatus().put(accountPaymentInfoDto.getCheckStatusName(), accountPaymentInfoDto.getTotalCount());
				}
			}
		}
		
		// Fetch paid amount and counts by date
		List<PaymentByDateDto> paymentsByDate = dashboardService.getAllPaymentsByDate(accountNumbers, null, null);

		//populatePaymentDataWithDateRanges(paymentDatas, null, null);
		for (PaymentByDateDto paymentByDateDto : paymentsByDate) {
			for (CustomerDashboardDto.PaymentData paymentData : paymentDatas) {
				if(paymentData.getAccountNumber().equals(paymentByDateDto.getAccountNumber())) {
					paymentData.getAmountByDate().put(DateUtils.wdf.format(paymentByDateDto.getPaymentDate()),paymentByDateDto.getAmount());
					paymentData.getCountByDate().put(DateUtils.wdf.format(paymentByDateDto.getPaymentDate()),paymentByDateDto.getCount());
				}
			}
		}
		
		//Combine the data for ALL drop down
		if(paymentDatas != null) {
			CustomerDashboardDto.PaymentData allPaymentData = combinePaymentData(paymentDatas);
			paymentDatas.add(allPaymentData);
		}
				
		return paymentDatas;
	}

	/**
	 * 
	 * @param accountNumbers
	 * @return
	 * @throws ParseException
	 */
	private List<CustomerDashboardDto.PaymentData> initializePaymentDataForCharts(List<String> accountNumbers) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		//Get Start of calendar
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -7);
		
		Map<String,Double> amountByDate = new HashMap<String, Double>();
		Map<String,Long> countByDate = new HashMap<String, Long>();
		for(int i=-6;i<=0;i++) {
			calendar.add(Calendar.DATE, 1);
			amountByDate.put(sdf.format(calendar.getTime()), 0.0);
    		countByDate.put(sdf.format(calendar.getTime()), 0L);
			
		}
		
		List<CustomerDashboardDto.PaymentData> paymentDatas = new ArrayList<CustomerDashboardDto.PaymentData>();
        for(String accountNumber : accountNumbers) {
        	CustomerDashboardDto.PaymentData paymentData = new CustomerDashboardDto.PaymentData();
        	paymentData.setAccountNumber(accountNumber);
			paymentData.getAmountByDate().putAll(amountByDate);
			paymentData.getCountByDate().putAll(countByDate);
			paymentDatas.add(paymentData);
		}
        return paymentDatas;
	}
	
	/**
	 * Method to combine all payment data to display on UI for ALL accounts
	 * @param paymentDatas
	 * @return
	 */
	private CustomerDashboardDto.PaymentData combinePaymentData(List<CustomerDashboardDto.PaymentData> paymentDatas) {
		CustomerDashboardDto.PaymentData allPaymentData = new CustomerDashboardDto.PaymentData();
		for(CustomerDashboardDto.PaymentData paymentData : paymentDatas) {
			for(String checkStatus : paymentData.getAmountByStatus().keySet()) {
				Double oldValue = allPaymentData.getAmountByStatus().get(checkStatus);
				Double val = paymentData.getAmountByStatus().get(checkStatus);
				allPaymentData.getAmountByStatus().put(checkStatus, (oldValue == null ? 0.0 : oldValue ) + (val == null ? 0.0 : val ));
			}
			for(String checkStatus : paymentData.getCountByStatus().keySet()) {
				Integer oldValue = allPaymentData.getCountByStatus().get(checkStatus);
				Integer val = paymentData.getCountByStatus().get(checkStatus);
				allPaymentData.getCountByStatus().put(checkStatus, (oldValue == null ? 0 : oldValue ) + (val == null ? 0 : val));
			}
			for(String date : paymentData.getAmountByDate().keySet()) {
				Double oldValue = allPaymentData.getAmountByDate().get(date);
				Double val = paymentData.getAmountByDate().get(date);
				allPaymentData.getAmountByDate().put(date, (oldValue == null ? 0.0 : oldValue ) + (val == null ? 0.0 : val ));
			}
			for(String date : paymentData.getCountByDate().keySet()) {
				Long oldValue = allPaymentData.getCountByDate().get(date);
				Long val = paymentData.getCountByDate().get(date);
				allPaymentData.getCountByDate().put(date, (oldValue == null ? 0 : oldValue )  + (val == null ? 0 : val));
			}
		}
		return allPaymentData;
	}

}



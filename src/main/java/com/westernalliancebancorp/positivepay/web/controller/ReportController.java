package com.westernalliancebancorp.positivepay.web.controller;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.dto.ReportDto;
import com.westernalliancebancorp.positivepay.dto.ReportOptionalParameterDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Report;
import com.westernalliancebancorp.positivepay.model.ReportBankCompanyAccountParameter;
import com.westernalliancebancorp.positivepay.model.ReportParameterOption;
import com.westernalliancebancorp.positivepay.model.ReportParameterOptionValue;
import com.westernalliancebancorp.positivepay.model.ReportTemplate;
import com.westernalliancebancorp.positivepay.report.ReportProcessor;
import com.westernalliancebancorp.positivepay.report.impl.ReportProcessorFactory;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.JobHistoryService;
import com.westernalliancebancorp.positivepay.service.ReportService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

import java.util.Collections;

/**
 * User:	Gopal Patil
 * Date:	Feb 7, 2014
 * Time:	12:29:35 AM
 */

@Controller
@SessionAttributes
public class ReportController {
	@Loggable
	private Logger logger;
	@Autowired
	private ReportService reportService;
	@Autowired
    private BankService bankService;
    @Autowired
    private UserService userService;
    @Autowired
    private CompanyService companyService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private JobHistoryService jobHistoryService;	
	@Autowired
	private BasicDataSource dataSource;
	
	@RequestMapping(value="/report/getOptionalParametersForReportTemplate/{reportId}/{reportTemplateId}", method = RequestMethod.GET)
	public @ResponseBody List<ReportOptionalParameterDto> getOptionalParameters(@PathVariable(value = "reportId") Long reportId, @PathVariable(value = "reportTemplateId") Long reportTemplateId) {
		List<ReportOptionalParameterDto> ret = new ArrayList<ReportOptionalParameterDto>();
		
		String principalLoginName = SecurityUtility.getPrincipal();
		Report report = reportService.findById(reportId);
		ReportTemplate template = reportService.getReportTemplateById(reportTemplateId);                
		
		if (template != null) {
			Set<ReportParameterOption> setOptionalParameters = template.getReportParameterOptions();
			if (setOptionalParameters != null && setOptionalParameters.size() > 0) {
		        List<ReportParameterOption> parameterList = new ArrayList<ReportParameterOption>(setOptionalParameters);
		        Collections.sort(parameterList);
		        
				for (ReportParameterOption option :parameterList) {
					ReportOptionalParameterDto param = new ReportOptionalParameterDto();
					param.setName(option.getName());
					param.setDisplayName(option.getDisplayName());
					param.setType(option.getDataType());
					ret.add(param);
				}
		
				if (report != null) {
					for (ReportParameterOptionValue value : report.getReportParameterOptionValues()) {
						for (ReportOptionalParameterDto param : ret) {
							if (param.getName().compareTo(value.getReportParameterOption().getName()) == 0) {
								param.setValueChar(value.getValueChar());
								param.setOperator(value.getOperator());
								if(value.getValueDateStart() != null) {
									param.setValueDateFrom(DateUtils.wdf.format(value.getValueDateStart()));
								}
								
								if(value.getValueDateEnd() != null) {
									param.setValueDateTo(DateUtils.wdf.format(value.getValueDateEnd()));
								}
								
								param.setValueDateSymbolic(value.isValueDateSymbolic() == null ? false : value.isValueDateSymbolic());
								param.setValueDateFromSymbolicValue(value.getValueDateStartSymbolicValue());
								param.setValueDateToSymbolicValue(value.getValueDateEndSymbolicValue());
							}
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	@RequestMapping(value="/report/getCompaniesForReport/{reportId}", method = RequestMethod.POST)
	public @ResponseBody List<CompanyDTO> getCompanies(@PathVariable(value = "reportId") Long reportId, @RequestBody int[] checked) {
		String principalLoginName = SecurityUtility.getPrincipal();
		
		List<CompanyDTO> ret = new ArrayList<CompanyDTO>();
		List<Long> bankIds = new ArrayList<Long>();				
		for (int i : checked) {
			bankIds.add(new Long(i));
		}		

		List<Company> companies = null;
		try {
			if (bankIds.size() == 1 && bankIds.get(0) == -1) {
				companies = companyService.findAllByUserName(principalLoginName);
			} else {
				companies = companyService.findAllByBankIds(bankIds);				
			}
			
			Report report = reportService.findById(reportId);
			
			for (Company company : companies) {
				Long companyId = company.getId();
				
				if(company.isActive() == true) {
					CompanyDTO companyDto = new CompanyDTO();
					companyDto.setId(companyId);
					companyDto.setCompanyName(company.getName());
									
					if (report != null) {
						for (ReportBankCompanyAccountParameter parameter : report.getReportBankCompanyAccountParameters()) {
							if (parameter.getCompany() != null) { 
								Long companyParameterId = parameter.getCompany().getId();
		
								if (companyParameterId.compareTo(companyId) == 0) {
									companyDto.setSelected(true);
								}
							}
						}
					}
					
					ret.add(companyDto);
				}
			}
		} catch (Exception e) {
			logger.debug("*** exception {}", e.getMessage());
			e.printStackTrace();
		}		
		
		return ret;
	}
	
	@RequestMapping(value="/report/getAccountsForReport/{reportId}", method = RequestMethod.POST)
	public @ResponseBody List<AccountDto> getAccounts(@PathVariable(value = "reportId") Long reportId, @RequestBody int[] checked) {
		List<AccountDto> ret = new ArrayList<AccountDto>();
		
		List<Long> companyIds = new ArrayList<Long>();				
		for (int i : checked) {
			companyIds.add(new Long(i));
		}		
		
		List<Account> accounts = accountService.findAllByCompanyIds(companyIds);	
		Report report = reportService.findById(reportId);
		
		for (Account account : accounts) {
			if(account.isActive() == true) {
				AccountDto accountDto = new AccountDto();
				accountDto.setId(account.getId());
				accountDto.setAccountName(account.getName());
				accountDto.setAccountNumber(account.getNumber());
				
				if (report != null) {
					for (ReportBankCompanyAccountParameter parameter : report.getReportBankCompanyAccountParameters()) {
						if (parameter.getAccount() != null && parameter.getAccount().getId().compareTo(account.getId()) == 0) {
							accountDto.setSelected(true);
						}
					}
				}
				
				ret.add(accountDto);
			}
		}
		
		return ret;
	}

	/**
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/report/view")
	public String viewReport(Model model, HttpServletRequest request)
			throws Exception {
		String userName = SecurityUtility.getPrincipal();
			
		List<Bank> banks = bankService.findAll();
		List<Company> companies = companyService.findAll();
		List<Account> accounts = accountService.findAll();
		
    	model.addAttribute("banks", banks);
    	model.addAttribute("companies", companies);
    	model.addAttribute("accounts", accounts);
    	//model.addAttribute("displayBankSelection", true);

    	List<ReportDto> reportDtoList = new ArrayList<ReportDto>();	
    	List<Report> reportList = reportService.findAllReportsByUserName(userName);
    	
    	for (Report report : reportList) {
    		ReportDto reportDtoNew = new ReportDto();
			reportDtoNew.setTemplateId(report.getReportTemplate().getId());
			reportDtoNew.setTemplateName(report.getReportTemplate().getName());
			reportDtoNew.setTemplateFileName(report.getReportTemplate().getTemplateFileName());
			reportDtoNew.setReportId(report.getId());
			reportDtoNew.setReportName(report.getName());
			reportDtoNew.setReportType(report.getReportTemplate().getReportTemplateType().getName());
			reportDtoNew.setDateCreated(DateUtils.wdf.format(report.getAuditInfo().getDateCreated()));
			reportDtoNew.setIsFavorite(true);
			
			String asOfDate = DateUtils.wdf.format(report.getAsOfDate());			
			reportDtoNew.setAsOfDate(asOfDate);
						
			reportDtoNew.setAsOfDateIsSymbolic(report.getAsOfDateIsSymbolic());
			reportDtoNew.setAsOfDateSymbolicValue(report.getAsOfDateSymbolicValue());
			reportDtoNew.setOutputFormat(report.getOutputFormat());
			reportDtoList.add(reportDtoNew);	
		}
    	
		List<ReportTemplate> reportTemplateList = reportService.findReportTemplatesForLoggedInUser();
		for (ReportTemplate template : reportTemplateList) {
			ReportDto reportDtoNew = new ReportDto();
			reportDtoNew.setTemplateId(template.getId());
			reportDtoNew.setTemplateName(template.getName());
			reportDtoNew.setTemplateFileName(template.getTemplateFileName());
			reportDtoNew.setReportId((long) -1);
			reportDtoNew.setReportType(template.getReportTemplateType().getName());
			
			reportDtoList.add(reportDtoNew);
		}
		
		model.addAttribute("reportDtoList", reportDtoList);
		model.addAttribute("username", userName);
		
		return "site.report.view.page";
	}		

	@RequestMapping(value="/report/save", method = RequestMethod.POST)
	public @ResponseBody Report saveReport(@ModelAttribute("reportDto") ReportDto reportDto, HttpServletRequest request) {
		Report ret = null;
		try {
			String userName = SecurityUtility.getPrincipal();

			ReportTemplate template = reportService.getReportTemplateById(reportDto.getTemplateId());
						
			reportDto.setTemplateFileName(template.getTemplateFileName());
			reportDto.setTemplateName(template.getName());

			Report report = null;
			if (reportDto.getReportId() == -1) {
				report = new Report();
			} else {
				report = reportService.findById(reportDto.getReportId());
			}
			
			if (report != null) {
				report.setName(reportDto.getReportName());	

				try {
				    report.setAsOfDate(DateUtils.wdf.parse(reportDto.getAsOfDate()));
				} catch (ParseException e) {
				    e.printStackTrace();
					report.setAsOfDate(new Date());
				}
				
				report.setAsOfDateIsSymbolic(reportDto.getAsOfDateIsSymbolic() == true ? true : false);
				report.setAsOfDateSymbolicValue(reportDto.getAsOfDateSymbolicValue());
				report.setOutputFormat(reportDto.getOutputFormat());
				report.setReportTemplate(template);
				
				Set<ReportBankCompanyAccountParameter> parameters = report.getReportBankCompanyAccountParameters();
				parameters.clear();
				
				for (Long id : reportDto.getBankIds()) {
					Bank bank = bankService.findById(id);
					if (bank != null) {
						ReportBankCompanyAccountParameter param = new ReportBankCompanyAccountParameter();
						param.setBank(bank);
						param.setReport(report);
						parameters.add(param);
					} 
				}
	
				for (Long id : reportDto.getCompanyIds()) {
					Company company = companyService.findById(id);
					if (company != null) {
						ReportBankCompanyAccountParameter param = new ReportBankCompanyAccountParameter();
						param.setCompany(company);
						param.setReport(report);
						parameters.add(param);
					} 
				} 
	
				for (Long id : reportDto.getAccountIds()) {
					Account account = accountService.findById(id);
					if (account != null) {
						ReportBankCompanyAccountParameter param = new ReportBankCompanyAccountParameter();
						param.setAccount(account);
						param.setReport(report);
						parameters.add(param);
					} 
				} 
	
				Set<ReportParameterOptionValue> optionalParameters = report.getReportParameterOptionValues();
				logger.debug("*** optional parameters {}", optionalParameters.size());
				optionalParameters.clear();
				
				for (ReportParameterOption option : template.getReportParameterOptions()) {
					ReportParameterOptionValue optionValue = new ReportParameterOptionValue();
					optionValue.setReport(report);
					optionValue.setReportParameterOption(option);
					
					if (option.getDataType().compareTo("char") == 0) {
						String valueForm = request.getParameter(option.getName());
						logger.debug("*** value form {} {}", valueForm, option.getName());
						if (valueForm != null) {
							optionValue.setValueChar(valueForm);
						}
					} else if (option.getDataType().compareTo("date") == 0) {
						String isSymbolic = request.getParameter(option.getName() + "IsSymbolic");
						String operator = request.getParameter(option.getName() + "Operator");
						optionValue.setOperator(operator);
						
						logger.debug("*** is symbolic {}", isSymbolic);
						logger.debug("*** operator {}", operator);
						
						if (isSymbolic == null) {
							String valueFromDate = request.getParameter(option.getName() + "FromDate");
							String valueToDate = request.getParameter(option.getName() + "ToDate");															

							if(valueFromDate != null) {
								try {
								    optionValue.setValueDateStart(DateUtils.wdf.parse(valueFromDate));
								} catch (ParseException e) {
								    e.printStackTrace();
								    optionValue.setValueDateStart(new Date());
								}
							}
							
							if(valueToDate != null) {
								try {
								    optionValue.setValueDateEnd(DateUtils.wdf.parse(valueToDate));
								} catch (ParseException e) {
								    e.printStackTrace();
								    optionValue.setValueDateEnd(new Date());
								}
							}
							
							optionValue.setValueDateIsSymbolic(false);							
						} else {
							String valueFromDateSymbolic = request.getParameter(option.getName() + "FromDateSymbolic");
							String valueToDateSymbolic = request.getParameter(option.getName() + "ToDateSymbolic");															
						    optionValue.setValueDateStartSymbolicValue(valueFromDateSymbolic);
						    optionValue.setValueDateEndSymbolicValue(valueToDateSymbolic);
						    optionValue.setValueDateIsSymbolic(true);
						}							
					}
					
					optionalParameters.add(optionValue);
				}
				
				report.setReportBankCompanyAccountParameters(parameters);	
				report.setReportParameterOptionValues(optionalParameters);
				Report reportFromDb = reportService.saveOrUpdate(report);
				ret = reportFromDb;
			}
		} catch (Exception e) {
			logger.debug("*** exception = {}", e.getMessage());	
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@RequestMapping(value="/report/run/{reportId}", method = RequestMethod.GET)
	public void runReport(@PathVariable(value = "reportId") Long reportId,  
			HttpServletRequest request, HttpServletResponse response) {
		
		Report report = reportService.findById(reportId);
		if (report != null) {
			String outputFormat = report.getOutputFormat();				
			ReportProcessorFactory reportProcessorFactory = new ReportProcessorFactory();	
			ReportProcessor reportProcessor = reportProcessorFactory.buildReportProcessor(outputFormat);
			JasperPrint jp = reportProcessor.buildReport(report, dataSource);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();	
			reportProcessor.exportReport(jp, baos);	
			reportProcessor.writeReport(response, baos, report.getReportTemplate().getTemplateFileName());
		}
	}
	
	@RequestMapping(value="/report/deleteById", method = RequestMethod.POST)
	public @ResponseBody Integer deleteReport(@RequestParam Long deleteReportId) { 
		int ret = 1;
		
		try {			
			Report report = reportService.findById(deleteReportId);
			if (report != null) {
				reportService.delete(report);
			}
		}
		catch(Exception e) {
			ret = 0;
		}
		
		return ret;
	}
}

package com.westernalliancebancorp.positivepay.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westernalliancebancorp.positivepay.dto.ItemErrorRecordsDto;
import com.westernalliancebancorp.positivepay.dto.JobDto;
import com.westernalliancebancorp.positivepay.dto.JobDtoBuilder;
import com.westernalliancebancorp.positivepay.dto.JobStepHistoryDto;
import com.westernalliancebancorp.positivepay.dto.JobTypeDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Job;
import com.westernalliancebancorp.positivepay.model.JobActionType;
import com.westernalliancebancorp.positivepay.model.JobCriteriaData;
import com.westernalliancebancorp.positivepay.model.JobHistory;
import com.westernalliancebancorp.positivepay.model.JobStep;
import com.westernalliancebancorp.positivepay.model.JobStepHistory;
import com.westernalliancebancorp.positivepay.model.JobType;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.JobHistoryService;
import com.westernalliancebancorp.positivepay.service.JobService;
import com.westernalliancebancorp.positivepay.service.JobStepHistoryService;
import com.westernalliancebancorp.positivepay.service.JobStepService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.web.validator.JobValidator;

/**
 * @author Gopal Patil
 * 
 */
@Controller
@SessionAttributes
public class JobController {

	@Loggable
	private Logger logger;

	@Autowired
	JobValidator jobValidator;

	@Autowired
	JobService jobService;

	@Autowired
	AccountService accountService;

	@Autowired
	BankService bankService;

	@Autowired
	CompanyService companyService;

	@Autowired
	JobHistoryService jobHistoryService;
	
	@Autowired
	JobStepHistoryService jobStepHistoryService;
	
	@Autowired
	UserService userService;

	@Autowired
	JobStepService jobStepService;

	@RequestMapping(value = "/job/viewjob")
	public String viewJob(Model model, HttpServletRequest request)
			throws Exception {	
		List<JobHistory> jobHistoryList = jobHistoryService.findAllJobs();		
		JobDtoBuilder builder =  new JobDtoBuilder();		
		List<JobDto> jobDtoList = builder.getJobDtoListFromHistory(jobHistoryList);
		
		model.addAttribute("jobDtoList", jobDtoList);
		return "site.view.job.page";
	}

	@RequestMapping(value = "/job/editjob")
	public String editJob(Model model, HttpServletRequest request,
			@RequestParam String id) throws Exception {

		Job job = jobService.findJobById(id);
		JobDtoBuilder builder = new JobDtoBuilder();
		JobDto jobDto = builder.getJobDtoFromJob(job);

		String jobRunOn = job.getRunDay();
		if (jobRunOn != null && StringUtils.hasText(jobRunOn)) {
			this.setRunOn(request, DateUtils.jobRunOnDaysList(jobRunOn));
		}
		
		//Both Set Interval and Set Start time should not be selected.
		String intervalTime = job.getIntervalTime();
		if (intervalTime != null && StringUtils.hasText(intervalTime)) {
			jobDto.setIntervalTime(this.setIntervalTime(intervalTime));
		} else {	
			String startTime = job.getStartTime();
			if (startTime != null && StringUtils.hasText(startTime)) {
				this.setStartRunTime(request, startTime);
			}
		}
		jobDto.setOlderStartDateTime(jobDto.getJobStartDate() + " " + DateUtils.convertTo12HoursFormat(jobDto.getJobRunTime()));
		
		String endTime = job.getEndTime();
		if (endTime != null && StringUtils.hasText(endTime)) {
			this.setEndRunTime(request, endTime);
		}

		model.addAttribute("edit", "true");
		model.addAttribute("jobDto", jobDto);		
		return "site.continue.job.page";
	}

	@RequestMapping(value = "/job/createjob")
	public String createJob(Model model, HttpServletRequest request)
			throws Exception {
		return "site.create.job.page";
	}

	@RequestMapping(value = "/job/continuejob")
	public String continueJob(Model model, HttpServletRequest request)
			throws Exception {
		return "site.continue.job.page";
	}

	@RequestMapping(value = "/job/cancelcontinuejob")
	public String cancelContinueJob(Model model, HttpServletRequest request)
			throws Exception {
		return "site.create.job.page";
	}
	
	@RequestMapping(value = "/job/errors")
	public String showErrors(@ModelAttribute("jobDto") JobDto jobDto, Model model, HttpServletRequest request)
			throws Exception {
		List<ItemErrorRecordsDto> list = jobStepHistoryService.fetchErrorsDto(jobDto.getJobActualStartTime(), jobDto.getJobActualEndTime(), jobDto.getTimezone());
		model.addAttribute("itemErrorList", list);
		return "site.job.error.page";
	}

	@RequestMapping(value = "/job/savejob")
	public String saveJob(@ModelAttribute("jobDto") JobDto jobDto,
			@RequestParam String action, BindingResult result,
			HttpServletRequest request, Model model) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		// if action is cancel forward to prev page
		if (action.equals("cancel")) {
			return "site.create.job.page";
		}		
		if(request.getParameter("edit") != null) {			
			boolean editMode = Boolean.parseBoolean(request.getParameter("edit"));			
			jobDto.setEditMode(editMode);
		}		
		// Set job run time and job run days
		jobDto.setIntervalTime(this.getIntervalTime(request));
		jobDto.setJobRunDay(this.getRunOn(request));
		jobDto.setJobRunTime(this.getStartRunTime(request));
		jobDto.setJobEndRunTime(this.getEndRunTime(request));
		// validate input
		jobValidator.validate(jobDto, result);

		if (result.hasErrors()) {
			String jobRunOn = jobDto.getJobRunDay();
			if (jobRunOn != null && StringUtils.hasText(jobRunOn)) {
				this.setRunOn(request, DateUtils.jobRunOnDaysList(jobRunOn));
			}
			String jobStartRunTime = jobDto.getJobRunTime();
			if (jobStartRunTime != null && StringUtils.hasText(jobStartRunTime)) {
				this.setUIStartRunTime(request, jobStartRunTime);
			}			
			String jobEndRunTime = jobDto.getJobEndRunTime();
			if (jobEndRunTime != null && StringUtils.hasText(jobEndRunTime)) {
				this.setEndRunTime(request, jobEndRunTime);
			}
			String intervalTime = jobDto.getIntervalTime();
			if (intervalTime != null && StringUtils.hasText(intervalTime)) {
				jobDto.setIntervalTime(this.setIntervalTime(intervalTime));
			}
			if (jobDto.isEditMode()) {
				String id = request.getParameter("id");
				jobDto.setJobId(Long.parseLong(id));
				model.addAttribute("edit", "true");
			}
			model.addAttribute("oldJob", jobDto);
			model.addAttribute("errors", "true");
			return "site.continue.job.page";
		}

		if (jobDto.isIndefinitely()) {
			jobDto.setJobEndDate("12/31/9999");
		}

		if (jobDto.isEditMode()) {
			String id = request.getParameter("id");
			jobDto.setJobId(Long.parseLong(id));
			model.addAttribute("edit", "true");
			List<JobStep> jobStepsList = jobService.findJobStepByJobId(jobDto.getJobId());
			List<JobDto> jobDtoList = new ArrayList<JobDto>();
			for (JobStep jobStep : jobStepsList) {
				// Start: All steps will have same continue job page information
				JobDto jobDtoEdit = new JobDto();
				jobDtoEdit.setJobStepId(jobStep.getId());
				jobDtoEdit.setJobName(jobDto.getJobName());
				jobDtoEdit.setJobDescription(jobDto.getJobDescription());
				jobDtoEdit.setJobFrequency(jobDto.getJobFrequency());
				jobDtoEdit.setJobStartDate(jobDto.getJobStartDate());
				jobDtoEdit.setJobRunTime(jobDto.getJobRunTime());
				jobDtoEdit.setJobEndDate(jobDto.getJobEndDate());
				jobDtoEdit.setJobEndRunTime(jobDto.getJobEndRunTime());
				jobDtoEdit.setTimezone(jobDto.getTimezone());
				jobDtoEdit.setJobRunDay(jobDto.getJobRunDay());
				jobDtoEdit.setIntervalTime(jobDto.getIntervalTime());
				jobDtoEdit.setIndefinitely(jobDto.isIndefinitely());
				jobDtoEdit.setWeekly(jobDto.isWeekly());
				// End: All steps will have same continue job page information

				// Start: Job steps information
				jobDtoEdit.setJobStepId(jobStep.getId());
				jobDtoEdit.setJobStepName(jobStep.getName());
				jobDtoEdit.setJobStepDescription(jobStep.getDescription());

				jobDtoEdit.setJobTypeId(jobStep.getJobType().getId());
				JobActionType jobActionType = jobService.findJobActionTypeById(jobStep.getJobActionType().getId());
				if(jobActionType!=null)
					jobDtoEdit.setJobActionTypeName(jobActionType.getName());
				jobDtoEdit.setJobActionTypeId(jobStep.getJobActionType()
						.getId());

				// find criteria
				List<JobCriteriaData> jobCriteria = jobService
						.fetchCriteriaByStep(jobStep.getId());
				for (JobCriteriaData jobCriteriaData : jobCriteria) {
					if (jobCriteriaData.getCriteriaName().name()
							.equals(JobCriteriaData.CRITERIA_NAME.BANK.name())) {
						String csvValues = jobCriteriaData.getValue();
						if (csvValues != null && !csvValues.isEmpty()) {
							for (String value : csvValues.split(",")) {
								jobDtoEdit.setJobBankCriteria(value);
							}
						}
					}
					if (jobCriteriaData
							.getCriteriaName()
							.name()
							.equals(JobCriteriaData.CRITERIA_NAME.COMPANY
									.name())) {
						String csvValues = jobCriteriaData.getValue();
						if (csvValues != null && !csvValues.isEmpty()) {
							for (String value : csvValues.split(",")) {
								jobDtoEdit.setJobCustomerCriteria(value);
							}
						}
					}
					if (jobCriteriaData
							.getCriteriaName()
							.name()
							.equals(JobCriteriaData.CRITERIA_NAME.ACCOUNT
									.name())) {
						String csvValues = jobCriteriaData.getValue();
						if (csvValues != null && !csvValues.isEmpty()) {
							for (String value : csvValues.split(",")) {
								jobDtoEdit.setJobAccountCriteria(value);
							}
						}
					}
				}
				jobDtoList.add(jobDtoEdit);
			}
			String jobSteps = mapper.writeValueAsString(jobDtoList);
			model.addAttribute("jobSteps", jobSteps);
		}

		String jobsType = mapper.writeValueAsString(getJobType());
		model.addAttribute("jobsType", jobsType);

		model.addAttribute("continueJobDto", jobDto);
		return "site.save.job.page";
	}

	@RequestMapping(value = "/job/cancel")
	public String cancelButtonJob(Model model, HttpServletRequest request)
			throws Exception {
		return "site.continue.job.page";
	}

	/**
	 * This method is invoked when the user wants to see success Job
	 * 
	 * @param request
	 * @param model
	 * @return next page
	 * @throws Exception
	 */
	@RequestMapping(value = "/job/successjob/{edit}/{id}/{jobStepId}")
	public @ResponseBody
	String successJob(@RequestBody JobDto[] jobDto,
			@PathVariable(value = "edit") boolean edit,
			@PathVariable(value = "id") long id,
			@PathVariable(value = "jobStepId") long jobStepId,
			BindingResult result, HttpServletRequest request, Model model)
			throws Exception {

		Set<JobStep> jobSteps = new HashSet<JobStep>();
		JobDtoBuilder jobDtoBuilder = new JobDtoBuilder();
		try {			
			if (edit) {	
				byte i = 1;
				jobDto[0].setJobId(id);
				Job j = jobDtoBuilder.getJobFromDto(jobDto[0]);	
				
				if (jobStepId != 0) {					
					for (JobDto job : jobDto) {
						JobStep jobStep = jobDtoBuilder.getJobStepFromDto(job);
						jobStep.setSequence(Byte.valueOf(i));
	
						JobType jobType = jobService.findJobTypeById(job.getJobTypeId());
						jobStep.setJobType(jobType);
	
						if (job.getJobActionTypeId() != null) {
							JobActionType jobActionType = jobService.findJobActionTypeById(job.getJobActionTypeId());
							jobStep.setJobActionType(jobActionType);
						}
						jobStep.setId(jobDto[0].getJobStepId());
						jobSteps.add(jobStep);
						jobStep.setJob(j);
						j.setJobStep(jobSteps);
						i++;
					}
				} else if (jobStepId == 0) {
					for (JobDto job : jobDto) {
						JobStep jobStep = jobDtoBuilder.getJobStepFromDto(job);
						jobStep.setSequence(Byte.valueOf(i));
	
						JobType jobType = jobService.findJobTypeById(job.getJobTypeId());
						jobStep.setJobType(jobType);
	
						if (job.getJobActionTypeId() != null) {
							JobActionType jobActionType = jobService.findJobActionTypeById(job.getJobActionTypeId());
							jobStep.setJobActionType(jobActionType);
						}
	
						jobSteps.add(jobStep);
						jobStep.setJob(j);
						j.setJobStep(jobSteps);
						i++;
					}						
				}
				jobService.update(j);
			} else {
				// Prepare JobObject once and then move ahead.
				Job j = jobDtoBuilder.getJobFromDto(jobDto[0]);
				byte i = 1;
				for (JobDto job : jobDto) {
					JobStep jobStep = jobDtoBuilder.getJobStepFromDto(job);
					jobStep.setSequence(Byte.valueOf(i));
					JobType jobType = jobService.findJobTypeById(job.getJobTypeId());
					jobStep.setJobType(jobType);
					if (job.getJobActionTypeId() != null) {
						JobActionType jobActionType = jobService.findJobActionTypeById(job.getJobActionTypeId());
						jobStep.setJobActionType(jobActionType);
					}
					jobSteps.add(jobStep);
					jobStep.setJob(j);
					j.setJobStep(jobSteps);
					i++;
				}
				// Once JobSteps are ready save all steps.
				jobService.save(j);
			}
		} catch (Exception e) {
			logger.error("Exception is thrown by: Job Controller And Exception is: "+ e.getMessage(), e);
			throw new Exception();
		}
		// If no exception show success...
		GenericResponse genericResponse = new GenericResponse("Jobs Created Successfully");
		genericResponse.setCode("200");
		return "site.create.job.page";
	}


	/**
	 * @author Sameer Shukla
	 * @return
	 * @throws Exception
	 * 
	 *             Behavior is to pull the mapping of radio's(Type) and Action
	 *             Type. Tweaked the behavior to pull the mapping is if there is
	 *             no relationship exists then no need to display the extra
	 *             type.
	 * 
	 *             Filtering criteria taken care on UI.
	 */
	@RequestMapping(value = "/job/fetchJobType", method = RequestMethod.GET)
	public @ResponseBody
	List<JobTypeDto> getJobType() throws Exception {
		List<JobType> jobTypes = null;
		List<JobTypeDto> jobTypeDtoList = new ArrayList<JobTypeDto>();
		try {
			jobTypes = jobService.findActiveJobTypes();			
			if(!jobTypes.isEmpty()) {
				for(JobType jobType : jobTypes) {					
					JobTypeDto jobTypeDto = new JobTypeDto();
					jobTypeDto.setId(jobType.getId());
					jobTypeDto.setName(jobType.getName());					
					List<JobActionType> jobActionTypeList = jobService.findJobActionTypeByJobTypeId(jobType.getId());	
					for(JobActionType jobActionType : jobActionTypeList) {
				      JobActionType obj = new JobActionType(); 
				      obj.setId(jobActionType.getId());
				      obj.setName(jobActionType.getName());
				      obj.setSpringBeanName(jobActionType.getSpringBeanName());
				      jobTypeDto.getJobActionTypes().add(obj);
				     }
					//jobTypeDto.setJobActionTypes(jobActionTypeList);
					jobTypeDtoList.add(jobTypeDto);
				}				
			}else {
				throw new Exception("Job Types are not present");
			}
			
		} catch (Exception ex) {
			throw new Exception("Failed in fetching job and action types.");
		}
		return jobTypeDtoList;	
	}

	/**
	 * @author Sameer Shukla
	 * @return
	 * @throws Exception
	 * 
	 *             Fetching BankList separately aling with CompanyList, as no
	 *             need to pull the entire accountlist. better to pass bankid
	 *             and company id
	 */
	@RequestMapping(value = "/job/bank", method = RequestMethod.GET)
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

	@RequestMapping(value = "/job/bank/{companyId}", method = RequestMethod.GET)
	public @ResponseBody
	List<Bank> getBankByCompanyId(@PathVariable(value = "companyId") long companyId) throws Exception {
		List<Bank> bankList = null;
		try {
			Company company = companyService.findById(companyId);
			Bank bank = bankService.findById(company.getBank().getId());
			if (bank != null) {
				bankList = new ArrayList<Bank>();
				bankList.add(bank);				
			}
		} catch (Exception ex) {
			throw new Exception("Failed in fetching Banks List.");
		}
		
		return bankList;
	}

	/**
	 * @author Sameer Shukla
	 * @return
	 * @throws Exception
	 * 
	 *             Fetching BankList separately aling with CompanyList, as no
	 *             need to pull the entire accountlist. better to pass bankid
	 *             and company id
	 */
	@RequestMapping(value = "/job/company", method = RequestMethod.GET)
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

	/**
	 * @author Sameer Shukla
	 * @param companyid
	 * @return
	 * @throws Exception
	 * 
	 *  Pass company id and pull the account numbers.
	 */
	@RequestMapping(value = "/job/account/{companyid}", method = RequestMethod.GET)
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

	@ExceptionHandler(Exception.class)
	public @ResponseBody
	GenericResponse handleException(HttpServletRequest request,
			HttpServletResponse response, Exception ex) {
		GenericResponse genericResponse = new GenericResponse(
				"Job Creation Failed:" + ex.getMessage());
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		return genericResponse;
	}

	@RequestMapping(value = "/job/deleteJobById")
	public String deleteJobById(@RequestBody JobDto jobDto) throws Exception {
		try {
			if (jobDto.getJobId() != null) {
				jobService.deleteSelectedJobById(jobDto.getJobId());
			} else {
				logger.info("No Job is selected to delete");
			}
		} catch (Exception e) {
			logger.error(
					"Exception is thrown by: Job Controller And Exception is: "
							+ e.getMessage(), e);
		}
		return "site.view.job.page";
	}

	@RequestMapping(value = "/job/runJob", method = RequestMethod.POST)
	public @ResponseBody
	GenericResponse runJob(@RequestBody JobDto jobDto, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		GenericResponse genericResponse = null;
		try {
			if (jobDto.getSelectedIds() != null) {
				String[] selectedIdsArray = jobDto.getSelectedIds();
				List<String> selectedIdsList = Arrays.asList(selectedIdsArray);
				jobService.runSelectedJobs(selectedIdsList);
				genericResponse = new GenericResponse(
						"Job has run successfully.");
			} else {
				genericResponse = new GenericResponse(
						"No Job is selected to run");
				logger.info("No Job is selected to run");
			}
		} catch (Exception e) {
			logger.error(
					"Exception is thrown by: Job Controller And Exception is: "
							+ e.getMessage(), e);
			e.printStackTrace();
			genericResponse = new GenericResponse(
					"Job has not run." + e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		return genericResponse;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		dateFormat.setLenient(false);

		// true passed to CustomDateEditor constructor means convert empty
		// String to null
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));
	}

	private String getRunOn(final HttpServletRequest request) {

		StringBuilder runOn = new StringBuilder();

		if (request.getParameter("monday") != null) {
			String monday = request.getParameter("monday");
			runOn.append(monday + ",");
		}
		if (request.getParameter("tuesday") != null) {
			String tuesday = request.getParameter("tuesday");
			runOn.append(tuesday + ",");
		}
		if (request.getParameter("wednesday") != null) {
			String wednesday = request.getParameter("wednesday");
			runOn.append(wednesday + ",");
		}
		if (request.getParameter("thursday") != null) {
			String thursday = request.getParameter("thursday");
			runOn.append(thursday + ",");
		}
		if (request.getParameter("friday") != null) {
			String friday = request.getParameter("friday");
			runOn.append(friday + ",");
		}
		if (request.getParameter("saturday") != null) {
			String saturday = request.getParameter("saturday");
			runOn.append(saturday + ",");
		}
		if (request.getParameter("sunday") != null) {
			String sunday = request.getParameter("sunday");
			runOn.append(sunday + ",");
		}

		if (runOn.length() > 0)
			runOn.deleteCharAt(runOn.length() - 1);

		return runOn.toString();
	}

	private HttpServletRequest setRunOn(final HttpServletRequest request,
			List<String> jobRunOnList) {
		if (jobRunOnList.contains("MON"))
			request.setAttribute("monday", "MON");
		if (jobRunOnList.contains("TUE"))
			request.setAttribute("tuesday", "TUE");
		if (jobRunOnList.contains("WED"))
			request.setAttribute("wednesday", "WED");
		if (jobRunOnList.contains("THU"))
			request.setAttribute("thursday", "THU");
		if (jobRunOnList.contains("FRI"))
			request.setAttribute("friday", "FRI");
		if (jobRunOnList.contains("SAT"))
			request.setAttribute("saturday", "SAT");
		if (jobRunOnList.contains("SUN"))
			request.setAttribute("sunday", "SUN");
		return request;
	}

	private String getStartRunTime(final HttpServletRequest request) {
		String hour = request.getParameter("jobStartHour");
		String minute = request.getParameter("jobStartMinute");
		String meridiem = request.getParameter("jobStartMeridiem");
		if(hour != null && minute != null && meridiem != null) {
			return hour + ":" + minute + " " + meridiem;
		}else{
			return "";
		}
	}
	
	private void setStartRunTime(HttpServletRequest request, String startTime) {		
		try {
			String time = DateUtils.convertTo12HoursFormat(startTime);			
			String[] time1 = time.split(":");
			String[] time2 = time1[1].split(" ");  	
			request.setAttribute("jobStartHour",time1[0]);
			request.setAttribute("jobStartMinute",time2[0]);
			request.setAttribute("jobStartMeridiem",time2[1]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void setUIStartRunTime(HttpServletRequest request, String startTime) {		
		String[] time1 = startTime.split(":");
		String[] time2 = time1[1].split(" ");  	
		request.setAttribute("jobStartHour",time1[0]);
		request.setAttribute("jobStartMinute",time2[0]);
		request.setAttribute("jobStartMeridiem",time2[1]);
	}

	private String getEndRunTime(final HttpServletRequest request) {
		String hour = request.getParameter("jobEndHour");
		String minute = request.getParameter("jobEndMinute");
		String meridiem = request.getParameter("jobEndMeridiem");
		if(hour != null && minute != null && meridiem != null) {
			return hour + ":" + minute + " " + meridiem;
		}else{
			return "";
		}
	}
	
	private void setEndRunTime(HttpServletRequest request, String endTime) {		
		try {
			String time = DateUtils.convertTo12HoursFormat(endTime);			
			String[] time1 = time.split(":");
			String[] time2 = time1[1].split(" ");  	
			request.setAttribute("jobEndHour",time1[0]);
			request.setAttribute("jobEndMinute",time2[0]);
			request.setAttribute("jobEndMeridiem",time2[1]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private String getIntervalTime(final HttpServletRequest request) {
		String minString = request.getParameter("intervalTime");
		if(minString != null) {
			if(minString.equals("0")) {
				return "";
			} else {
				long min = Long.parseLong(minString);
				long hours = TimeUnit.MINUTES.toHours(min);
				long remainMinutes = min - TimeUnit.HOURS.toMinutes(hours);
				return String.format("%02d:%02d", hours, remainMinutes);
			}
		} else {
			return "";
		}
	}
	
	private String setIntervalTime(String intervalTime) {		
		String[] timeArray = intervalTime.split("\\:");	
		long hours = Long.parseLong(timeArray[0]);	
		long minutes = Long.parseLong(timeArray[1]);
		if(hours > 0) {
			minutes +=  hours*60;
		}
		return minutes+"";		
	}
	
	@RequestMapping(value = "/job/jobstephistory/{jobId}", method = RequestMethod.GET)
	public @ResponseBody
	List<JobStepHistoryDto> getJobStepHistory(@PathVariable(value = "jobId") long jobId)
			throws Exception {
		logger.debug("JobId:" + jobId);
		List<JobStepHistoryDto> jobStepHistoryDtoList = new ArrayList<JobStepHistoryDto>();
		try {
			if (jobId > 0) {
				List<JobStepHistory> jobStepHistoryList = jobStepHistoryService.findJobStepHistoryByJobId(jobId);
				JobDto jobDto = jobService.findLastJobConfigurationBy(jobId);
				String jobActualStartTime = ""; 
				String jobActualEndTime = "";
				
				if(jobDto.getJobActualStartTime() != null && StringUtils.hasText(jobDto.getJobActualStartTime())) {
					try {
						jobActualStartTime = DateUtils.getWALFormatDateTime(jobDto.getJobActualStartTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} 
				
				if(jobDto.getJobActualEndTime() != null && StringUtils.hasText(jobDto.getJobActualEndTime())) {
					try {
						jobActualEndTime = DateUtils.getWALFormatDateTime(jobDto.getJobActualEndTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} 
				
				if(!jobStepHistoryList.isEmpty()) {					
					for(JobStepHistory jobStepHistory : jobStepHistoryList) {
						JobStepHistoryDto jobStepHistoryDto = new JobStepHistoryDto();
						//Job details
						jobStepHistoryDto.setJobId(jobStepHistory.getJob().getId());
						jobStepHistoryDto.setJobTimezone(jobStepHistory.getJob().getTimezone());
						jobStepHistoryDto.setJobActualStartTime(jobActualStartTime);
						jobStepHistoryDto.setJobActualEndTime(jobActualEndTime);
						
						//Job Step Details
						jobStepHistoryDto.setJobStepName(jobStepHistory.getJobStep().getName());						
						jobStepHistoryDto.setJobStepId(jobStepHistory.getJobStep().getId());
						jobStepHistoryDto.setJobType(jobStepHistory.getJobStep().getJobType().getName());
						jobStepHistoryDto.setJobStepStatus(jobStepHistory.getJobStatusType().getName());						
				
						String jobActionType = jobStepHistory.getJobStep().getJobActionType().getSpringBeanName();
						
						if(jobActionType.equals("crsPaidFileJobTask") || jobActionType.equals("dailyStopFileJobTask") || jobActionType.equals("stopReturnedFileJobTask")) {							
							jobStepHistoryDto.setJobStepNumOfFilesProcessed(jobStepHistory.getNumberItemsProcessed());						
							jobStepHistoryDto.setJobStepNumOfFilesFailed(jobStepHistory.getNumberOfErrors());						
							
							String fileType = "";
							if(jobActionType.equals("crsPaidFileJobTask")){
								fileType = Constants.CRS_PAID;
							} else if(jobActionType.equals("dailyStopFileJobTask")) {
								fileType = Constants.DAILY_STOP;
							} else if(jobActionType.equals("stopReturnedFileJobTask")) {
								fileType = Constants.STOP_PRESENTED;
							}							
							Date jobStepActualEndTime;
							
							//If job run less than a minute; end date time adds a minute to query between clause
							if (jobStepHistory.getActualStartTime().compareTo(jobStepHistory.getActualEndTime()) == 0) {
								jobStepActualEndTime = DateUtils.nextDate(jobStepHistory.getActualEndTime(), 0, 0, 1, 0);										
							} else {
								jobStepActualEndTime = jobStepHistory.getActualEndTime();
							}
							//File saved in database as per server timezone, so have to convert job step user date to server timezone
							Date jobAStepStartDate = DateUtils.convertDateToServerTimezone(jobStepHistory.getActualStartTime(), jobStepHistory.getJob().getTimezone());
							Date jobAStepEndDate = DateUtils.convertDateToServerTimezone(jobStepActualEndTime, jobStepHistory.getJob().getTimezone());							
							Long itemsProcesedCount = jobStepService.findJobStepNumOfItemsProcessedInFile(jobAStepStartDate, jobAStepEndDate, fileType);
							Long errorCount = jobStepService.findJobStepNumOfErrorsInFile(jobAStepStartDate, jobAStepEndDate, fileType);
							
							List<String> fileNames = jobStepService.findJobStepFileNames(jobAStepStartDate, jobAStepEndDate, fileType);
							StringBuilder fileNameBuilder = new StringBuilder();	
							String prefix = "";
							if(!fileNames.isEmpty()) {
								if(fileNames.size() > 1) {
									for(String name : fileNames) {
										fileNameBuilder.append(prefix);
										prefix = ",  ";
										fileNameBuilder.append(name);
									}			
									jobStepHistoryDto.setJobStepFilename(fileNameBuilder.toString());
								} else if(fileNames.size() == 1) {
									jobStepHistoryDto.setJobStepFilename(fileNames.get(0));					
								}				
							}	
							
							jobStepHistoryDto.setJobStepNumOfItemsProcessed(itemsProcesedCount);							
							jobStepHistoryDto.setJobStepNumOfErrors(errorCount);
							jobStepHistoryDto.setShowErrorLink(true);
						} else {							
							jobStepHistoryDto.setJobStepNumOfFilesProcessed(0l);						
							jobStepHistoryDto.setJobStepNumOfFilesFailed(0l);
							
							jobStepHistoryDto.setJobStepNumOfItemsProcessed(jobStepHistory.getNumberItemsProcessed());						
							jobStepHistoryDto.setJobStepNumOfErrors(jobStepHistory.getNumberOfErrors());
							jobStepHistoryDto.setShowErrorLink(false);
						}						
						
						jobStepHistoryDto.setJobStepActualStartTime(DateUtils.getStringFromDateTime(jobStepHistory.getActualStartTime()));
						jobStepHistoryDto.setJobStepActualEndTime(DateUtils.getStringFromDateTime(jobStepHistory.getActualEndTime()));
						jobStepHistoryDto.setComments(jobStepHistory.getComments());	
						jobStepHistoryDtoList.add(jobStepHistoryDto);
					}					
				}
			} else {
				throw new Exception(new IllegalArgumentException("Invalid JobId Passed!!!"));
			}
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
		return jobStepHistoryDtoList;
	}

    @RequestMapping(value = "/job/fetchJobStatus", method = RequestMethod.GET)
    public
    @ResponseBody
    List<JobDto> fetchJobStatus() throws Exception {
        List<JobDto> jobDtoList = new ArrayList<JobDto>();
        try {
            jobDtoList = jobHistoryService.findallJobsJdbcFetch();
            for (JobDto jobDto : jobDtoList) {
                if ( jobDto.getJobLastRunDate() != null && StringUtils.hasText( jobDto.getJobLastRunDate()) ) {
                    String jobLastRunDate = DateUtils.getWALFormatDateTime(jobDto.getJobLastRunDate());
                    jobDto.setJobLastRunDate(jobLastRunDate);
                } else {
                	jobDto.setJobLastRunDate("");
                }
                
                if ( jobDto.getJobNextRunDate() != null &&  StringUtils.hasText( jobDto.getJobNextRunDate() )) {
	                String jobNextRunDate = DateUtils.getWALFormatDateTime(jobDto.getJobNextRunDate());
	                jobDto.setJobNextRunDate(jobNextRunDate);
                } else {
                	jobDto.setJobNextRunDate("");
                }                
            }
        } catch (Exception e) {
            logger.error("Exception is thrown by: Job Controller And Exception is: " + e.getMessage(), e);
            e.printStackTrace();
            throw new Exception("Failed in fetching all jobs.");
        }
        return jobDtoList;
    }

}

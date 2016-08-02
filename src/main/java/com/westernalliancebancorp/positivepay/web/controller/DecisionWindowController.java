package com.westernalliancebancorp.positivepay.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dto.DecisionWindowDto;
import com.westernalliancebancorp.positivepay.dto.DecisionWindowDtoBuilder;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.CompanyService;
import com.westernalliancebancorp.positivepay.service.DecisionWindowService;
import com.westernalliancebancorp.positivepay.service.model.GenericResponse;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

/**
 * 
 * @author Sameer Shukla
 * 
 */
@Controller
public class DecisionWindowController {

	@Loggable
	private Logger logger;

	@Autowired
	private DecisionWindowService decisionWindowService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private BankService bankService;
	
	@Autowired
	private BatchDao batchDao;

	/**
	 * Display The Landing Decision Window Page
	 * 
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/decisionwindow", method = RequestMethod.GET)
	public String showDecisionWindow(Model model, HttpServletRequest request)
			throws Exception {
		return "site.view.decisionwindow.page";
	}

	
	@RequestMapping(value = "/decisionwindow/bankCompanyDecisionWindow", method = RequestMethod.GET)
	public @ResponseBody List<DecisionWindowDto> getBankCompanyDecisionWindow(Model model,
			HttpServletRequest request) throws Exception {
		List<DecisionWindowDto> decisionWindowList = batchDao.fetchAllBankCompanyDecisionWindowMapping();
		for(DecisionWindowDto dto : decisionWindowList)
		{
		if(dto.getStart()!=null)
		{
			String startHour = DateUtils.convertTo12HoursFormat(dto.getStart());
			dto.setStart(startHour);
		}
		
		if(dto.getEnd()!=null)
		{
			String endHour = DateUtils.convertTo12HoursFormat(dto.getEnd());
			dto.setEnd(endHour);
		}
		}
		return decisionWindowList;
	}


	/**
	 * Algorithm: Allow user to create Decision Window, once Decision Window is
	 * created successfully, then assign this window to the selected Companies.
	 * 
	 * @param companyId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/decisionwindow", method = RequestMethod.POST)
	public @ResponseBody
	GenericResponse getDecisionWindow(
			@RequestBody DecisionWindowDto[] decisionWindowDto)
			throws Exception {
		try {

			DecisionWindowDtoBuilder dtoBuilder = new DecisionWindowDtoBuilder();
			for (DecisionWindowDto dto : decisionWindowDto) {
				DecisionWindow decisionWindow = dtoBuilder.dtoToModel(dto);

				DecisionWindow dw = decisionWindowService.save(decisionWindow);

				Long[] companyIds = dto.getCompany();
				if (companyIds != null && companyIds.length > 0) {
					for (Long cid : companyIds) {
						decisionWindowService.updateCompanyWithDecisionWindow(
								cid, dw);
					}
				}
			}

		} catch (Exception ex) {
			logger.error("Error occurred while Creating Decision Window", ex);
			throw new HttpStatusCodedResponseException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while Creating Decision Window",
					ex.getMessage());
		}
		return new GenericResponse(
				"Success: Decision Window created successfully!!");
	}

}

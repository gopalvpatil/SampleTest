package com.westernalliancebancorp.positivepay.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dto.AccountDto;
import com.westernalliancebancorp.positivepay.dto.UserDto;
import com.westernalliancebancorp.positivepay.exception.HttpStatusCodedResponseException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.UserService;

@Controller
public class AccountController {

	@Loggable
	private Logger logger;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/user/company/{companyId}/account", method = RequestMethod.POST)
	public @ResponseBody Long saveAccount(
			@PathVariable("companyId") Long companyId,
			@RequestBody AccountDto accountDto) throws Exception{
		try{
			if(accountDto.getSelectedUserIds() !=null && accountDto.getSelectedUserIds().isEmpty()) {
				List<UserDto> userlist = userService.getUsersByCompanyId(accountDto.getCompanyId());
				List<Long> companyUserIds =  Lambda.extract(userlist, Lambda.on(UserDto.class).getUserId());
				if(!companyUserIds.containsAll(accountDto.getSelectedUserIds())) {
					throw new HttpStatusCodedResponseException(HttpStatus.FORBIDDEN,"Cannot assign users which do not belong to this account company.");
				}
			}
			return accountService.saveAccountDetails(accountDto);
		}catch(HttpStatusCodedResponseException ex) {
			throw ex;
		}catch(Exception ex) {
			logger.error("Error occurred while saving Account detail.", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"Error occurred while saving Account detail.", ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/user/company/{companyId}/account/{accountId}", method = RequestMethod.GET)
	public @ResponseBody AccountDto getAccount(
			@PathVariable("companyId") Long companyId,
			@PathVariable(value="accountId") Long accountId) throws Exception{
		try{
			AccountDto dto = accountService.getAccountDetails(accountId);
			List<Long> userIds = Lambda.extract(dto.getUsers(), Lambda.on(UserDto.class).getUserId());
			dto.setSelectedUserIds(userIds);
			return dto;
		}catch(Exception ex) {
			logger.error("Error occurred while fetching Account detail.", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"Error occurred while fetching Account detail.", ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/user/company/{companyId}/account/{accountId}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteAccount(
			@PathVariable("companyId") Long companyId,
			@PathVariable(value="accountId") Long accountId) throws Exception{
		try{
			accountService.makeAccountInactive(accountId);
		}catch(Exception ex) {
			logger.error("Error occurred while deleting Account detail.", ex);
			throw new HttpStatusCodedResponseException(HttpStatus.INTERNAL_SERVER_ERROR,"Error occurred while deleting Account detail.", ex.getMessage());
		}
	}
}

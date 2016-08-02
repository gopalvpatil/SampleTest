package com.westernalliancebancorp.positivepay.dto;

import java.util.ArrayList;
import java.util.List;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AccountCycleCutOff;
import com.westernalliancebancorp.positivepay.model.AccountServiceOption;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.UserDetail;

/**
 * 
 * @author Sameer Shukla
 * 
 *         Kind of builder, not very good, should be autowired....
 */
public class AccountDtoBuilder {

	/**
	 * Method to convert account into AccountDTO
	 * @param accountDto
	 * @param account
	 */
	public static void updateAccountDtoFromEntity(AccountDto accountDto, Account account) {
		accountDto.setId(account.getId());
		accountDto.setCompanyId(account.getCompany().getId());
		accountDto.setAccountName(account.getName());
		accountDto.setAccountNumber(account.getNumber());
		if(account.getAccountCycleCutOff() != null)
			accountDto.setAccountCycleCutOff(account.getAccountCycleCutOff().getId());
		accountDto.setDataOutputMethod(account.getDataOutputMethod());
		accountDto.setFileInputMethod(account.getFileInputMethod());
		//accountDto.setFormat(account.g);
		accountDto.setPpDecision(account.getDefaultPpDecision());
		accountDto.setReportOutputMethod(account.getReportOutputMethod());
		if(account.getAccountServiceOption() != null)
			accountDto.setAccountServiceOption(account.getAccountServiceOption().getId());
		accountDto.setStaleDays(account.getStaleDays());
		accountDto.setActive(account.isActive());
		//accountDto.setThirdparty(account.get);
		List<UserDto> users = new ArrayList<UserDto>();
		for(UserDetail userDetail : account.getUserDetails()) {
			UserDto userDto = new UserDto();
			userDto.setUserId(userDetail.getId());
			userDto.setUserName(userDetail.getUserName());
			userDto.setFirstName(userDetail.getFirstName());
			userDto.setLastName(userDetail.getLastName());
			users.add(userDto);
		}
		accountDto.setUsers(users);
	}
	
	
	/**
	 * Method to convert accountDto into account
	 * @param accountDto
	 * @param account
	 */
	public static void updateAccountFromDto(Account account, AccountDto accountDto) {
		if(accountDto.getAccountCycleCutOff() != null) {
			AccountCycleCutOff accountCycleCutOff = new AccountCycleCutOff();
			accountCycleCutOff.setId(accountDto.getAccountCycleCutOff());
			account.setAccountCycleCutOff(accountCycleCutOff);
		}else{
			account.setAccountCycleCutOff(null);
		}
		
		if(accountDto.getAccountServiceOption() != null) {
			AccountServiceOption accountServiceOption = new AccountServiceOption();
			accountServiceOption.setId(accountDto.getAccountServiceOption());
			account.setAccountServiceOption(accountServiceOption);
		}else{
			account.setAccountServiceOption(null);
		}
		
		//account.setAccountType(accountType);
		account.setActive(accountDto.getActive());
		
		account.setDataOutputMethod(accountDto.getDataOutputMethod());
		account.setDefaultPpDecision(accountDto.getPpDecision());
		account.setFileInputMethod(accountDto.getFileInputMethod());
		account.setName(accountDto.getAccountName());
		account.setNumber(accountDto.getAccountNumber());
		//account.setOpenDate(openDate);
		//account.setPaymentType(paymentType);
		account.setReportOutputMethod(accountDto.getReportOutputMethod());
		account.setStaleDays(accountDto.getStaleDays());
	}

}

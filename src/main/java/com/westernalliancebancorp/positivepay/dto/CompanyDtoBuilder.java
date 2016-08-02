package com.westernalliancebancorp.positivepay.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Contact;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

public class CompanyDtoBuilder 
{

	/**
	 * Create Model from Request Object
	 * 
	 * @param dto
	 * @return
	 */
	public Company getCompanyFromDTO(CompanyDTO dto, BankService bankService) {
		Company company = new Company();
		company.setName(dto.getName());
		company.setTimeZone(dto.getTimeZone());
		//Hardcoded for the time being.
		
		//company.setDescription(dto.getDescription());
		List<Bank> bankList = bankService.findAll();
		Bank bank = null;
		for (Bank banks : bankList) {
			if (banks.getId() == dto.getBankId()) {
				bank = banks;
				break;
			}
		}
		company.setBank(bank);

		Set<Contact> contacts = new HashSet<Contact>();

		Contact contact = new Contact();
		contact.setAddress1(dto.getAddress1());
		contact.setAddress2(dto.getAddress2());
		contact.setCity(dto.getCity());
		contact.setEmail(dto.getEmail());
		contact.setPhone(dto.getPhone());
		contact.setName(dto.getMainContact());
		contact.setZip(dto.getZip());
		contact.setFax(dto.getFax());
		contact.setState(dto.getState());
		AuditInfo auditInfo = new AuditInfo();
		String name = SecurityUtility.getPrincipal();
		auditInfo.setCreatedBy(name);
		auditInfo.setDateCreated(new Date());
		auditInfo.setDateModified(new Date());
		contact.setAuditInfo(auditInfo);
		contact.setCompany(company);
		contacts.add(contact);

		company.setContacts(contacts);
		return company;
	}

	public static CompanyDTO getCompanyDtoFromEntity(Company company) {
		CompanyDTO companyDTO = null;
		if(company != null) {
			companyDTO = new CompanyDTO();
			companyDTO.setId(company.getId());
			companyDTO.setName(company.getName());
			
			//Populate main contact
			if(company.getContacts() != null && !company.getContacts().isEmpty()) {
				Contact mainContact = null;
				for(Contact contact : company.getContacts()) {
					if(contact.getPrimaryContact()) {
						mainContact = contact;
						break;
					}
				}
				if(mainContact == null)//If main contact not available then take any contact
					mainContact = company.getContacts().iterator().next();
				companyDTO.setMainContact(mainContact.getName());
				companyDTO.setPhone(mainContact.getPhone());
				companyDTO.setEmail(mainContact.getEmail());
				companyDTO.setFax(mainContact.getFax());
				companyDTO.setAddress1(mainContact.getAddress1());
				companyDTO.setAddress2(mainContact.getAddress2());
				companyDTO.setCity(mainContact.getCity());
				companyDTO.setState(mainContact.getState());
				companyDTO.setZip(mainContact.getZip());
			}

            Set<Account> accountSet = company.getAccounts();
            Set<UserDto> userDtoSet = new HashSet<UserDto>();
            List<UserDto> userDtoList = new ArrayList<UserDto>();
            for(Account account:accountSet) {
                Set<UserDetail> userDetails = account.getUserDetails();
                for(UserDetail userDetail:userDetails) {
                    UserDto userDto = new UserDto();
                    userDto.setUserId(userDetail.getId());
                    userDto.setFirstName(userDetail.getUserName());
                    userDto.setLastName(userDetail.getLastName());
                    userDto.setUserName(userDetail.getUserName());
                    if(userDetail.getBaseRole() != null)
                        userDto.setBaseRole(userDetail.getBaseRole().getName());
                    if(!userDtoList.contains(userDto))//Avoid duplicate 
                    	userDtoList.add(userDto);
                }
            }
            userDtoList.addAll(userDtoSet);
            companyDTO.setUsers(userDtoList);
			//Populate User list
			/*if(company.getUserDetails() != null && !company.getUserDetails().isEmpty()) {
				List<UserDto> userList = new ArrayList<UserDto>();
				for(UserDetail userDetail : company.getUserDetails()) {
					UserDto userDto = new UserDto();
					userDto.setUserId(userDetail.getId());
					userDto.setFirstName(userDetail.getUserName());
					userDto.setLastName(userDetail.getLastName());
					userDto.setUserName(userDetail.getUserName());
					if(userDetail.getBaseRole() != null)
						userDto.setBaseRole(userDetail.getBaseRole().getName());
					userList.add(userDto);
				}
				companyDTO.setUsers(userList);
			}*/
		}
		return companyDTO;
	}
	
	public static CompanyDTO getCompanyDtoSetupFromEntity(Company company) {
		CompanyDTO companyDTO = null;
		if(company != null) {
			companyDTO = new CompanyDTO();
			companyDTO.setId(company.getId());
			companyDTO.setName(company.getName());
			companyDTO.setTimeZone(company.getTimeZone());
			companyDTO.setAccountForAnalysis(company.getAccountForAnalysis());
			companyDTO.setBranchName(company.getBranchName());
			companyDTO.setFederalTaxId(company.getFederalTaxId());
			companyDTO.setActive(company.isActive());
			Bank bank = company.getBank();
			companyDTO.setBankId(bank.getId());
			
			//Populate main contact
			if(company.getContacts() != null && !company.getContacts().isEmpty()) {
				Contact mainContact = null;
				for(Contact contact : company.getContacts()) {
					if(contact.getPrimaryContact()) {
						mainContact = contact;
						break;
					}
				}
				if(mainContact == null)//If main contact not available then take any contact
					mainContact = company.getContacts().iterator().next();
				companyDTO.setMainContact(mainContact.getName());
				companyDTO.setPhone(mainContact.getPhone());
				companyDTO.setEmail(mainContact.getEmail());
				companyDTO.setFax(mainContact.getFax());
				companyDTO.setAddress1(mainContact.getAddress1());
				companyDTO.setAddress2(mainContact.getAddress2());
				companyDTO.setCity(mainContact.getCity());
				companyDTO.setState(mainContact.getState());
				companyDTO.setZip(mainContact.getZip());
			}
			
			//Populate Account list
			if(company.getAccounts() != null && !company.getAccounts().isEmpty()) {
				List<AccountDto> accountList = new ArrayList<AccountDto>();
				for(Account accountDetail : company.getAccounts()) {
					AccountDto accountDto = new AccountDto();
					accountDto.setId(accountDetail.getId());
					accountDto.setAccountName(accountDetail.getName());
					accountDto.setAccountNumber(accountDetail.getNumber());
					accountDto.setCompanyId(company.getId());
					accountDto.setActive(accountDetail.isActive());
					accountList.add(accountDto);
				}
				companyDTO.setAccounts(accountList);
			}
		}
		return companyDTO;
	}
	
	public Company getCompanyFromUpdateDTO(Company company, CompanyDTO dto, BankService bankService) {
		company.setName(dto.getName());
		company.setTimeZone(dto.getTimeZone());
		
        Bank bank = bankService.findById(dto.getBankId());
        company.setBank(bank);
        
		Set<Contact> contacts = new HashSet<Contact>();
		
		Contact contact = company.getContacts().iterator().next();
		contact.setName(dto.getMainContact());
		contact.setAddress1(dto.getAddress1());
		contact.setAddress2(dto.getAddress2());
		contact.setCity(dto.getCity());
		contact.setState(dto.getState());
		contact.setZip(dto.getZip());
		contact.setFax(dto.getFax());
		contact.setEmail(dto.getEmail());
		contact.setPhone(dto.getPhone());
		
		AuditInfo auditInfo = new AuditInfo();
		String name = SecurityUtility.getPrincipal();
		auditInfo.setCreatedBy(name);
		auditInfo.setDateCreated(new Date());
		auditInfo.setDateModified(new Date());
		contact.setAuditInfo(auditInfo);
		contact.setCompany(company);
		contacts.add(contact);
		company.setContacts(contacts);
		return company;
	}
	
	public static Company updateCompanyFromCompanyDto(Company company, CompanyDTO companyDTO) {
		if(company.getId() == null)
			company.setId(companyDTO.getId());
		company.setName(companyDTO.getName());
		company.setTimeZone(companyDTO.getTimeZone());
		company.setAccountForAnalysis(companyDTO.getAccountForAnalysis());
		company.setBranchName(companyDTO.getBranchName());
		company.setFederalTaxId(company.getFederalTaxId());
		company.setIsActive(companyDTO.getActive());
		return company;
	}
	
	public static Contact updateContactFromCompanyDto(Contact contact, CompanyDTO companyDTO) {
		contact.setAddress1(companyDTO.getAddress1());
		contact.setAddress2(companyDTO.getAddress2());
		contact.setCity(companyDTO.getCity());
		contact.setEmail(companyDTO.getEmail());
		contact.setPhone(companyDTO.getPhone());
		contact.setName(companyDTO.getMainContact());
		contact.setZip(companyDTO.getZip());
		contact.setFax(companyDTO.getFax());
		contact.setState(companyDTO.getState());
		return contact;
	}

}

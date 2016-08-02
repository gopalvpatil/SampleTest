package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.dto.CompanyDTO;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.Contact;
/**
 * Interface providing service methods to work with the Company Model
 * @author Anand Kumar
 */
public interface CompanyService {
	Company update(Company company);
	Company save(Company company);
	void delete(Company company);
	Company findById(Long id);
	List<Company> findAll();
	List<CompanyDTO> findByBankId(Long bankId) throws Exception;
	List<Company> findAllByBankIds(List<Long> bankIds) throws Exception;
	List<Company> findAllByUserName(String userName);
	Company getCompanyDetail(Long companyId);
	Company getCompanyDetails(Long companyId);
	Contact updateContactDetails(Contact contact);
	Contact saveCompanyContact(Contact contact);
	void makeCompanyAndItsAccountInactive(Long companyId);
	Long saveOrUpdateCompany(CompanyDTO companyDTO);
}
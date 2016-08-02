/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Company;

/**
 * Data access object interface to work with Company Model database operations
 * @author Anand Kumar
 *
 */
public interface CompanyDao extends GenericDao<Company, Long> {
	List<Company> findAllByBankIds(List<Long> bankIds);
	List<Company> findAllByUserName(String userName);
	Company getCompanyDetail(Long companyId);
	Company getCompanyDetails(Long companyId);

    List<Company> findAllActiveCompanies();
}

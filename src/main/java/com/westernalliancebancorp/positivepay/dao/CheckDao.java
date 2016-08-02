/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.Company;

/**
 * Data access object interface to work with Check Model database operations
 * @author Anand Kumar
 *
 */
public interface CheckDao extends GenericDao<Check, Long> {
	boolean isDuplicate(Check check);
	Check findCheckBy(String strAccountNumber, String strCheckNumber, BigDecimal dCheckAmount);
	Check findCheckBy(String checkNumber, Long accountId, BigDecimal checkAmount, Long checkStatusId);
    List<Check> findStaleChecks(List<Long> accountIds, Date staleDate);
    Check findCheckBy(String accountNumber, String checkNumber);
    Check findByReferenceDataId(Long referenceDataId);
    List<Check> findChecksByCheckStatusIds(List<Long> checkStatusIds);
    List<Check> findAllChecksInExceptionForUserCompany(Company userCompany);
    List<Check> findAllChecksForUserCompany(Company userCompany);
	long findItemsLoadedBy(Long id);
	List<Check> findAllByFileMetaDataId(Long fileMetaDataId);
	List<Check> findAllChecksBySearchParametersMap(Map<String, String> searchParametersMap);
    List<Check> finalAllChecksByDigest(List<String> digestList);
    List<Check> findAllChecksBy(String accountNumber);
	Check findCheckByCheckId(Long checkId);
}

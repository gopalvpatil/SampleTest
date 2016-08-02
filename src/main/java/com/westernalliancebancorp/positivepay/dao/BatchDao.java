/**
 *
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.westernalliancebancorp.positivepay.dto.*;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.UserDetailFilterSearchValue;

/**
 * Class to have single method to insert all checks
 *
 * @author Anand Kumar
 */
public interface BatchDao {
	List<Check> insertAllChecks(List<Check> checks);
    List<ExceptionalCheck> insertAllExceptionalChecks(List<ExceptionalCheck> exceptionalChecks);
    List<ExceptionalReferenceData> insertAllExceptionalReferenceData(List<ExceptionalReferenceData> exceptionalReferenceDataList);
    List<Long> findAllStaleChecks(List<Long> accountIds, String staleDate, List<Long> checkStatusId);
    List<Check> findAllDuplicateChecks(List<Check> checks);
    List<ReferenceData> findAllDuplicateReferenceDatas(List<ReferenceData> referenceDataList);
    List<Check> insertAllChecksNoFileMetadata(List<Check> checks);
    List<ReferenceData> insertAllReferenceData(List<ReferenceData> referenceDataList);
    List<CheckDto> findAllStopButPaidChecks(List<Long> accountIds, List<Long> checkStatusIds);
    List<CheckDto> findAllIssuedMatchedChecks(List<Long> accountIds, List<Long> checkStatusIds);
    List<CheckDto> findAllIssuedUnMatchedChecks(List<Long> accountIds, List<Long> checkStatusIds);
    List<CheckDto> findAllPaidChecksNoAmountClauseBy(List<Long> accountIds, List<Long> checkStatusIds);
    List<CheckDto> findAllStopUnProcessedChecks(List<Long> accountIds, List<Long> checkStatusIds);
    List<CheckDto> findAllVoidButStopChecks(List<Long> accountIds, List<Long> checkStatusIds);
    List<Long> findAllStopNotIssuedChecks(List<Long> accountIds);
    List<Long> findAllPaidNotIssuedChecks(List<Long> accountIds);
    List<CheckDto> findAllChecksByAnyStatusNoAmount(List<Long> accountIds,
                                                           List<Long> checkStatusIds, ReferenceData.ITEM_TYPE item_type);
    List<ExceptionCheckDto> findAllPaidOrStopButVoidDuplicateChecks(List<Long> checkStatusIds);
    List<ExceptionCheckDto> findAllVoidOrStopButIssuedDuplicateChecks(List<Long> checkStatusIds);
    List<UserDto> findUsersBy(Long companyId);
    Map<Long, Integer> getExceptionalItemsCountOfFile(List<Long> fileMetadataIds);
    Map<Long, Integer> getProcessedItemsCountOfFile(List<Long> fileMetadataIds);
    void insertRolePermissions(Long roleId, List<Long> selectedPermissionIdsList);
	void deleteRolePermissions(Long roleId, List<Long> assignedPermissionIdsList);
	void insertUserPermissions(Long userDetailId, Set<Long> permissionIdsList);
	void deleteUserPermissions(Long userDetailId, Set<Long> permissionIdsList);
	void updateUserBaseRole(Long userDetailId, Long roleId);
	List<ManageUserDto> findUserBySearchCriteria(FindUserDto criteria);
	void updateUserStatusAndInstitutionId(List<ManageUserDto> manageUsers);
	void assignUsersToCompany(Map<Long, Set<Long>> companyUsersMap);
    List<AccountInfoDto> getCustomerAccountInfo(List<Long> accountIdList);
    List<Long> getAllAccountsIds();
    List<AccountPaymentInfoDto> getAllPaymentsAndCountGroupedByCheckStatus(List<String> accountNumbers, List<String> checkStatus);
    List<CheckStatusDto> getDisplayableCheckStatuses();
    List<UserDetailFilterSearchValue> insertUserDetailFilterSearchValueList(List<UserDetailFilterSearchValue> UserDetailFilterSearchValueList);
    List<PaymentDetailDto> findAllPayments(Map<String, SearchParameterDto> searchParametersMap);
    List<PaymentDetailDto> findAllPaymentsForCompany(Company userCompany);
    List<UserDetailFilterSearchValueDto> getUserDetailFilterSearchValuesByUserDetailDefinedFilterId(Long userDetailDefinedFilterId);
    List<PaymentByDateDto> findAllPaymentsMadeBetweenDate(List<String> accountNumbers, Date fromDate, Date toDate);
    List<Long> findAllChecksInExceptionalState(List<Long> accountIds);
    List<DecisionWindowDto> fetchAllBankCompanyDecisionWindowMapping();
    List<PaymentDetailDto> findAllPaymentsByDataCriteriaAndUserCompany(DataCriteriaDto dataCriteriaDto, Company userCompany);
    ExceptionalReferenceDataDto getExceptionalReferenceDataInfo(Long exceptionalReferenceDataId);
    void deleteUsersFromAccount(Long accountId, List<Long> userIds);
    void addUsersToAccount(Long accountId, List<Long> userIds);
    List<Long> systemMessageInUserDetailHistory(Long user_detail_id, Long messageId);
    List<Check> findAllDuplicateChecksForManualEntry(List<Check> checks);
    List<ExceptionalReferenceDataDto> getExceptionalReferenceData(String checkNumber,	String accountNumber, String itemType);
    CheckDto findCheckById(Long checkId);
    List<CompanyDTO> findSelectedCompanies(List<Long> bankIds, Boolean fetchAll);
    List<AccountDto> findSelectedAccounts(List<Long> compIds, List<Long> bankIds);
	List<PaymentDetailDto> findAllItems(Map<String, SearchParameterDto> searchParametersMap);
    List<JobDto> getAllJobHistory();
	Long findJobStepNumOfItemsProcessedInFile(Date jobStepActualStartTime, Date jobStepActualEndTime,	String fileType);
	Long findJobStepNumOfErrorsInFile(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType);
	List<ItemErrorRecordsDto> findErrorsInFile(java.util.Date jobStartDateTime, java.util.Date jobEndDateTime);
	List<RecentFileDto> filterFilesBy(Company userCompany, Date uploadDate, List<Long> fileMetaDataIds, String noOfDaysBefore);
	String findLastCronExpressionByJobId(Long id);
	JobDto findLastJobConfigurationBy(Long jobId);
	PaymentDetailDto findItemDetails(String checkNumber, String accountNumber, String itemType);
	List<String> findJobStepFileNames(Date jobStepActualStartTime, Date jobStepActualEndTime, String fileType);
	CheckDto findCheckByTraceNumber(String traceNumber);
	PaymentDetailDto findItemDetailsByTraceNumber(String traceNumber);
	List<Long> findFileMetaDataIdsByAccountId(Long accountId);
}

package com.westernalliancebancorp.positivepay.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.dto.AccountInfoForCustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * Interface providing service methods to work with the Check Model
 * @author Anand Kumar
 */
public interface CheckService {
	Check update(Check check);	
	Check save(Check check);	
	void delete(Check check);	
	Check findById(Long id);
	List<Check> findAll();	
	List<Check> saveAll(List<Check> checks);
	boolean isDuplicate(Check check);
	Check findCheckBy(String strAccountNumber, String strCheckNumber, BigDecimal dCheckAmount) throws Exception;	
	Check findCheckBy(String strAccountNumber, String strCheckNumber);	
	List<CheckDto> findAllChecksInExceptionForUserCompany();	
	Check changePayee(Long checkId, String payee, String comment);	
	Check changeDate(Long checkId, String date, String comment) throws ParseException;	
    CheckDto getCheckDetails(Long checkId);    
    Map<Long, Integer> getProcessedItemsCountOfFile(List<FileMetaData> fileMetaDataList);
	long findItemsLoadedBy(Long id);
    List<CheckStatus> getAllCheckStatus();
    Map<Long, Integer> getUnProcessedItemsCountOfFile(List<FileMetaData> fileMetaDataList);
    List<CheckStatusDto> getDisplayableCheckStatus();
    AccountInfoForCustomerDashboardDto getCustomerAccountInfo();
    Long saveAccountInfo(CheckDto checkDto);
    Check unmatchAndMatch(String userComment, Long checkId, Long referenceIdToMatch);

    List<CheckHistory> getCheckHistory(Long checkId);

    void changeDuplicateReferenceDataCheckNumber(Long exceptionalReferenceDataId, String changedCheckNumber, String userComment) throws ParseException, CallbackException, WorkFlowServiceException;

    void changeDuplicateReferenceDataAccountNumber(Long exceptionalReferenceDataId, String changeAccountNumber, String userComment) throws ParseException, CallbackException, WorkFlowServiceException;
	CheckStatus getLatestCheckStatus(long checkId);
    List<Check> manualEntrySaveAll(List<Check> checks);
	CheckHistory addComment(Long checkId, String comment);
	CheckHistory addHistoryEntryForNewCheck(Long checkId, String source);

    CheckHistory addHistoryEntryForNewCheck(Check returnCheck, String source, ItemType itemType);
    void correctZeroCheckNumber(Long exceptionalReferenceDataId,
	    String changedCheckNumber) throws ParseException, CallbackException, WorkFlowServiceException;
    void correctZeroCheckNumberByReferenceDataId(Long referenceDataId,
	    String changedCheckNumber) throws CallbackException, WorkFlowServiceException;
    CheckHistory addHistoryEntryForDuplicateChecks(Long checkId,
	    String comment, Action action);
    CheckHistory addHistoryEntryForDuplicateChecks(Check returnCheck,
	    String comment, Action action);
    CheckHistory addHistoryEntryForNewCheck(Check manualEntryCheck, String string,
	    ItemType itemType, Date manualEntryDate, BigDecimal amount);
    CheckDto findCheckByTraceNumber(String traceNumber);
    void payDuplicateReferenceData(Long exceptionalReferenceDataId,
	    String userComment) throws ParseException, WorkFlowServiceException, CallbackException;
}

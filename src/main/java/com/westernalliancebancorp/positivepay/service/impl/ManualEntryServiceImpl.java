package com.westernalliancebancorp.positivepay.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.utility.common.PPUtils;

/**
 * User: gduggirala
 * Date: 30/5/14
 * Time: 12:58 PM
 */
@Service
public class ManualEntryServiceImpl implements ManualEntryService {
    @Autowired
    AccountService accountService;
    @Autowired
    ItemTypeService itemTypeService;
    @Autowired
    BatchDao batchDao;
    @Autowired
    ExceptionalCheckService exceptionalCheckService;
    @Autowired
    ExceptionStatusDao exceptionStatusDao;
    @Autowired
    FileDao fileDao;
    @Autowired
    FileTypeDao fileTypeDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    ReferenceDataService referenceDataService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckService checkService;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    PermissionDao permissionDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    UserDetailDao userDetailDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    StartStatusService startStatusService;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;

    public static final String ACTUAL_DUPLICATES = "ACTUAL_DUPLICATES";
    public static final String DUPLICATES_WITH_DIFFERENT_ITEM_CODES = "DUPLICATES_WITH_DIFFERENT_ITEM_CODES";
    public static final String UNKNOWN_SEQUENCE_CHECKS = "UNKNOWN_SEQUENCE_CHECKS";
    @Override
    @RollbackForEmulatedUser
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String, List<CheckDto>> saveManualEntries(List<CheckDto> checks) throws CallbackException, WorkFlowServiceException {
        Map<String, List<CheckDto>> manualEntryResponseMap = new HashMap<String, List<CheckDto>>();
        List<Check> checksToSave = new ArrayList<Check>();
        FileMetaData manualEntryMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);
        for (CheckDto fromCheck : checks) {
            Check toCheck = new Check();
            BeanUtils.copyProperties(fromCheck, toCheck);
            //Get Account from the account number String
            Account account = accountService.findByAccountNumberAndCompanyId(fromCheck.getAccountNumber(), fromCheck.getCompanyId());
            toCheck.setAccount(account);
            //Set ItemType from issue Code
            toCheck.setItemType(itemTypeService.findByCode(fromCheck.getIssueCode()));
            //Set the digest
            toCheck.setDigest(account.getNumber() + "" + PPUtils.stripLeadingZeros(fromCheck.getCheckNumber()));
            //set the routing number
            toCheck.setRoutingNumber(account.getBank().getRoutingNumber());
            //set file meta data for manual entry
            toCheck.setFileMetaData(manualEntryMetaData);
            //WALPP-251 : set void date/amount if the item type is void
            if (toCheck.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
            	toCheck.setIssueDate(null);
            	toCheck.setVoidDate(fromCheck.getIssueDate());
            	toCheck.setIssuedAmount(null);
            	toCheck.setVoidAmount(fromCheck.getIssuedAmount());	
            }
            checksToSave.add(toCheck);
        }

        List<Check> duplicates = null;
       
        if(checksToSave.size() >0) {
            duplicates = batchDao.findAllDuplicateChecksForManualEntry(checksToSave);
        }
        Map<String, List<Check>> filteredMap = matchItemCodes(duplicates, checksToSave);
        List<CheckDto> duplicateChecks = new ArrayList<CheckDto>();
        if (filteredMap.get(ACTUAL_DUPLICATES) != null && filteredMap.get(ACTUAL_DUPLICATES).size() > 0) {
            for (Check fromCheck : filteredMap.get(ACTUAL_DUPLICATES)) {
                CheckDto toCheck = new CheckDto();
                BeanUtils.copyProperties(fromCheck, toCheck);
                //Set Account Number Manually
                toCheck.setAccountNumber(fromCheck.getAccount().getNumber());
                duplicateChecks.add(toCheck);
            }
            manualEntryResponseMap.put("SAVED_CHECKS", null);
            manualEntryResponseMap.put("DUPLICATE_CHECKS", duplicateChecks);
            return manualEntryResponseMap;
        }
        List<ReferenceData> checksWithStopAndPaidItemTypes = new ArrayList<ReferenceData>();
        List<Check> checkList = new ArrayList<Check>();
        //Have the manual entries which are not "Stop" and "Paid"
        //Once this method is executed "checksWithPaidItemTypes" will have the reference data's which are filtered out from checksToSave list

        filterOutStopAndPaidChecks(checksToSave, checksWithStopAndPaidItemTypes, false);
        checkList = filteredMap.get(DUPLICATES_WITH_DIFFERENT_ITEM_CODES);
        if(checkList !=null && !checkList.isEmpty()){
        	filterOutStopAndPaidChecks(checkList, checksWithStopAndPaidItemTypes, true);
        }
        List<ReferenceData> duplicateRefDatas = null;
        
        if(checksWithStopAndPaidItemTypes.size() >0) {
        	duplicateRefDatas = batchDao.findAllDuplicateReferenceDatas(checksWithStopAndPaidItemTypes);
            if(duplicateRefDatas != null && duplicateRefDatas.size()>0){
                manualEntryResponseMap.put("DUPLICATE_CHECKS", getCheckDtoFromReferencedata(duplicateRefDatas));
            }
        }

        if (checksWithStopAndPaidItemTypes.size() > 0) {
            PositivePayThreadLocal.setInputMode(PositivePayThreadLocal.INPUT_MODE.ManualEntry.toString());
            referenceDataService.saveAll(checksWithStopAndPaidItemTypes);
            processStopAndPaidChecks(checksWithStopAndPaidItemTypes); //Move it to stopNotIssued or PaidNotIssued.
            setSavedCheckDtoList(getCheckDtoFromReferencedata(checksWithStopAndPaidItemTypes), manualEntryResponseMap);
        }
        if (filteredMap.get(DUPLICATES_WITH_DIFFERENT_ITEM_CODES) != null && filteredMap.get(DUPLICATES_WITH_DIFFERENT_ITEM_CODES).size() > 0) {
            processSequenceExceptionChecks(filteredMap);
            //We should do something with the unknown sequence checks.
        }

        if (checksToSave.size() > 0) {
            PositivePayThreadLocal.setInputMode(PositivePayThreadLocal.INPUT_MODE.ManualEntry.toString());
            checkService.manualEntrySaveAll(checksToSave);
            startStatusService.processStartChecks(checkDao.finalAllChecksByDigest(Lambda.extract(checksToSave, Lambda.on(Check.class).getDigest())));
            PositivePayThreadLocal.removeInputMode();
            setSavedCheckDtoList(getCheckDtoFromCheckDetail(checksToSave), manualEntryResponseMap);
        }
        //We have got for check_detail, but here we will not have Paid or Stop checks.
        //manualEntryResponseMap.put("SAVED_CHECKS", savedCheckDtoList);
        return manualEntryResponseMap;
    }

    private void processStopAndPaidChecks(List<ReferenceData> referenceDataList) {
        PositivePayThreadLocal.setInputMode(PositivePayThreadLocal.INPUT_MODE.ManualEntry.toString());
        referenceDataProcessorService.processNonDuplicateReferenceData(referenceDataList);
        PositivePayThreadLocal.removeInputMode();
    }

    private List<CheckDto> getCheckDtoFromCheckDetail(List<Check> checkList) {
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>(checkList.size());
        for (Check check : checkList) {
            CheckDto checkDto = new CheckDto();
            BeanUtils.copyProperties(check, checkDto);
            checkDtoList.add(checkDto);
        }
        return checkDtoList;
    }

    private List<CheckDto> getCheckDtoFromReferencedata(List<ReferenceData> referenceDataList){
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>(referenceDataList.size());
        for(ReferenceData referenceData:referenceDataList){
            CheckDto checkDto = new CheckDto();
            checkDto.setIssueCode(itemTypeService.getItemCodeFromReferenceDataItemType(referenceData.getItemType()));
            checkDto.setCheckNumber(referenceData.getCheckNumber());
            checkDto.setAccountNumber(referenceData.getAccount().getNumber());
            checkDto.setIssuedAmount(referenceData.getAmount());
            checkDto.setDigest(referenceData.getDigest());
            if(referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)){
                checkDto.setIssueDate(referenceData.getPaidDate());
            }else{
                checkDto.setIssueDate(referenceData.getStopDate());
            }
            checkDtoList.add(checkDto);
        }
        return checkDtoList;
    }

    private void setSavedCheckDtoList(List<CheckDto> savedCheckList, Map<String, List<CheckDto>> manualEntryResponseMap){
        List<CheckDto> checkDtoList = manualEntryResponseMap.get("SAVED_CHECKS");
        if(checkDtoList == null){
            checkDtoList = new ArrayList<CheckDto>();
        }
        checkDtoList.addAll(savedCheckList);
    }

    private void processSequenceExceptionChecks(Map<String, List<Check>> filteredMap) throws CallbackException, WorkFlowServiceException {
        List<Check> checkList = filteredMap.get(DUPLICATES_WITH_DIFFERENT_ITEM_CODES);
        //We reached here means we know that the list of received checks contain same check number and
        //account number but a different Item code.
        //Different Sequence exceptions that we can think of are
        //VoidAfterStop, VoidAfterPaidService, IssuedAfterStopService, IssuedAfterVoidService, StaleVoidService, StopAfterPaid and StopAfterVoid
        //Let's consider each case.

        //First get all the checks in DB by Digest
        List<String> digest = Lambda.extract(checkList, Lambda.on(Check.class).getDigest());
        List <Check> checksInDb = checkDao.finalAllChecksByDigest(digest);
        List<Check> unknownSequenceChecks = new ArrayList<Check>();
        List<Check> nonDuplicateChecks = new ArrayList<Check>();
        for(Check manualEntryCheck:checkList) {
            for(Check checkInDb:checksInDb) {
                Map<String, Object> userData = new HashMap<String, Object>();
                if(manualEntryCheck.getDigest().equals(checkInDb.getDigest())) {
                    if(isVoidAfterStop(checkInDb, manualEntryCheck)) {
                        //Take void after Stop action.
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "voidAfterStop",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isVoidAfterPaid(checkInDb, manualEntryCheck)) {
                        //Take void after paid action.
                	userData.put(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name(), manualEntryCheck);
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "voidAfterPaid",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isIssuedAfterStop(checkInDb, manualEntryCheck)) {
                        //Take issuedAfterStop.
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "issuedAfterStop",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isIssuedAfterVoid(checkInDb, manualEntryCheck)) {
                        //Take issuedAfterVoid
                	userData.put(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name(), manualEntryCheck);
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "issuedAfterVoid", userData);
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isStaleVoid(checkInDb, manualEntryCheck)) {
                        //Take StaleAfterVoid
                	userData.put(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_ISSUED_CHECK.name(), manualEntryCheck);
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "staleVoid",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isStopAfterPaid(checkInDb, manualEntryCheck)) {
                        //Take StopAfterPaidService
                    	ReferenceData refDataForUserData = referenceDataDao.findByCheckNumberAccountIdAndItemType(checkInDb.getCheckNumber(), checkInDb.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP).get(0);
                    	userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), refDataForUserData);
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "stopAfterPaid",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else if(isStopAfterVoid(checkInDb, manualEntryCheck)) {
                        //Take StopAfterVoid
                    	ReferenceData refDataForUserData = referenceDataDao.findByCheckNumberAccountIdAndItemType(checkInDb.getCheckNumber(), checkInDb.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP).get(0);
                    	userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), refDataForUserData);
                        createHistoryRecord(checkInDb, manualEntryCheck);
                        workflowService.performAction(checkInDb, "stopAfterVoid",userData );
                        nonDuplicateChecks.add(manualEntryCheck);
                    }else{
                        unknownSequenceChecks.add(manualEntryCheck);
                    }
                }
            }
        }
        filteredMap.put(UNKNOWN_SEQUENCE_CHECKS, unknownSequenceChecks);
    }

    private void createHistoryRecord(Check checkInDb, Check manualEntryCheck) {
	 BigDecimal amount = manualEntryCheck.getIssuedAmount();
	 Date manualEntryDate = manualEntryCheck.getIssueDate();
	if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
	    amount = manualEntryCheck.getVoidAmount();
	    manualEntryDate = manualEntryCheck.getVoidDate();
	}
        checkService.addHistoryEntryForNewCheck(checkInDb, PositivePayThreadLocal.INPUT_MODE.ManualEntry.toString(), manualEntryCheck.getItemType(),manualEntryDate,amount);
    }

    private boolean isStaleVoid(Check checkInDb, Check manualEntryCheck) {
        if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if(checkStatus.getName().equals(CheckStatus.STALE_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "staleVoid", workflowManager);
            }
        }
        return Boolean.FALSE;
    }


    private boolean isIssuedAfterVoid(Check checkInDb, Check manualEntryCheck) {
        if (manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.I.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if (checkStatus.getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "issuedAfterVoid", workflowManager);
            }
        }
        return Boolean.FALSE;
    }

    private boolean isIssuedAfterStop(Check checkInDb, Check manualEntryCheck) {
        if (manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.I.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if (checkStatus.getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "issuedAfterStop", workflowManager);
            }
        }
        return Boolean.FALSE;
    }


    private boolean isVoidAfterPaid(Check checkInDb, Check manualEntryCheck) {
        if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if(checkStatus.getName().equals(CheckStatus.PAID_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "voidAfterPaid", workflowManager);
            }
        }
        return Boolean.FALSE;
    }

    private boolean isVoidAfterStop(Check checkInDb, Check manualEntryCheck) {
        if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if(checkStatus.getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "voidAfterStop", workflowManager);
            }
        }
        return Boolean.FALSE;
    }

    private boolean isStopAfterPaid(Check checkInDb, Check manualEntryCheck) {
        if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.S.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if(checkStatus.getName().equals(CheckStatus.PAID_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "stopAfterPaid", workflowManager);
            }
        }
        return Boolean.FALSE;
    }

    private boolean isStopAfterVoid(Check checkInDb, Check manualEntryCheck) {
        if(manualEntryCheck.getItemType().getItemCode().equals(ItemType.CODE.S.name())) {
            CheckStatus checkStatus = checkStatusDao.findById(checkInDb.getCheckStatus().getId());
            if(checkStatus.getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(checkInDb.getWorkflow().getId());
                return workflowService.canPerformAction(checkInDb, "stopAfterVoid", workflowManager);
            }
        }
        return Boolean.FALSE;
    }


    private Map<String, List<Check>> matchItemCodes(List<Check> duplicates, List<Check> checksToSave) {
        List<Check> actualDuplicates = new ArrayList<Check>();
        Map<String, List<Check>> returnMap = new HashMap<String, List<Check>>();
        //In the duplicates list that we recived we have the item codes of the checksToSave and not the items codes of the checks in DB
        //So getting the list of checks with item codes.
        List<String> digests = null;
        if (duplicates != null && duplicates.size() > 0) {
            digests = Lambda.extract(duplicates, Lambda.on(Check.class).getDigest());
        }
        if (digests == null || digests.size() <= 0) {
            return returnMap;
        }
        List<Check> duplicatesWithItemCodes = checkDao.finalAllChecksByDigest(digests);

        List<Check> duplicatesWithDifferentItemCodes = new ArrayList<Check>();

        returnMap.put(ACTUAL_DUPLICATES, actualDuplicates);
        returnMap.put(DUPLICATES_WITH_DIFFERENT_ITEM_CODES, duplicatesWithDifferentItemCodes);
        for (Check duplicateCheckFromDatabase : duplicatesWithItemCodes) {
            for (Check checkToSave : checksToSave) {
                if (duplicateCheckFromDatabase.getCheckNumber().equals(checkToSave.getCheckNumber())
                        && duplicateCheckFromDatabase.getAccount().getId().equals(checkToSave.getAccount().getId())) {
                    if (duplicateCheckFromDatabase.getItemType().getItemCode().equals(checkToSave.getItemType().getItemCode())) {
                        actualDuplicates.add(duplicateCheckFromDatabase);
                    } else {
                        duplicatesWithDifferentItemCodes.add(checkToSave);
                    }
                }
            }
        }
        checksToSave.removeAll(duplicatesWithDifferentItemCodes);
        return returnMap;
    }

    private List<ExceptionalCheck> getExceptionalChecks(List<Check> checkList) {
        ExceptionType exceptionType = exceptionTypeDao.findByName(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE);
        List<ExceptionalCheck> exceptionalCheckList = new ArrayList<ExceptionalCheck>(checkList.size());
        for (Check check : checkList) {
            ExceptionalCheck exceptionalCheck = new ExceptionalCheck();
            exceptionalCheck.setFileMetaData(check.getFileMetaData());
            exceptionalCheck.setAccountNumber(check.getAccount().getNumber());
            exceptionalCheck.setCheckNumber(check.getCheckNumber());
            if (check.getItemType().getItemCode().equals(ItemType.CODE.I.name())) {
                exceptionalCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.ISSUED);
            } else if (check.getItemType().getItemCode().equals(ItemType.CODE.V.name())) {
                exceptionalCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.VOID);
            }
            exceptionalCheck.setExceptionStatus(ModelUtils.createOrRetrieveExceptionStatus(ExceptionStatus.STATUS.OPEN, exceptionStatusDao));
            exceptionalCheck.setExceptionType(exceptionType);
            exceptionalCheck.setIssueCode(check.getItemType().getItemCode());
            try {
                exceptionalCheck.setIssueDate(DateUtils.getWALFormatDateString(check.getIssueDate()));
            } catch (ParseException pe) {
                throw new RuntimeException(pe);
            }
            exceptionalCheck.setPayee(check.getPayee());
            exceptionalCheck.setIssuedAmount(check.getIssuedAmount() + "");
            exceptionalCheck.setLineNumber(check.getLineNumber());
            exceptionalCheck.setRoutingNumber(check.getRoutingNumber());
            exceptionalCheckList.add(exceptionalCheck);
        }
        return exceptionalCheckList;
    }

    private void filterOutStopAndPaidChecks(List<Check> checksToSave, List<ReferenceData> checksWithStopAndPaidItemTypes, boolean duplicatesWithDiffItemCodes) {
        List<Check> filteredChecksToSave = new ArrayList<Check>();

        for (Check check : checksToSave) {
            if (check.getItemType().getItemCode().equals(ItemType.CODE.P.name())) {
                ReferenceData paidReferenceData = new ReferenceData();
                paidReferenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                paidReferenceData.setAmount(check.getIssuedAmount());
                paidReferenceData.setCheckNumber(check.getCheckNumber());
                paidReferenceData.setPaidDate(check.getIssueDate());
                paidReferenceData.setTraceNumber("N/A");
                paidReferenceData.setLineNumber(check.getLineNumber());
                paidReferenceData.setDigest(check.getAccount().getNumber() + "" + check.getCheckNumber());
                paidReferenceData.setAccount(check.getAccount());
                paidReferenceData.setAssignedBankNumber(check.getAccount().getBank().getAssignedBankNumber());
                paidReferenceData.setFileMetaData(check.getFileMetaData());
                paidReferenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
                checksWithStopAndPaidItemTypes.add(paidReferenceData);
                filteredChecksToSave.add(check);
            } else if (check.getItemType().getItemCode().equals(ItemType.CODE.S.name())) {
                ReferenceData stopReferenceData = new ReferenceData();
                stopReferenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                stopReferenceData.setCheckNumber(check.getCheckNumber());
                stopReferenceData.setAmount(check.getIssuedAmount());
                stopReferenceData.setStopDate(check.getIssueDate());
                stopReferenceData.setTraceNumber("N/A");
                stopReferenceData.setDigest(check.getAccount().getNumber() + "" + check.getCheckNumber());
                stopReferenceData.setLineNumber(check.getLineNumber());
                stopReferenceData.setAccount(check.getAccount());
                stopReferenceData.setAssignedBankNumber(check.getAccount().getBank().getAssignedBankNumber());
                stopReferenceData.setFileMetaData(check.getFileMetaData());
                stopReferenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
                checksWithStopAndPaidItemTypes.add(stopReferenceData);
                // We will not create stop check entry in order to incorporate stopNotIssued Exception
                filteredChecksToSave.add(check);
            }
        }
        if(!duplicatesWithDiffItemCodes){ //We want to retain the entries in the list, if Duplicates with different item codes are passed
        //Remove the checks which are converted into referenceData, i.e. the checks which have item codes "S" or "P"
        checksToSave.removeAll(filteredChecksToSave);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ItemType> retrievePermittedItemTypes(String userName) {
        List<Permission> permissionList = permissionDao.findResourcesByUserAndType(userName, Permission.TYPE.MANUAL_ENTRY);
        if (permissionList == null || permissionList.isEmpty()) {
            List<ItemType> filteredItemTypes = new ArrayList<ItemType>();
            if (SecurityUtility.isUserBankAdmin(userName, userDetailDao)) {
                List<ItemType> itemTypes = itemTypeDao.findAllActiveItemTypes();
                for (ItemType itemType : itemTypes) {
                    if (itemType.getItemCode().equals(ItemType.CODE.I) || itemType.getItemCode().equals(ItemType.CODE.P) ||
                            itemType.getItemCode().equals(ItemType.CODE.V) || itemType.getItemCode().equals(ItemType.CODE.S)) {
                        filteredItemTypes.add(itemType);
                    }
                }
                return filteredItemTypes;
            } else {
                return filteredItemTypes;
            }
        }
        return ModelUtils.getItemTypesFromPermission(itemTypeDao, permissionList);
    }
}

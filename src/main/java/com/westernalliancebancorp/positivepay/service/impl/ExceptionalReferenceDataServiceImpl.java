package com.westernalliancebancorp.positivepay.service.impl;

import java.text.ParseException;
import java.util.*;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

import javax.security.auth.login.AccountNotFoundException;

@Service
public class ExceptionalReferenceDataServiceImpl implements ExceptionalReferenceDataService {

	/** The logger object */
	@Loggable
	private Logger logger;
	@Autowired
	private ExceptionalReferenceDataDao exceptionalReferenceDataDao;
	@Autowired
	private BatchDao batchDao;    
    @Autowired
    private CheckDao checkDao;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private CheckStatusDao checkStatusDao;
    @Autowired
    private CheckHistoryDao checkHistoryDao;
    @Autowired
    private ActionDao actionDao;
    @Autowired
    CheckService checkService;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;
    @Autowired
    ExceptionTypeService exceptionTypeService;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    ItemTypeDao itemTypeDao;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalReferenceData update(ExceptionalReferenceData check) {
		return exceptionalReferenceDataDao.update(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalReferenceData save(ExceptionalReferenceData check) {
		return exceptionalReferenceDataDao.save(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ExceptionalReferenceData check) {
		exceptionalReferenceDataDao.delete(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalReferenceData findById(Long id) {
		return exceptionalReferenceDataDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalReferenceData> findAll() {
		return exceptionalReferenceDataDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalReferenceData> saveAll(List<ExceptionalReferenceData> exceptionalReferenceDataList) {
		//Using JDBC Template batch Updates
		List<ExceptionalReferenceData> exceptionalReferenceDataListToBeSaved = new ArrayList<ExceptionalReferenceData>();
        for (ExceptionalReferenceData exceptionalReferenceData : exceptionalReferenceDataList) {
            AuditInfo auditInfo = new AuditInfo();
            String name = SecurityUtility.getPrincipal();
            auditInfo.setCreatedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy(name);
            exceptionalReferenceData.setAuditInfo(auditInfo);
            exceptionalReferenceDataListToBeSaved.add(exceptionalReferenceData);
        }
        return batchDao.insertAllExceptionalReferenceData(exceptionalReferenceDataListToBeSaved);
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean deleteExceptionalReferenceDataRecord(Long exceptionReferenceId, String userComment) {
        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionReferenceId);
        /* Create comment for history creation */
        String historyComment = null;
       	if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.name())) 
       	 {
       	    historyComment = "DuplicatePaidException resolved : Delete Duplicate";
       	 }
       	 else if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.name())) 
       	 {
       	     historyComment = "DuplicateStopException resolved : Delete Duplicate";
       	 }
        Check check = checkDao.findCheckBy(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getCheckNumber());
        if(check!=null) {
            updateCheckHistory(check, exceptionalReferenceData, userComment);
            check.setExceptionType(null);
            check.setExceptionResolvedDate(new Date());
            checkDao.update(check);
            /* Put an entry in the check history stating exception has been resolved */
            Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_RESOLVED, check.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
            checkService.addHistoryEntryForDuplicateChecks(check, historyComment, action);
        }
        exceptionalReferenceDataDao.delete(exceptionalReferenceData);
        return Boolean.TRUE;
    }

    private void updateCheckHistory(Check check, ExceptionalReferenceData exceptionalReferenceData, String userComment) {
        CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DELETE, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setMatchStatus(check.getMatchStatus() == null ? Constants.UNMATCHED : check.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        String traceNumber = exceptionalReferenceData.getTraceNumber();
        //Improved the check history logging information. Included PaidDate,stop date and PaidAmount/StopAmount of the Duplicate check
        logger.info("Duplicate check details : PaidDate -"+exceptionalReferenceData.getPaidDate()+", StopDate -"+exceptionalReferenceData.getStopDate()+", Amount -" +exceptionalReferenceData.getAmount());
        if (traceNumber != null) {
            checkHistory.setSystemComment(
                    String.format("A duplicate payment with the same check number and the account number is deleted the trace number is  '%s'", traceNumber));
        } else {
            checkHistory.setSystemComment(
                    String.format("A duplicate payment with the same check number and the account number is deleted "));
        }
        if (userComment != null && !userComment.isEmpty()) {
            checkHistory.setUserComment(userComment);
        }
        checkHistoryDao.save(checkHistory);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public ExceptionalReferenceData findBy(String traceNumber, String amount, String accountNumber) {
        return exceptionalReferenceDataDao.findBy(traceNumber,amount, accountNumber);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void changeZeroedCheckNumber(Long id, String changedCheckNumber, String userComment) throws ParseException, AccountNotFoundException, CallbackException, WorkFlowServiceException {
        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(id);
        exceptionalReferenceData.setCheckNumber(changedCheckNumber);
        ReferenceData referenceDataFromExceptionalReferenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData);
        //Find if there is any referenceData already existing with the same check number and account number..
        List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceDataFromExceptionalReferenceData.getCheckNumber(), referenceDataFromExceptionalReferenceData.getAccount().getId(), referenceDataFromExceptionalReferenceData.getItemType());
        if (referenceDataList != null && !referenceDataList.isEmpty()) {
            ReferenceData referenceDataFromDb = referenceDataList.get(0);
            //There is already a referenceData which is existing with the same check number and account number, so lets swap them first
            swapReferenceAndExceptionalReferenceData(referenceDataFromDb, referenceDataFromExceptionalReferenceData);
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceDataFromDb);
        } else {
            //No ReferenceData with the same check number and the account number so save it and process it.
            referenceDataDao.save(referenceDataFromExceptionalReferenceData);
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceDataFromExceptionalReferenceData);
        }
        exceptionalReferenceDataDao.delete(exceptionalReferenceDataDao.findById(id));
        
    }

    @Override
    public void processDuplicatesInExceptionsReferneceDataWith(String oldCheckNumber, Long oldAccountId) throws ParseException, AccountNotFoundException, CallbackException, WorkFlowServiceException {
        Account oldAccount = accountDao.findById(oldAccountId);
        String oldAccountNumber = oldAccount.getNumber();
        List<ExceptionalReferenceData> exceptionalReferenceDataList = exceptionalReferenceDataDao.findByCheckNumberAndAccountNumber(oldCheckNumber, oldAccountNumber);
        if(exceptionalReferenceDataList == null || exceptionalReferenceDataList.isEmpty()){
            logger.info("There is nothing to take care of we are good..");
        }else if(exceptionalReferenceDataList.size()>1){
            //As there are many referenceDataException records with the same check number and account number
            //Lets see if there is a check existing with the same check number and the account number.
            Check check = checkDao.findCheckBy(oldAccountNumber, oldCheckNumber);
            if(check != null){
                //Check is existing so lets check for which exceptional reference data in the list
                //The amount is matching.
                ItemType itemType = itemTypeDao.findById(check.getItemType().getId());
                boolean isActionTaken = false;
                for(ExceptionalReferenceData exceptionalReferenceData:exceptionalReferenceDataList){
                    if(itemType.getItemCode().equals(ItemType.CODE.I)){
                        if(check.getIssuedAmount().equals(exceptionalReferenceData.getAmount())){
                            //Issued amount is matching, so lets take appropriate action.
                            ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData);
                            referenceDataDao.save(referenceData);
                            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData, check);
                            exceptionalReferenceDataDao.delete(exceptionalReferenceData);
                            isActionTaken = true;
                            break;
                        }
                    }else if(itemType.getItemCode().equals(ItemType.CODE.V)){
                        if(check.getVoidAmount().equals(exceptionalReferenceData.getAmount())){
                            //Void amounts are matching, so lets take appropriate actions.
                            ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData);
                            referenceDataDao.save(referenceData);
                            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData, check);
                            exceptionalReferenceDataDao.delete(exceptionalReferenceData);
                            isActionTaken = true;
                            break;
                        }
                    }
                }
                if(!isActionTaken){
                    //There is no exceptional referenceData where the amounts are matching, so lets take the first one in the list
                    //And proceed.
                    ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataList.get(0);
                    ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData);
                    referenceDataDao.save(referenceData);
                    referenceDataProcessorService.processNonDuplicateReferenceData(referenceData, check);
                    exceptionalReferenceDataDao.delete(exceptionalReferenceDataList.get(0));
                }
            }else {
                //There is no check found with the same check number and the account number, so lets take the first one and proceed.
                ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataList.get(0);
                ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData);
                referenceDataDao.save(referenceData);
                referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
                exceptionalReferenceDataDao.delete(exceptionalReferenceDataList.get(0));
            }
        }else if(exceptionalReferenceDataList.size() == 1){
            ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceDataList.get(0));
            referenceDataDao.save(referenceData);
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
            exceptionalReferenceDataDao.delete(exceptionalReferenceDataList.get(0));
        }
    }

    private void swapReferenceAndExceptionalReferenceData(ReferenceData referenceDataInDb, ReferenceData referenceDataFromExceptionalReferenceData) throws ParseException {
        //First get all the values in referenceData and create exceptional referenceData from it.
        ExceptionalReferenceData exceptionalReferenceDataFromReferenceData = referenceDataCreationService.createExceptionalReferenceData(referenceDataInDb);
        //Now copy all the values from exceptional reference data into referenceData in DB
        referenceDataInDb.setTraceNumber(referenceDataFromExceptionalReferenceData.getTraceNumber());
        referenceDataInDb.setDigest(referenceDataFromExceptionalReferenceData.getDigest());
        referenceDataInDb.setAmount(referenceDataFromExceptionalReferenceData.getAmount());
        referenceDataInDb.setFileMetaData(referenceDataFromExceptionalReferenceData.getFileMetaData());
        //Save reference data in DB
        referenceDataDao.update(referenceDataInDb);
        //Mark the exceptionReferenceData which has been created from referenceData in DB with Duplicate Paid or Duplicate stop based on the ItemType.
        if (exceptionalReferenceDataFromReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
            exceptionalReferenceDataFromReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException));
        } else {
            exceptionalReferenceDataFromReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicateStopItemException));
        }
        exceptionalReferenceDataDao.save(exceptionalReferenceDataFromReferenceData);
    }
}

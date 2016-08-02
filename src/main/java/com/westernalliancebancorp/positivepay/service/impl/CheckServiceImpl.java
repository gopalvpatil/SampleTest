package com.westernalliancebancorp.positivepay.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.westernalliancebancorp.positivepay.service.*;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.AccountInfoDto;
import com.westernalliancebancorp.positivepay.dto.AccountInfoForCustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.dto.CheckStatusDto;
import com.westernalliancebancorp.positivepay.dto.DecisionWindowDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.DecisionWindow;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.Workflow;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.scheduler.PositivePaySchedulerFactoryBean;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.CurrencyUtils;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * providing implementation for service methods to work with the Check Model.
 *
 * @author Anand Kumar
 */
@Service(value = "checkService")
public class CheckServiceImpl implements CheckService {

    /**
     * The logger object
     */
    @Loggable
    private Logger logger;
    @Autowired
    private CheckDao checkDao;
    @Autowired
    private BatchDao batchDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    PositivePaySchedulerFactoryBean ppScheduler;
    @Autowired
    ActionDao actionDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    BankDao bankDao;
    @Autowired
    StartStatusService startStatusService;
    @Autowired
    ExceptionStatusService exceptionStatusService;
    @Autowired
    UserDetailDao userDetailDao;
    @Autowired
    UserService userService;
    @Autowired
    AccountService accountService;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    FileTypeDao fileTypeDao;
    @Autowired
    FileDao fileDao;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;
    @Autowired
    DecisionWindowService decisionWindowService;
    @Autowired
    ExceptionTypeService exceptionTypeService;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;

    /**
     * Updates Check to database by calling appropriate dao method.
     *
     * @param check
     * @return check object that was saved
     * @see com.westernalliancebancorp.positivepay.service.CheckService#update(com.westernalliancebancorp.positivepay.model.Check)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check update(Check check) {
        return checkDao.update(check);
    }

    /**
     * Saves a Check to database by calling appropriate dao method.
     *
     * @param check
     * @return Check object that was saved
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check save(Check check) {
        return checkDao.save(check);
    }

    /**
     * Deletes the Check from the database by calling appropriate dao method.
     *
     * @param check
     * @see com.westernalliancebancorp.positivepay.service.CheckService#delete(com.westernalliancebancorp.positivepay.model.Check)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(Check check) {
        checkDao.delete(check);
    }

    /**
     * Finds the Check by given id by calling appropriate dao method.
     *
     * @param id to find the Check
     * @return Check object that was saved
     * @see com.westernalliancebancorp.positivepay.service.CheckService#findById(java.lang.Long)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check findById(Long id) {
        return checkDao.findById(id);
    }

    /**
     * finds all the checks from the database by calling appropriate dao method.
     *
     * @return List of Check objects
     * @see com.westernalliancebancorp.positivepay.service.CheckService#findAll()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Check> findAll() {
        return checkDao.findAll();
    }
    
    /**
     * finds all the check status from the database by calling appropriate dao method.
     *
     * @return List of Check objects
     * @see com.westernalliancebancorp.positivepay.service.CheckService#
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CheckStatus> getAllCheckStatus() {
        return checkStatusDao.findAll();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Check> saveAll(List<Check> checks) {
        //Using JDBC Template batch Updates
        List<Check> checksToBeSaved = new ArrayList<Check>();
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(workflow.getId());
        CheckStatus targetCheckStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, "start", checkStatusDao);
        for (Check check : checks) {
            AuditInfo auditInfo = new AuditInfo();
            String name = SecurityUtility.getPrincipal();
            auditInfo.setCreatedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy(name);
            check.setAuditInfo(auditInfo);
            check.setWorkflow(workflow);
            check.setCheckStatus(targetCheckStatus);
            checksToBeSaved.add(check);
        }
        List<Check> insertedChecks = batchDao.insertAllChecks(checksToBeSaved);

        //Schedule this to call StartStatusServiceImpl method.
        try {
			ppScheduler.scheduleCheckProcessJob();
			//startStatusService.processStartChecks();
			//exceptionStatusService.processExceptionChecks();
		} catch (ParseException e) {
			logger.error("Exception while processing checks from Start Status:", e);
		} catch (Exception e) {
			logger.error("Exception while processing checks from Start Status:", e);
		}
        return insertedChecks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Check> manualEntrySaveAll(List<Check> checks) {
        //Using JDBC Template batch Updates
        List<Check> checksToBeSaved = new ArrayList<Check>();
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(workflow.getId());
        CheckStatus targetCheckStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, "start", checkStatusDao);
        for (Check check : checks) {
            AuditInfo auditInfo = new AuditInfo();
            String name = SecurityUtility.getPrincipal();
            auditInfo.setCreatedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy(name);
            check.setAuditInfo(auditInfo);
            check.setWorkflow(workflow);
            check.setCheckStatus(targetCheckStatus);
            checksToBeSaved.add(check);
        }
        List<Check> insertedChecks = batchDao.insertAllChecks(checksToBeSaved);
        return insertedChecks;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isDuplicate(Check check) {
        return checkDao.isDuplicate(check);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check findCheckBy(String strAccountNumber, String strCheckNumber,
                             BigDecimal dCheckAmount) throws Exception {
        return checkDao.findCheckBy(strAccountNumber, strCheckNumber, dCheckAmount);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check findCheckBy(String strAccountNumber, String strCheckNumber) {
        return checkDao.findCheckBy(strAccountNumber, strCheckNumber);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<CheckDto> findAllChecksInExceptionForUserCompany() {
    	if(SecurityUtility.isLoggedInUserBankAdmin()) {
    		throw new RuntimeException("Invalid API usage - This method should not be called for bank admin.");
    	}
    	Company userCompany = userService.getLoggedInUserCompany();
        List<Check> checks = checkDao.findAllChecksInExceptionForUserCompany(userCompany);
        List<CheckDto> checksInException = new ArrayList<CheckDto>();
        for (Check check : checks) {
            CheckDto checkInException = new CheckDto();
            BeanUtils.copyProperties(check, checkInException);
            checkInException.setAccountNumber(check.getAccount().getNumber());
            ReferenceData refData = check.getReferenceData();
    		if(refData != null) {
    			checkInException.setPaidAmount(refData.getAmount());
    		}
            if(check.getCheckStatus().getExceptionalStatus()) {
            	checkInException.setExceptionType(check.getCheckStatus().getDescription());
            	checkInException.setExceptionStatus("OPEN");
    		} else {
    			checkInException.setExceptionStatus("CLOSED");
    		}
            checkInException.setExceptional(check.getCheckStatus().getExceptionalStatus());
            checkInException.setStatusName(check.getCheckStatus().getName());
            checkInException.setWorkflowId(check.getWorkflow().getId());
            checksInException.add(checkInException);
        }
        return checksInException;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addHistoryEntryForDuplicateChecks(Check returnCheck, String comment,Action action) {
    	//Check returnCheck = checkDao.findById(checkId);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        BeanUtils.copyProperties(returnCheck, checkHistory);
        //Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        checkHistory.setMatchStatus(returnCheck.getMatchStatus() == null ? Constants.UNMATCHED : returnCheck.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount() == null ? returnCheck.getVoidAmount() : returnCheck.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(comment);
        checkHistoryDao.save(checkHistory);
		return checkHistory;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addHistoryEntryForDuplicateChecks(Long checkId, String comment,Action action) {
    	Check returnCheck = checkDao.findById(checkId);
        CheckHistory checkHistory = addHistoryEntryForDuplicateChecks(returnCheck,comment,action);
		return checkHistory;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check changePayee(Long checkId, String payee, String comment) {
        Check returnCheck = checkDao.findById(checkId);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        String actualPayee = returnCheck.getPayee();
        returnCheck.setPayee(payee);
        checkDao.update(returnCheck);
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CHANGE_PAYEE, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        BeanUtils.copyProperties(returnCheck, checkHistory);
        checkHistory.setMatchStatus(returnCheck.getMatchStatus()==null?"UNMATCHED":returnCheck.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount()==null?returnCheck.getVoidAmount():returnCheck.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("Check payee has been changed from \"%s\" to \"%s\" by taking the action \"%s\"", actualPayee == null ? "None" : actualPayee, payee, "Change Payee"));
        if (comment != null && !comment.isEmpty()) {
            checkHistory.setUserComment(comment);
        }
        checkHistoryDao.save(checkHistory);
        return returnCheck;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addComment(Long checkId, String comment) {
    	Check returnCheck = checkDao.findById(checkId);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CHANGE_PAYEE, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        BeanUtils.copyProperties(returnCheck, checkHistory);
        checkHistory.setMatchStatus(returnCheck.getMatchStatus() == null ? Constants.UNMATCHED : returnCheck.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount() == null ? returnCheck.getVoidAmount() : returnCheck.getIssuedAmount());
        if(checkHistory.getCheckAmount()==null) /** Special cases for paidNotIssued where no issuedAmount/voidAmount is present **/
        {
            ReferenceData referenceData = referenceDataDao.findById(returnCheck.getReferenceData().getId());
            checkHistory.setCheckAmount(referenceData.getAmount());
        }
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("Added comment \"%s\" for Check Number: \"%s\" ", comment, String.valueOf(returnCheck.getCheckNumber())));
        if (comment != null && !comment.isEmpty()) {
            checkHistory.setUserComment(comment);
        }
        checkHistoryDao.save(checkHistory);
		return checkHistory;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check changeDate(Long checkId, String date, String comment) throws ParseException {
        Check returnCheck = checkDao.findById(checkId);
        Date newDate = DateUtils.wdf.parse(date);
        Date actualDate = returnCheck.getIssueDate();
        returnCheck.setIssueDate(newDate);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        checkDao.update(returnCheck);
        CheckHistory checkHistory = new CheckHistory();
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CHANGE_ISSUE_DATE, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount()==null?returnCheck.getVoidAmount():returnCheck.getIssuedAmount());
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        BeanUtils.copyProperties(returnCheck, checkHistory);
        checkHistory.setId(null);
        checkHistory.setMatchStatus(returnCheck.getMatchStatus() == null ? "UNMATCHED" : returnCheck.getMatchStatus());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("Check issued date has been changed from \"%s\" to \"%s\" by taking the action \"%s\"",
                actualDate == null ? "None" : DateUtils.getWALFormatDate(actualDate), DateUtils.getWALFormatDate(newDate), "Change date"));
        if (comment != null && !comment.isEmpty()) {
            checkHistory.setUserComment(comment);
        }
        checkHistoryDao.save(checkHistory);
        return returnCheck;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addHistoryEntryForNewCheck(Long checkId, String source) {
        Check returnCheck = checkDao.findById(checkId);
        return addHistoryEntryForNewCheck(returnCheck, source, returnCheck.getItemType());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addHistoryEntryForNewCheck(Check returnCheck, String source, ItemType itemType) {
        if (source == null) {
            source = PositivePayThreadLocal.getInputMode() == null ? PositivePayThreadLocal.getSource() : PositivePayThreadLocal.getInputMode();
        }
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        itemTypeDao.findById(itemType.getId());
        CheckHistory checkHistory = new CheckHistory();
        BeanUtils.copyProperties(returnCheck, checkHistory);
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CREATED, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setSource(source);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        checkHistory.setVoidAmount(returnCheck.getVoidAmount());
        checkHistory.setMatchStatus(returnCheck.getMatchStatus()==null?Constants.UNMATCHED:returnCheck.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount()==null?returnCheck.getVoidAmount():returnCheck.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        try
        {
            if(itemType.getItemCode().equals(ItemType.CODE.P.name())){
        	List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(returnCheck.getCheckNumber(), returnCheck.getAccount().getId(), ReferenceData.ITEM_TYPE.PAID, ReferenceData.STATUS.NOT_PROCESSED);
        	ReferenceData referenceDataPaid = matchedReferenceDataList.get(0);
        	Double paidAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceDataPaid.getAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Paid item on %s for $%s ",DateUtils.getWALFormatDateString(referenceDataPaid.getPaidDate()),NumberFormat.getNumberInstance(Locale.US).format(paidAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.S.name())){
        	List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(returnCheck.getCheckNumber(), returnCheck.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP, ReferenceData.STATUS.NOT_PROCESSED);
        	ReferenceData referenceDataStop = matchedReferenceDataList.get(0);
        	Double stopAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceDataStop.getAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Stop item on %s for $%s ",DateUtils.getWALFormatDateString(referenceDataStop.getStopDate()),NumberFormat.getNumberInstance(Locale.US).format(stopAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.I.name())){
        	Double issuedAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(returnCheck.getIssuedAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Issued item on %s for $%s ",DateUtils.getWALFormatDateString(returnCheck.getIssueDate()),NumberFormat.getNumberInstance(Locale.US).format(issuedAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.V.name())){
        	Double voidAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(returnCheck.getVoidAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Void item on %s for $%s ",DateUtils.getWALFormatDateString(returnCheck.getVoidDate()),NumberFormat.getNumberInstance(Locale.US).format(voidAmount)));
            }
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        checkHistory.setUserComment("None");
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }
    
    /*** Special method for manual Entry used for sequence exceptions **/
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckHistory addHistoryEntryForNewCheck(Check returnCheck, String source, ItemType itemType,Date date,BigDecimal amount) {
        if (source == null) {
            source = PositivePayThreadLocal.getInputMode() == null ? PositivePayThreadLocal.getSource() : PositivePayThreadLocal.getInputMode();
        }
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        itemTypeDao.findById(itemType.getId());
        CheckHistory checkHistory = new CheckHistory();
        BeanUtils.copyProperties(returnCheck, checkHistory);
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CREATED, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setSource(source);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(amount);
        checkHistory.setIssueDate(date);
        checkHistory.setMatchStatus(returnCheck.getMatchStatus()==null?Constants.UNMATCHED:returnCheck.getMatchStatus());
        checkHistory.setId(null);
        checkHistory.setCheckAmount(amount);
        checkHistory.setAuditInfo(new AuditInfo());
        try
        {
            if(itemType.getItemCode().equals(ItemType.CODE.P.name())){
        	List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(returnCheck.getCheckNumber(), returnCheck.getAccount().getId(), ReferenceData.ITEM_TYPE.PAID, ReferenceData.STATUS.NOT_PROCESSED);
        	ReferenceData referenceDataPaid = matchedReferenceDataList.get(0);
        	Double paidAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceDataPaid.getAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Paid item on %s for $%s ",DateUtils.getWALFormatDateString(referenceDataPaid.getPaidDate()),NumberFormat.getNumberInstance(Locale.US).format(paidAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.S.name())){
        	List<ReferenceData> matchedReferenceDataList = referenceDataDao.findByCheckNumberAccountIdItemTypeAndStatus(returnCheck.getCheckNumber(), returnCheck.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP, ReferenceData.STATUS.NOT_PROCESSED);
        	ReferenceData referenceDataStop = matchedReferenceDataList.get(0);
        	Double stopAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceDataStop.getAmount().floatValue()));
                checkHistory.setSystemComment(String.format("Stop item on %s for $%s ",DateUtils.getWALFormatDateString(referenceDataStop.getStopDate()),NumberFormat.getNumberInstance(Locale.US).format(stopAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.I.name())){
        	Double issuedAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(amount.floatValue()));
                checkHistory.setSystemComment(String.format("Issued item on %s for $%s ",DateUtils.getWALFormatDateString(date),NumberFormat.getNumberInstance(Locale.US).format(issuedAmount)));
            }else if(itemType.getItemCode().equals(ItemType.CODE.V.name())){
        	checkHistory.setVoidAmount(amount);
        	checkHistory.setVoidDate(date);
        	checkHistory.setIssuedAmount(null);
        	checkHistory.setIssueDate(null);
        	Double voidAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(amount.floatValue()));
                checkHistory.setSystemComment(String.format("Void item on %s for $%s ",DateUtils.getWALFormatDateString(date),NumberFormat.getNumberInstance(Locale.US).format(voidAmount)));
            }
        } catch (ParseException pe) {
            throw new RuntimeException(pe);
        }
        checkHistory.setUserComment("None");
        checkHistoryDao.save(checkHistory);
        return checkHistory;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckDto getCheckDetails(Long checkId) {
    	return batchDao.findCheckById(checkId);
    }

    @Override
    public Map<Long, Integer> getProcessedItemsCountOfFile(List<FileMetaData> fileMetaDataList) {
        List<Long> fileIdList = Lambda.extract(fileMetaDataList, Lambda.on(FileMetaData.class).getId());
        return batchDao.getProcessedItemsCountOfFile(fileIdList);
    }

    @Override
    public Map<Long, Integer> getUnProcessedItemsCountOfFile(List<FileMetaData> fileMetaDataList) {
        List<Long> fileIdList = Lambda.extract(fileMetaDataList, Lambda.on(FileMetaData.class).getId());
        return batchDao.getExceptionalItemsCountOfFile(fileIdList);
    }

	@Override
	public long findItemsLoadedBy(Long id) {
		return checkDao.findItemsLoadedBy(id);
	}

    @Override
    public AccountInfoForCustomerDashboardDto getCustomerAccountInfo() {
    	AccountInfoForCustomerDashboardDto accountInfoForCustomerDashboardDto = new AccountInfoForCustomerDashboardDto();
        List<AccountInfoDto> accountInfoDtoList = new ArrayList<AccountInfoDto>();
        Company company = userService.getLoggedInUserCompany();
        //Get the decision window for the company
        DecisionWindow decisionWindow = company.getDecisionWindow();
        DecisionWindowDto decisionWindowDto = new DecisionWindowDto();
        decisionWindowDto.setEnd(DateUtils.convertFromMilitaryToNormalTime(decisionWindow.getEndWindow().toString()));
        decisionWindowDto.setStart(DateUtils.convertFromMilitaryToNormalTime(decisionWindow.getStartWindow().toString()));
        decisionWindowDto.setTimezone(decisionWindow.getTimeZone());
        if(!decisionWindowService.isWithinDecisionWindow(decisionWindow)){
        	decisionWindowDto.setOutSideWindow(Boolean.TRUE);
        } else {
        	decisionWindowDto.setOutSideWindow(Boolean.FALSE);
        }
        accountInfoForCustomerDashboardDto.setDecisionWindow(decisionWindowDto);
        //Get all the accounts for the company
        List<Account> companyAccountList = accountDao.findAllByCompany(company);
        if(companyAccountList != null && !companyAccountList.isEmpty()) {
            List<Long> accountIdList = Lambda.extract(companyAccountList, Lambda.on(Account.class).getId());
            accountInfoDtoList = batchDao.getCustomerAccountInfo(accountIdList);
        }
        accountInfoForCustomerDashboardDto.setAccountInfoDtoList(accountInfoDtoList);
        return accountInfoForCustomerDashboardDto;
    }
    
    @Override
    public List<CheckStatusDto> getDisplayableCheckStatus() {
    	List<CheckStatusDto> checkStatusDtos = new ArrayList<CheckStatusDto>();
    	for(CheckStatus.DISPLAYABLE_CHECK_STATUS disCheck_STATUS : CheckStatus.DISPLAYABLE_CHECK_STATUS.values()) {
    		CheckStatusDto checkStatusDto = new CheckStatusDto();
    		checkStatusDto.setName(disCheck_STATUS.getName());
    		checkStatusDto.setDescription(disCheck_STATUS.getDescription());
    		checkStatusDtos.add(checkStatusDto);
    	}
        return checkStatusDtos;//batchDao.getDisplayableCheckStatuses();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED) 
    public Long saveAccountInfo(CheckDto checkDto) {
    	Check check = this.findById(checkDto.getId());
    	Account newAccount = accountService.findByAccountNumber(checkDto.getAccountNumber());
    	check.setAccount(newAccount);
    	this.update(check);
    	return check.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Check unmatchAndMatch(String userComment, Long checkId, Long referenceIdToMatch) {
        Check returnCheck = checkDao.findById(checkId);
        ReferenceData refernceDataToUnmatch = null;
        //We need to know if the customer would like to UnMatch and Match or just match
        //If unmatch and match then we will have returnCheck.getReferenceData() should not be null
        //In that case remove the match and update the history that it has been unmatched.
        if(returnCheck.getReferenceData()!=null) {
            refernceDataToUnmatch = referenceDataDao.findById(returnCheck.getReferenceData().getId());
        }
        if(referenceIdToMatch != null) {
            processCheckUnmatch(userComment, returnCheck, refernceDataToUnmatch);
        }
        //Check if the user would like to match this check with someother referenceDataId
        if(referenceIdToMatch == null)
            return returnCheck;
        ReferenceData referenceDataToMatch = referenceDataDao.findById(referenceIdToMatch);
        if(referenceDataToMatch != null && referenceDataToMatch.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            processCheckMatch(userComment, returnCheck,referenceDataToMatch);
        }
        return returnCheck;
    }

    private void processCheckMatch(String userComment, Check returnCheck, ReferenceData referenceDataToMatch) {
        CheckHistory checkHistory = new CheckHistory();
	BeanUtils.copyProperties(returnCheck, checkHistory);
	returnCheck.setReferenceData(referenceDataToMatch);
        checkDao.update(returnCheck);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        referenceDataToMatch.setStatus(ReferenceData.STATUS.PROCESSED);
        referenceDataDao.update(referenceDataToMatch);
        Account referenceDataAccount = accountDao.findById(referenceDataToMatch.getAccount().getId());
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.MATCH, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        checkHistory.setMatchStatus("MATCHED");
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount()==null?returnCheck.getVoidAmount():returnCheck.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("Payment Matched "));
        logger.debug(String.format("Check has been changed matched with paid record " +
                "with check number \"%s\" and account number \"%s\" by taking the action \"%s\"",
                referenceDataToMatch.getCheckNumber(), referenceDataAccount.getNumber(), "Match"));
        if (userComment != null && !userComment.isEmpty()) {
            checkHistory.setUserComment(userComment);
        }
        checkHistoryDao.save(checkHistory);
    }

    private void processCheckUnmatch(String userComment, Check returnCheck, ReferenceData referenceDataToUnmatch) {
        CheckHistory checkHistory = new CheckHistory();
	BeanUtils.copyProperties(returnCheck, checkHistory);
	returnCheck.setReferenceData(null);
        CheckStatus checkStatus = checkStatusDao.findById(returnCheck.getCheckStatus().getId());
        checkDao.update(returnCheck);
        //What should we do with the record that has been unmatched? keep it in process status or not processed status?
        Account referenceDataAccount = accountDao.findById(referenceDataToUnmatch.getAccount().getId());
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.UNMATCH, checkStatus.getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setCheck(returnCheck);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setTargetCheckStatus(checkStatus);
        checkHistory.setAction(action);
        checkHistory.setIssuedAmount(returnCheck.getIssuedAmount());
        checkHistory.setMatchStatus("UNMATCHED");
        checkHistory.setId(null);
        checkHistory.setCheckAmount(returnCheck.getIssuedAmount()==null?returnCheck.getVoidAmount():returnCheck.getIssuedAmount());
        checkHistory.setAuditInfo(new AuditInfo());
        checkHistory.setSystemComment(String.format("Payment Unmatched " ));
        logger.debug(String.format("Check has been changed unmatched with \"%s\" record " +
                "with check number \"%s\" and account number \"%s\" by taking the action \"%s\"",
                referenceDataToUnmatch.getItemType().name(), referenceDataToUnmatch.getCheckNumber(), referenceDataAccount.getNumber(), "Unmatch"));
        if (userComment != null && !userComment.isEmpty()) {
            checkHistory.setUserComment(userComment);
        }
        checkHistoryDao.save(checkHistory);
    }

    @Override
    public List<CheckHistory> getCheckHistory(Long checkId) {
        return checkHistoryDao.findByCheckIdWithCheckStatus(checkId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeDuplicateReferenceDataAccountNumber(Long exceptionalReferenceDataId, String changeAccountNumber, String userComment) throws ParseException, CallbackException, WorkFlowServiceException {
        /**
         * When the duplicate reference data check account number is changed (Per say call this as DUPLICATE-RD1)
         * then find if there is any referenceData already existing with the same check number and account number (DUPLICATE-RD2) and ItemCode in referenceData table.
         * If DUPLICATE-RD2 is existing then
         *      And if DUPLICATE-RD2 is referred by a check CHK1 then unMatch DUPLICATE-RD2 with that check and move that check(CHK1) into start status.
         *      If DUPLICATE-RD2 is not referred to any check then move DUPLICATE-RD2 into exceptional reference data as Duplicate (As we now know that DUPLICATE-RD1 is corrected, so
         *      DUPLICATE-RD2 is misread)
         *      Put DUPLICATE-RD1 into ReferenceData table with the status NOT_PROCESSED.
         * If DUPLICATE-RD2 is not existing then
         *      Move DUPLICATE-RD1 into ReferenceData table.
         */
        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionalReferenceDataId);
        /* Create comment for history creation */
       	String historyComment = null;
       	if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.name())) 
       	 {
       	    historyComment = "DuplicatePaidException resolved : ChangeAccountNumber";
       	 }
       	 else if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.name())) 
       	 {
       	     historyComment = "DuplicateStopException resolved : ChangeAccountNumber";
       	 }
        String originalCheckNumber = exceptionalReferenceData.getCheckNumber();
        String originalAccountNumber = exceptionalReferenceData.getAccountNumber();
        exceptionalReferenceData.setAccountNumber(changeAccountNumber);
        short assignedBankNumber = exceptionalReferenceData.getAssignedBankNumber();
        Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber() + "");
        if (account == null) {
            throw new RuntimeException(String.format("Couldn't find the account for ReferenceDataException bearing the accountNumber %s and the assigned bank number %s", changeAccountNumber, assignedBankNumber));
        }
        processChangeActionsOnDuplicateExceptionalReferenceData(userComment, exceptionalReferenceData, account);
        try {
            List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountNumberAndItemType(originalCheckNumber, originalAccountNumber, exceptionalReferenceData.getItemType());
            ReferenceData referenceData = referenceDataList.get(0);
            Account account1 = accountDao.findById(referenceData.getAccount().getId());
            Check check = checkDao.findCheckBy(account1.getNumber(), originalCheckNumber);
            check.setExceptionType(null);
            check.setExceptionResolvedDate(new Date());
            checkDao.save(check);
            /* Put an entry in the check history stating exception has been resolved */
            Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_RESOLVED, check.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
            addHistoryEntryForDuplicateChecks(check, historyComment, action);
        } catch (Exception e) {
            logger.error("Exception occurred while updating the check exception type to null after resolving the exception ", e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeDuplicateReferenceDataCheckNumber(Long exceptionalReferenceDataId, String changedCheckNumber, String userComment) throws ParseException, CallbackException, WorkFlowServiceException {
        /**
         * When the duplicate reference data check serial number is changed (Per say call this as DUPLICATE-RD1)
         * then find if there is any referenceData already existing with the same check number and account number (DUPLICATE-RD2) and ItemCode in referenceData table.
         * If DUPLICATE-RD2 is existing then
         *      And if DUPLICATE-RD2 is referred by a check CHK1 then unMatch DUPLICATE-RD2 with that check and move that check(CHK1) into start status.
         *      If DUPLICATE-RD2 is not referred to any check then move DUPLICATE-RD2 into exceptional reference data as Duplicate (As we now know that DUPLICATE-RD1 is corrected, so
         *      DUPLICATE-RD2 is misread)
         *      Put DUPLICATE-RD1 into ReferenceData table with the status NOT_PROCESSED.
         * If DUPLICATE-RD2 is not existing then
         *      Move DUPLICATE-RD1 into ReferenceData table.
         */

        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionalReferenceDataId);
     /* Create comment for history creation */
        String historyComment = null;
        if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.name())) {
            historyComment = "DuplicatePaidException resolved : ChangeCheckNumber";
        } else if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.name())) {
            historyComment = "DuplicateStopException resolved : ChangeCheckNumber";
        }
        String originalCheckNumber = exceptionalReferenceData.getCheckNumber();
        String originalAccountNumber = exceptionalReferenceData.getAccountNumber();
        exceptionalReferenceData.setCheckNumber(changedCheckNumber);
        String accountNumber = exceptionalReferenceData.getAccountNumber();
        short assignedBankNumber = exceptionalReferenceData.getAssignedBankNumber();
        Account account = fileUploadUtils.accountService.getAccountFromAccountNumberAndAssignedBankNumber(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber() + "");
        if (account == null) {
            throw new RuntimeException(String.format("Couldn't find the account for ReferenceDataException bearing the accountNumber %s and the assigned bank number %s", accountNumber, assignedBankNumber));
        }
        processChangeActionsOnDuplicateExceptionalReferenceData(userComment, exceptionalReferenceData, account);

        try {
            List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountNumberAndItemType(originalCheckNumber, originalAccountNumber, exceptionalReferenceData.getItemType());
            ReferenceData referenceData = referenceDataList.get(0);
            Account account1 = accountDao.findById(referenceData.getAccount().getId());
            Check check = checkDao.findCheckBy(account1.getNumber(), originalCheckNumber);
            check.setExceptionType(null);
            check.setExceptionResolvedDate(new Date());
            checkDao.save(check);
            /* Put an entry in the check history stating exception has been resolved */
            Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_RESOLVED, check.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
            addHistoryEntryForDuplicateChecks(check, historyComment, action);
        } catch (Exception e) {
            logger.error("Exception occurred while updating the check exception type to null after resolving the exception ", e);
        }
    }

    private void processChangeActionsOnDuplicateExceptionalReferenceData(String userComment, ExceptionalReferenceData exceptionalReferenceData, Account account) throws ParseException, WorkFlowServiceException, CallbackException {
        List<ReferenceData> existingReferenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(exceptionalReferenceData.getCheckNumber(), account.getId(), exceptionalReferenceData.getItemType());
        if (existingReferenceDataList.size() > 1) {
            throw new RuntimeException(String.format("Cannot have more than one record with the same account number '%s' check number '%s' and item type '%s'", account.getNumber(), exceptionalReferenceData.getCheckNumber(), exceptionalReferenceData.getItemType().name()));
        }
        ReferenceData referenceDataFromExceptionalReferenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData, account);

        if (existingReferenceDataList.size() < 1) {
            logger.debug(String.format("No other reference data found with the check number %s account number %s and item type %s", exceptionalReferenceData.getCheckNumber(), account.getNumber(), exceptionalReferenceData.getItemType()));
            //Move this exceptionalReferenceData as referenceData and delete the exceptionalReferennceData
            referenceDataDao.save(referenceDataFromExceptionalReferenceData);
            exceptionalReferenceDataDao.delete(exceptionalReferenceData);
            //Call to Moumita's Code to move this ReferenceData into possible state.
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceDataFromExceptionalReferenceData);
        } else if (existingReferenceDataList.size() == 1) {
            ReferenceData existingReferenceData = existingReferenceDataList.get(0);
            ExceptionalReferenceData exceptionalReferenceDataFromExistingReferenceData = referenceDataCreationService.createExceptionalReferenceData(existingReferenceData, account);
            //There is one reference data which is already existing, so check if it is processed or not
            if (existingReferenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
                Long existingRefDataId = existingReferenceData.getId();
                AuditInfo existingRefDataIdAuditInfo = existingReferenceData.getAuditInfo();
                //Delete the existing exceptionalReferenceData first.
                exceptionalReferenceDataDao.delete(exceptionalReferenceData);
                //As the existing one is not processed so move that to exceptional reference data with an assumption that
                //the existing one is misread.
                //Save the new one.
                exceptionalReferenceDataDao.save(exceptionalReferenceDataFromExistingReferenceData);
                //We should not delete the existing referenceData and save the referenceDataFromExceptionalReferenceData as existing referenceData
                //might being referred from Check_history. So we better update the existing one with the corrected data in
                //referenceDataFromExceptionalReferenceData
                BeanUtils.copyProperties(referenceDataFromExceptionalReferenceData, existingReferenceData);
                existingReferenceData.setId(existingRefDataId);
                existingReferenceData.setAuditInfo(existingRefDataIdAuditInfo);
                referenceDataDao.update(existingReferenceData);
                //Now make decision to process the referenceData that you we created, a correct one
                //Can be coded by Moumita with the useCases that we came up with on the other day.
                referenceDataProcessorService.processNonDuplicateReferenceData(existingReferenceData);
            } else {
                //Reference Data is in PROCESSED state, find the check to which it is associated.
                Check checkMappedWithReferenceData = checkDao.findByReferenceDataId(existingReferenceData.getId());
                //A Check has already been mapped to this referenceData and this referenceData is wrong as customer as corrected and exceptional referenceData
                //And if DUPLICATE-RD2 is referred by a check CHK1 then unMatch DUPLICATE-RD2, move that check(CHK1) into start status and update the data of existing
                // ReferenceData DUPLICATE-RD2 with the data of DUPLICATE-RD1 (We cannot delete RD2 as it might be having references in checkHistory).
                //Delete the exceptional referenceData record and create a entry in ExceptionalReferenceData table with the data of DUPLICATE-RD2
                exceptionalReferenceDataDao.save(exceptionalReferenceDataFromExistingReferenceData);
                if (checkMappedWithReferenceData != null) {
                    processCheckUnmatch(userComment, checkMappedWithReferenceData, existingReferenceData);
                    Long existingRefDataId = existingReferenceData.getId();
                    AuditInfo existingRefDataIdAuditInfo = existingReferenceData.getAuditInfo();
                    BeanUtils.copyProperties(referenceDataFromExceptionalReferenceData, existingReferenceData);
                    existingReferenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                    existingReferenceData.setId(existingRefDataId);
                    existingReferenceData.setAuditInfo(existingRefDataIdAuditInfo);
                    referenceDataDao.update(existingReferenceData);
                    Map<String, Object> userData = new HashMap<String, Object>();
                    workflowService.forceStatusChange(checkMappedWithReferenceData, "start", userData);
                } else {
                    BeanUtils.copyProperties(referenceDataFromExceptionalReferenceData, existingReferenceData);
                    existingReferenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                    referenceDataDao.update(existingReferenceData);
                }
                referenceDataProcessorService.processNonDuplicateReferenceData(existingReferenceData);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckStatus getLatestCheckStatus(long checkId) {
    	Check check = checkDao.findById(checkId);
        com.westernalliancebancorp.positivepay.model.CheckStatus checkStatus = checkStatusDao.findById(check.getCheckStatus().getId());
        return checkStatus;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void correctZeroCheckNumber(Long exceptionalReferenceDataId, String changedCheckNumber) throws ParseException, CallbackException, WorkFlowServiceException {
        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionalReferenceDataId);
        Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber() + "");
        if (account == null) {
            throw new RuntimeException(String.format("Couldn't find the account for ReferenceDataException bearing the accountNumber %s and the assigned bank number %s", exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber()));
        }
        List<ReferenceData> existingReferenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(changedCheckNumber, account.getId(), exceptionalReferenceData.getItemType());
        if (existingReferenceDataList.size() > 1) {

            throw new RuntimeException(String.format("Cannot have more than one record with the same account number '%s' check number '%s' and item type '%s'", account.getNumber(), changedCheckNumber, exceptionalReferenceData.getItemType().name()));
        }
        if (existingReferenceDataList.size() < 1) {

            logger.debug(String.format("No other reference data found with the check number %s account number %s and item type %s", changedCheckNumber, account.getNumber(), exceptionalReferenceData.getItemType()));
            //Move this exceptionalReferenceData as referenceData and delete the exceptionalReferennceData
            exceptionalReferenceData.setCheckNumber(changedCheckNumber);
            ReferenceData referenceDataFromExceptionalReferenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData, account);
            referenceDataDao.save(referenceDataFromExceptionalReferenceData);
            exceptionalReferenceDataDao.delete(exceptionalReferenceData);
            //Call to Moumita's Code to move this ReferenceData into possible state.
            referenceDataProcessorService.processNonDuplicateReferenceData(referenceDataFromExceptionalReferenceData);

        } else if (existingReferenceDataList.size() == 1) {
            throw new RuntimeException(String.format("The check number provided already exists in the system "));
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void correctZeroCheckNumberByReferenceDataId(Long referenceDataId, String changedCheckNumber) throws CallbackException, WorkFlowServiceException  {
	/** To be used in scenarios where the check number cannot be read and has been saved as random negative numbers in reference data **/
	ReferenceData referenceData = referenceDataDao.findById(referenceDataId);
	referenceData.setCheckNumber(changedCheckNumber);
	referenceDataDao.update(referenceData);
	referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
	
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void payDuplicateReferenceData(Long exceptionalReferenceDataId,
	    String userComment) throws ParseException, WorkFlowServiceException, CallbackException {
	ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionalReferenceDataId);
	     /* Create comment for history creation */
	        String historyComment = "DuplicatePaidException resolved : pay";
	        /* Create a new check number by adding a leading zero to the existing check number */
	        String originalCheckNumber = exceptionalReferenceData.getCheckNumber();
	        String originalAccountNumber = exceptionalReferenceData.getAccountNumber();
	        String changedCheckNumber = addPrecedingZero(exceptionalReferenceData.getCheckNumber());
	        String accountNumber = exceptionalReferenceData.getAccountNumber();
	        short assignedBankNumber = exceptionalReferenceData.getAssignedBankNumber();
	        Account account = fileUploadUtils.accountService.getAccountFromAccountNumberAndAssignedBankNumber(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber()+"");
	        if (account == null) {
	            throw new RuntimeException(String.format("Couldn't find the account for ReferenceDataException bearing the accountNumber %s and the assigned bank number %s", accountNumber, assignedBankNumber));
	        }
	        do{
	        List<ReferenceData> existingReferenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(changedCheckNumber, account.getId(), exceptionalReferenceData.getItemType());
	        if (existingReferenceDataList.size() > 1) {
	            
	            throw new RuntimeException(String.format("Cannot have more than one record with the same account number '%s' check number '%s' and item type '%s'", account.getNumber(), changedCheckNumber, exceptionalReferenceData.getItemType().name()));
	        }
	        if (existingReferenceDataList.size() < 1) {
	            
	            logger.debug(String.format("No other reference data found with the check number %s account number %s and item type %s", changedCheckNumber, account.getNumber(), exceptionalReferenceData.getItemType()));
	            //Move this exceptionalReferenceData as referenceData and delete the exceptionalReferennceData
	            exceptionalReferenceData.setCheckNumber(changedCheckNumber);
	            ReferenceData referenceData = referenceDataCreationService.createReferenceData(exceptionalReferenceData,account);
	            referenceDataDao.save(referenceData);
	            exceptionalReferenceDataDao.delete(exceptionalReferenceData);
	            //Now since the new check number with leading zero, is in reference data, create and match with check
	            Check check = createCheckFromReferenceData(referenceData);
                    logger.debug("Corresponding check id "+check.getId()+" for reference data id: "+referenceData.getId());
                    
                    logger.debug("Started Performing workflow action created for check id " + check.getId());
                    Map<String, Object> userData = new HashMap<String, Object>();
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                    workflowService.performAction(check, "created", userData);
                    logger.debug("Completed Performing workflow action created for check id " + check.getId());
	            break;
	            
	        } else if (existingReferenceDataList.size() == 1) {
	            //Add one additional zero before the check number
	            changedCheckNumber = addPrecedingZero(changedCheckNumber);
	        }
	        }while (true);
	        
	        try {
	            List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountNumberAndItemType(originalCheckNumber, originalAccountNumber, exceptionalReferenceData.getItemType());
	            ReferenceData referenceData = referenceDataList.get(0);
	            Account account1 = accountDao.findById(referenceData.getAccount().getId());
	            Check checkOld = checkDao.findCheckBy(account1.getNumber(), originalCheckNumber);
	            checkOld.setExceptionType(null);
	            checkOld.setExceptionResolvedDate(new Date());
	            checkDao.save(checkOld);
	            /* Put an entry in the check history stating exception has been resolved */
	            Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_RESOLVED, checkOld.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
	            addHistoryEntryForDuplicateChecks(checkOld, historyComment, action);
	        } catch (Exception e) {
	            logger.error("Exception occurred while updating the check exception type to null after resolving the exception ", e);
	        }
	
    }
    
    private String addPrecedingZero(String checkNumber)
    {
	return Constants.ZERO_CHECK_NUMBER+checkNumber;
    }
    
    private Check createCheckFromReferenceData (ReferenceData referenceData)
    {
        Check check = new Check();
        check.setAccount(referenceData.getAccount());
        check.setIssuedAmount(referenceData.getAmount());
        check.setCheckNumber(referenceData.getCheckNumber());
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
        check.setCheckStatus(checkStatus);
        check.setRoutingNumber(referenceData.getAccount().getBank().getRoutingNumber());
        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
        check.setIssueDate(referenceData.getPaidDate());
        check.setFileMetaData(referenceData.getFileMetaData());
        check.setLineNumber(referenceData.getLineNumber());
        check.setItemType(ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
        check.setDigest(referenceData.getAccount().getNumber() + "" + referenceData.getCheckNumber());
        checkDao.save(check);
        return check;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public CheckDto findCheckByTraceNumber(String traceNumber) {
    	return batchDao.findCheckByTraceNumber(traceNumber);
    }
}

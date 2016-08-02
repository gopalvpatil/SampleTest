package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.ReferenceDataProcessorService;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackContext;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;
import com.westernalliancebancorp.positivepay.workflow.WorkflowUtil;

/**
 * User: Moumita Ghosh
 * Date: 19/6/14
 * Time: 1:03 AM
 */

@Service
public class ReferenceDataProcessorServiceImpl implements ReferenceDataProcessorService {

    @Loggable
    private Logger logger;

    @Autowired
    private ReferenceDataDao referenceDataDao;

    @Autowired
    private CheckDao checkDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    StartStatusService startStatusService;

    @Autowired
    ItemTypeDao itemTypeDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    CheckService checkService;
    
    @Autowired
    WorkflowUtil workflowUtil;

    @Override
    public void processNonDuplicateReferenceData(List<ReferenceData> referenceDataList) {
        for (ReferenceData refData : referenceDataList) {
            try {
                processNonDuplicateReferenceData(refData);
            } catch (CallbackException e) {
                logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, e.getMessage() + " ReferenceData Id " + refData, e), e);
                e.printStackTrace();
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, e.getMessage() + " ReferenceData Id " + refData, e), e);
                e.printStackTrace();
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, re.getMessage() + " ReferenceData Id " + refData, re), re);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processNonDuplicateReferenceData(ReferenceData referenceData, Long checkId) throws CallbackException, WorkFlowServiceException {
            /* If the reference Data is populated by BatchJDBCUpdate, id will not be available */
        if (referenceData.getId() == null) {
            referenceData = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceData.getCheckNumber(), referenceData.getAccount().getId(), referenceData.getItemType()).get(0);
        }
        if (referenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            Check check = checkDao.findById(checkId);
            processNonDuplicateReferenceData(referenceData, check);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processNonDuplicateReferenceData(ReferenceData referenceData, Check check) throws CallbackException, WorkFlowServiceException {
            /* If the reference Data is populated by BatchJDBCUpdate, id will not be available */
        if (referenceData.getId() == null) {
            referenceData = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceData.getCheckNumber(), referenceData.getAccount().getId(), referenceData.getItemType()).get(0);
        }
        if (referenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            if (referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                if (check != null) {
                    if (check.getCheckStatus().getName().equals(CheckStatus.START_STATUS_NAME)) {

                        logger.debug("Check is in Start status,delegating to start status service :Check id " + check.getId());
                        List<Check> checks = new ArrayList<Check>();
                        checks.add(check);
                        startStatusService.processStartChecks(checks);

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME)) {

                        logger.error(String.format("Functionality error cannot expect more than one reference data matching the criteria of "
                                + "check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                	    addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopPaid for check id " + check.getId());
                        workflowService.performAction(check, "stopPaid", userData);
                        logger.debug("Completed Performing workflow action stopPaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action voidPaid for check id " + check.getId());
                        workflowService.performAction(check, "voidPaid", userData);
                        logger.debug("Completed Performing workflow action voidPaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STALE_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stalePaid for check id " + check.getId());
                        workflowService.performAction(check, "stalePaid", userData);
                        logger.debug("Completed Performing workflow action stalePaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.ISSUED_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {

                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            workflowService.performAction(check, "matched", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());

                        } else {

                            logger.debug("Started Performing workflow action invalidAmountPaid for check id " + check.getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidAmountException);
                            workflowService.performAction(check, "invalidAmountPaid", userData);
                            logger.debug("Completed Performing workflow action invalidAmountPaid for check id " + check.getId());
                        }

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.INVALID_AMOUNT_PAID)) {
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {
                            addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            String commentString = String.format("During the process of changing the check number to '%s' system has identified that there is a check in Invalid amount Paid status," +
                                    " so we are moving it to 'Paid' as the amount are matching and the exceptional state is no longer valid",check.getCheckNumber());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name(),commentString);
                            workflowService.performAction(check, "pay", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());
                        }

                    } 
                    else {
                        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                        logger.error("Unable to handle state : " + check.getCheckStatus().getName() + " Workflow version " + workflowManager.getSupportedVersion() + " Check info " + check + " Reference data " + referenceData);
                    }
                } else { //If the check corresponding to the reference data does not exist in the database, create a new check and move to 'paidNotIssued'
                    check = new Check();
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
                    logger.debug("Corresponding check id "+check.getId()+" for reference data id: "+referenceData.getId());
                    
                    logger.debug("Started Performing workflow action paidNotIssued for check id " + check.getId());
                    workflowService.performAction(check, "paidNotIssued", userData);
                    logger.debug("Completed Performing workflow action paidNotIssued for check id " + check.getId());
                }
            } else if (referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)) {
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                if (check != null) {
                    if (check.getCheckStatus().getName().equals(CheckStatus.START_STATUS_NAME)) {
                	
                	    logger.debug("Check is in Start status,delegating to start status service :Check id " + check.getId());
                        List<Check> checks = new ArrayList<Check>();
                        checks.add(check);
                        startStatusService.processStartChecks(checks);
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                	
                            logger.error(String.format("Functionality error cannot expect more than one reference data matching the criteria of "
                                + "check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopAfterPaid for check id " + check.getId());
                        workflowService.performAction(check, "stopAfterPaid", userData);
                        logger.debug("Completed Performing workflow action StopAfterPaid for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopAfterVoid for check id " + check.getId());
                	    userData.put(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_REFERENCE_DATA.name(), referenceData);
                        workflowService.performAction(check, "stopAfterVoid", userData);
                        logger.debug("Completed Performing workflow action stopAfterVoid for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STALE_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action staleStop for check id " + check.getId());
                        workflowService.performAction(check, "staleStop", userData);
                        logger.debug("Completed Performing workflow action staleStop for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.ISSUED_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {

                            logger.debug("Started Performing workflow action Stop for check id " + check.getId());
                            workflowService.performAction(check, "stop", userData);
                            logger.debug("Completed Performing workflow action Stop for check id " + check.getId());
                            
                        } else {
                            
                            logger.debug("Started Performing workflow action invalidAmountStop for check id " + check.getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidStopAmountException);
                            workflowService.performAction(check, "invalidAmountStop", userData);
                            logger.debug("Completed Performing workflow action invalidAmountStop for check id " + check.getId());
                            
                        }
                    } 
                    else if (check.getCheckStatus().getName().equals(CheckStatus.INVALID_AMOUNT_STOP)) {
                         if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {
                            addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            String commentString = String.format("During the process of changing the check number to '%s' system has identified that there is a check in Invalid amount Stop status," +
                                    " so we are moving it to 'Stop' as the amount are matching and the exceptional state is no longer valid",check.getCheckNumber());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name(),commentString);
                            workflowService.performAction(check, "stop", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());
                        }

                    } 
                    else {
                        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                        logger.error("Unable to handle state : " + check.getCheckStatus().getName() + " Workflow version " + workflowManager.getSupportedVersion() + " Check info " + check + " Reference data " + referenceData);
                    }
                } else {    //If the check corresponding to the reference data does not exist in the database, create a new check and move to 'stopNotIssued'
                    check = new Check();
                    check.setAccount(referenceData.getAccount());
                    check.setIssuedAmount(referenceData.getAmount());
                    check.setCheckNumber(referenceData.getCheckNumber());
                    Workflow workflow = workflowManagerFactory.getLatestWorkflow();
                    CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
                    check.setCheckStatus(checkStatus);
                    check.setRoutingNumber(referenceData.getAccount().getBank().getRoutingNumber());
                    check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                    check.setIssueDate(referenceData.getStopDate());
                    check.setFileMetaData(referenceData.getFileMetaData());
                    check.setLineNumber(referenceData.getLineNumber());
                    check.setItemType(ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                    check.setDigest(referenceData.getAccount().getNumber() + "" + referenceData.getCheckNumber());
                    checkDao.save(check);
                    logger.debug("Corresponding check id "+check.getId()+" for reference data id: "+referenceData.getId());
                    
                    logger.debug("Started Performing workflow action stopNotIssued for check id " + check.getId());
                    workflowService.performAction(check, "stopNotIssued", userData);
                    logger.debug("Completed Performing workflow action stopNotIssued for check id " + check.getId());
                }
            }
        }
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processNonDuplicateReferenceData(ReferenceData referenceData, Check check,CallbackContext callbackContext) throws CallbackException, WorkFlowServiceException {
            /* If the reference Data is populated by BatchJDBCUpdate, id will not be available */
        if (referenceData.getId() == null) {
            referenceData = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceData.getCheckNumber(), referenceData.getAccount().getId(), referenceData.getItemType()).get(0);
        }
        if (referenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            if (referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                if (check != null) {
                    if (check.getCheckStatus().getName().equals(CheckStatus.START_STATUS_NAME)) {

                        logger.debug("Check is in Start status,delegating to start status service :Check id " + check.getId());
                        List<Check> checks = new ArrayList<Check>();
                        checks.add(check);
                        startStatusService.processStartChecks(checks);

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME)) {

                        logger.error(String.format("Functionality error cannot expect more than one reference data matching the criteria of "
                                + "check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                	    addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopPaid for check id " + check.getId());
                        workflowService.performAction(check, "stopPaid", userData);
                        logger.debug("Completed Performing workflow action stopPaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action voidPaid for check id " + check.getId());
                        workflowService.performAction(check, "voidPaid", userData);
                        logger.debug("Completed Performing workflow action voidPaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STALE_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stalePaid for check id " + check.getId());
                        workflowService.performAction(check, "stalePaid", userData);
                        logger.debug("Completed Performing workflow action stalePaid for check id " + check.getId());

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.ISSUED_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {

                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            workflowService.performAction(check, "matched", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());

                        } else {

                            logger.debug("Started Performing workflow action invalidAmountPaid for check id " + check.getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidAmountException);
                            workflowService.performAction(check, "invalidAmountPaid", userData);
                            logger.debug("Completed Performing workflow action invalidAmountPaid for check id " + check.getId());
                        }

                    } else if (check.getCheckStatus().getName().equals(CheckStatus.INVALID_AMOUNT_PAID)) {
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {
                            addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            String commentString = String.format("During the process of changing the check number to '%s' system has identified that there is a check in Invalid amount Paid status," +
                                    " so we are moving it to 'Paid' as the amount are matching and the exceptional state is no longer valid",check.getCheckNumber());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name(),commentString);
                            workflowService.performAction(check, "pay", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());
                        }

                    } 
                    else {
                        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                        logger.error("Unable to handle state : " + check.getCheckStatus().getName() + " Workflow version " + workflowManager.getSupportedVersion() + " Check info " + check + " Reference data " + referenceData);
                    }
                } else { //If the check corresponding to the reference data does not exist in the database, create a new check and move to 'paidNotIssued'
                    check = new Check();
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
                    logger.debug("Corresponding check id "+check.getId()+" for reference data id: "+referenceData.getId());
                    //Populate history in case of Change Check Number/Account Number
                    if(callbackContext.getActionNameToPerform().equals(Action.ACTION_NAME.CHANGE_CHECK_NUMBER.getName()))
                    {
                	Map<String, Object> map = callbackContext.getUserData();
                        String comment = String.format("Check number changed from \"%s\" to \"%s\"",map.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_OLD.name()),check.getCheckNumber());
                        workflowUtil.insertNonWorkflowActionIntoHistory(check, comment,Action.ACTION_NAME.CHANGE_CHECK_NUMBER,check.getCheckStatus(),referenceData );
                    }
                    if(callbackContext.getActionNameToPerform().equals(Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER.getName()))
                    {
                	Map<String, Object> map = callbackContext.getUserData();
                	String comment = String.format("Account number changed from \"%s\" to \"%s\"",map.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_OLD.name()),map.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name()));
                        workflowUtil.insertNonWorkflowActionIntoHistory(check, comment,Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER,check.getCheckStatus(),referenceData );
                    }
                    logger.debug("Started Performing workflow action paidNotIssued for check id " + check.getId());
                    workflowService.performAction(check, "paidNotIssued", userData);
                    logger.debug("Completed Performing workflow action paidNotIssued for check id " + check.getId());
                }
            } else if (referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)) {
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                if (check != null) {
                    if (check.getCheckStatus().getName().equals(CheckStatus.START_STATUS_NAME)) {
                	
                	    logger.debug("Check is in Start status,delegating to start status service :Check id " + check.getId());
                        List<Check> checks = new ArrayList<Check>();
                        checks.add(check);
                        startStatusService.processStartChecks(checks);
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME)) {
                	
                            logger.error(String.format("Functionality error cannot expect more than one reference data matching the criteria of "
                                + "check number %s and account id %d", check.getCheckNumber(), check.getAccount().getId()));
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopAfterPaid for check id " + check.getId());
                        workflowService.performAction(check, "stopAfterPaid", userData);
                        logger.debug("Completed Performing workflow action StopAfterPaid for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.VOID_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action stopAfterVoid for check id " + check.getId());
                	    userData.put(WorkflowService.STANDARD_MAP_KEYS.MANUAL_ENTRY_REFERENCE_DATA.name(), referenceData);
                        workflowService.performAction(check, "stopAfterVoid", userData);
                        logger.debug("Completed Performing workflow action stopAfterVoid for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.STALE_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                	    logger.debug("Started Performing workflow action staleStop for check id " + check.getId());
                        workflowService.performAction(check, "staleStop", userData);
                        logger.debug("Completed Performing workflow action staleStop for check id " + check.getId());
                        
                    } else if (check.getCheckStatus().getName().equals(CheckStatus.ISSUED_STATUS_NAME)) {
                        addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                        if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {

                            logger.debug("Started Performing workflow action Stop for check id " + check.getId());
                            workflowService.performAction(check, "stop", userData);
                            logger.debug("Completed Performing workflow action Stop for check id " + check.getId());
                            
                        } else {
                            
                            logger.debug("Started Performing workflow action invalidAmountStop for check id " + check.getId());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.INVALID_AMOUNT_EXCEPTION_TO_SET.name(), ExceptionType.EXCEPTION_TYPE.InvalidStopAmountException);
                            workflowService.performAction(check, "invalidAmountStop", userData);
                            logger.debug("Completed Performing workflow action invalidAmountStop for check id " + check.getId());
                            
                        }
                    } 
                    else if (check.getCheckStatus().getName().equals(CheckStatus.INVALID_AMOUNT_STOP)) {
                         if (referenceData.getAmount().doubleValue() == (check.getIssuedAmount().doubleValue())) {
                            addManualEntryHistory(check, ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                            logger.debug("Started Performing workflow action matched for check id " + check.getId());
                            String commentString = String.format("During the process of changing the check number to '%s' system has identified that there is a check in Invalid amount Stop status," +
                                    " so we are moving it to 'Stop' as the amount are matching and the exceptional state is no longer valid",check.getCheckNumber());
                            userData.put(WorkflowService.STANDARD_MAP_KEYS.USER_COMMENT.name(),commentString);
                            workflowService.performAction(check, "stop", userData);
                            logger.debug("Completed Performing workflow action matched for check id " + check.getId());
                        }

                    } 
                    else {
                        WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                        logger.error("Unable to handle state : " + check.getCheckStatus().getName() + " Workflow version " + workflowManager.getSupportedVersion() + " Check info " + check + " Reference data " + referenceData);
                    }
                } else {    //If the check corresponding to the reference data does not exist in the database, create a new check and move to 'stopNotIssued'
                    check = new Check();
                    check.setAccount(referenceData.getAccount());
                    check.setIssuedAmount(referenceData.getAmount());
                    check.setCheckNumber(referenceData.getCheckNumber());
                    Workflow workflow = workflowManagerFactory.getLatestWorkflow();
                    CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
                    check.setCheckStatus(checkStatus);
                    check.setRoutingNumber(referenceData.getAccount().getBank().getRoutingNumber());
                    check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                    check.setIssueDate(referenceData.getStopDate());
                    check.setFileMetaData(referenceData.getFileMetaData());
                    check.setLineNumber(referenceData.getLineNumber());
                    check.setItemType(ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                    check.setDigest(referenceData.getAccount().getNumber() + "" + referenceData.getCheckNumber());
                    checkDao.save(check);
                    logger.debug("Corresponding check id "+check.getId()+" for reference data id: "+referenceData.getId());
                    //Populate history in case of Change Check Number/Change Account number
                    if(callbackContext.getActionNameToPerform().equals(Action.ACTION_NAME.CHANGE_CHECK_NUMBER.getName()))
                    {
                	Map<String, Object> map = callbackContext.getUserData();
                	String comment = String.format("Check number changed from \"%s\" to \"%s\"",map.get(WorkflowService.STANDARD_MAP_KEYS.CHECK_NUMBER_OLD.name()),check.getCheckNumber());
                        workflowUtil.insertNonWorkflowActionIntoHistory(check, comment,Action.ACTION_NAME.CHANGE_CHECK_NUMBER,check.getCheckStatus(),referenceData );
                    }
                    if(callbackContext.getActionNameToPerform().equals(Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER.getName()))
                    {
                	Map<String, Object> map = callbackContext.getUserData();
                	String comment = String.format("Account number changed from \"%s\" to \"%s\"",map.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_OLD.name()),map.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name()));
                        workflowUtil.insertNonWorkflowActionIntoHistory(check, comment,Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER,check.getCheckStatus(),referenceData );
                    }
                   logger.debug("Started Performing workflow action stopNotIssued for check id " + check.getId());
                    workflowService.performAction(check, "stopNotIssued", userData);
                    logger.debug("Completed Performing workflow action stopNotIssued for check id " + check.getId());
                }
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processNonDuplicateReferenceData(ReferenceData referenceData) throws CallbackException, WorkFlowServiceException {
            /* If the reference Data is populated by BatchJDBCUpdate, id will not be available */
        if (referenceData.getId() == null) {
            referenceData = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceData.getCheckNumber(), referenceData.getAccount().getId(), referenceData.getItemType()).get(0);
        }
        if (referenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            Check check = checkDao.findCheckBy(referenceData.getAccount().getNumber(), referenceData.getCheckNumber());
            processNonDuplicateReferenceData(referenceData, check);
        }

    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processNonDuplicateReferenceData(ReferenceData referenceData,CallbackContext callbackContext) throws CallbackException, WorkFlowServiceException {
            /* If the reference Data is populated by BatchJDBCUpdate, id will not be available */
        if (referenceData.getId() == null) {
            referenceData = referenceDataDao.findByCheckNumberAccountIdAndItemType(referenceData.getCheckNumber(), referenceData.getAccount().getId(), referenceData.getItemType()).get(0);
        }
        if (referenceData.getStatus().equals(ReferenceData.STATUS.NOT_PROCESSED)) {
            Check check = checkDao.findCheckBy(referenceData.getAccount().getNumber(), referenceData.getCheckNumber());
            processNonDuplicateReferenceData(referenceData, check,callbackContext);
        }

    }

    private void addManualEntryHistory(Check check, ItemType itemType) {
        if (PositivePayThreadLocal.getInputMode() != null && PositivePayThreadLocal.getInputMode().equals(PositivePayThreadLocal.INPUT_MODE.ManualEntry.toString())) {
            checkService.addHistoryEntryForNewCheck(check, PositivePayThreadLocal.getInputMode(), itemType);
        }
    }
}

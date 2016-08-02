package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.*;

import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.StopReturnService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * This class is a replacement for "StopPresentedService", a background job which has a wrong logic.
 * User: gduggirala
 * Date: 16/4/14
 * Time: 2:19 PM
 */
@Service
public class StopReturnServiceImpl implements StopReturnService {
    @Loggable
    Logger logger;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    FileDao fileDao;
    @Autowired
    ItemTypeDao itemTypeDao;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    FileTypeDao fileTypeDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;
    /**
     * Write another method to accept reference data objects which has no id's. So search by check#, Account# and ItemCode.
     * @param referenceIds
     * @return
     * @throws WorkFlowServiceException
     * @throws CallbackException
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean processStopReturnReference(List<Long> referenceIds) throws WorkFlowServiceException, CallbackException {
        List<ReferenceData> referenceDataList = referenceDataDao.findAllReferenceDataBy(referenceIds);
        for (ReferenceData referenceData : referenceDataList) {
            logger.info(Log.event(Event.STOP_RETURN_BEGIN, String.format("Stop return transaction has began for referenceData of size %d", referenceIds.size())));
            Check check = checkDao.findCheckBy(referenceData.getAccount().getNumber(), referenceData.getCheckNumber());
            if (check != null) {
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(check.getWorkflow().getId());
                CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, CheckStatus.STOP_STATUS_NAME, checkStatusDao);
                if (check.getCheckStatus().getId().equals(checkStatus.getId())) {
                    ReferenceData stopRefrenceData = check.getReferenceData();
                    if (referenceData == null) {
                        throw new WorkFlowServiceException(String.format("Check is: %d is in stop status but there is no referenceId associated with it please check", check.getId()));
                    }
                    Map<String, Object> userData = new HashMap<String, Object>();
                    //As we are moving into "No Pay" status we should set the reference id based on which we are moving it
                    //into that state.
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                    workflowService.performAction(check, "stopPresentedNoPay", userData);

                    //Once the check is in "stopPresented" state move it into "Stop" again setting the
                    //old referenceId
                    userData.clear();
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), stopRefrenceData);
                    workflowService.performAction(check, "stop", userData);
                    logger.info(Log.event(Event.STOP_RETURN_SUCCESSFULL, String.format("Stop return successful for the check Id %d and the reference id %d", check.getId(), referenceData.getId())));
                }
            } else {
                //Check not present in check_detail table, create a new entry in check detail
                WorkflowManager workflowManager = workflowManagerFactory.getWorkflowManagerById(workflowManagerFactory.getLatestWorkflow().getId());
                CheckStatus startStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManager, "start", checkStatusDao);
                Account account = accountDao.findById(referenceData.getAccount().getId());
                ItemType stopItemType=itemTypeDao.findByCode(ItemType.CODE.S.name());
                Check newCheck = new Check();
                newCheck.setId(null);
                newCheck.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                newCheck.setCheckNumber(referenceData.getCheckNumber());
                newCheck.setDigest(account.getNumber() +""+ check.getCheckNumber());
                newCheck.setAccount(account);
                newCheck.setIssueDate(new Date());
                newCheck.setItemType(stopItemType);
                newCheck.setIssuedAmount(referenceData.getAmount());
                newCheck.setCheckStatus(startStatus);
                newCheck.setCheckHistorySet(Collections.<CheckHistory>emptySet());
                AuditInfo auditInfo = new AuditInfo();
                newCheck.setAuditInfo(auditInfo);
                checkDao.save(newCheck);

                ReferenceData referenceDataStopState = new ReferenceData();
                referenceDataStopState.setFileMetaData(ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao));
                referenceDataStopState.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                referenceDataStopState.setItemType(ReferenceData.ITEM_TYPE.STOP);
                referenceDataStopState.setAuditInfo(auditInfo);
                referenceDataStopState.setTraceNumber(UUID.randomUUID().toString());
                referenceDataStopState.setAccount(newCheck.getAccount());
                referenceDataStopState.setCheckNumber(newCheck.getCheckNumber());
                referenceDataStopState.setAmount(newCheck.getIssuedAmount());
                referenceDataDao.save(referenceDataStopState);

                //As we are moving into "Stop" status we should set the reference id based on the new reference data created with item code STOP

                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceDataStopState);

                workflowService.performAction(newCheck, "stop", userData);

                //As we are moving into "No Pay" status we should set the reference id based on which we are moving it
                //into that state.
                userData.clear();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
                workflowService.performAction(newCheck, "stopPresentedNoPay", userData);
                
                //mark the check with exception 'PresentedNotIssuedException'
                ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.PresentedNotIssuedException);
                newCheck.setExceptionType(exceptionType);
                newCheck.setExceptionCreationDate(new Date());
                checkDao.update(newCheck);

                //Once the check is in "stopPresented" state move it into "Stop" again setting the
                //old referenceId
                userData.clear();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceDataStopState);
                workflowService.performAction(newCheck, "stop", userData);

            }
            logger.info(Log.event(Event.STOP_RETURN_END, String.format("Stop return transaction has began for referenceData of size %d", referenceIds.size())));
        }
        return Boolean.TRUE;
    }
}

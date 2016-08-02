package com.westernalliancebancorp.positivepay.service.impl;

import java.text.ParseException;
import java.util.*;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.workflow.CallbackContext;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import com.westernalliancebancorp.positivepay.workflow.StatusArrivalCallback;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

/**
 * User:	Gopal Patil
 * Date:	Apr 2, 2014
 * Time:	5:31:50 PM
 */
@Service
public class StartStatusServiceImpl implements StartStatusService {

    @Loggable
    Logger logger;

    @Autowired
    WorkflowManagerFactory workflowManagerFactory;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    CheckDao checkDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ReferenceDataDao referenceDataDao;

    @Autowired
    FileDao fileDao;

    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Autowired
    WorkflowDao workflowDao;

    @Autowired
    ItemTypeDao itemTypeDao;
    
    @Autowired
    FileTypeDao fileTypeDao;

    @Autowired
    ActionDao actionDao;

    /* (non-Javadoc)
     * @see com.westernalliancebancorp.positivepay.service.StartStatusService#processStartChecks(java.util.List)
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void processStartChecks(List<Check> checkList) throws CallbackException, WorkFlowServiceException {
        String actionNameToPerform = "";
        for (Check check : checkList) {
            try {
                Map<String, Object> userData = new HashMap<String, Object>();
                String issuedCode = itemTypeDao.findById(check.getItemType().getId()).getItemCode();
                if (issuedCode.equalsIgnoreCase(ItemType.CODE.I.name())) {
                    actionNameToPerform = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CREATED, check.getWorkflow().getVersion(), Action.ACTION_TYPE.WORK_FLOW_ACTION, actionDao).getName();
                } else if (issuedCode.equalsIgnoreCase(ItemType.CODE.S.name())) {
                    actionNameToPerform = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.STOP, check.getWorkflow().getVersion(), Action.ACTION_TYPE.WORK_FLOW_ACTION, actionDao).getName();
                } else if (issuedCode.equalsIgnoreCase(ItemType.CODE.V.name())) {
                    //Please check VoidStatusArrivalCallback for VoidNotIssuedException
                    actionNameToPerform = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.VOID, check.getWorkflow().getVersion(), Action.ACTION_TYPE.WORK_FLOW_ACTION, actionDao).getName();
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.CALLED_BY.name(), "StartStatusService");
                    userData.put(WorkflowService.STANDARD_MAP_KEYS.CALLED_FOR_ACTION.name(), "void");
                }
                if (!actionNameToPerform.trim().isEmpty()) {
                    handleWorkflowAction(check, actionNameToPerform, userData);
                } else {
                    logger.error("No issue code found for the check.. with id " + check.getId() + " to decide the action");
                }
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.START_STATUS_CHECK_UNSUCCESSFUL, "Unable to process the check " + check.getId(), re), re);
            }
        }
    }

    private void handleWorkflowAction(Check check, String actionToPerform, Map<String, Object> userData) throws CallbackException, WorkFlowServiceException {
        if (actionToPerform.equals("created")) {
            workflowService.performAction(check, actionToPerform, userData);
        } else if (actionToPerform.equals("stop")) {
            handleStopWorkflowAction(check, userData);
        } else if(actionToPerform.equals("void")) {
            workflowService.performAction(check, actionToPerform, userData);
        }
    }

    private void handleStopWorkflowAction(Check check, Map<String, Object> userData) throws CallbackException, WorkFlowServiceException {
        //As there is no referenceData already existing with this stop.. we have to create one.
        //First search for the reference Id
        List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountIdAndItemType(check.getCheckNumber(), check.getAccount().getId(), ReferenceData.ITEM_TYPE.STOP);
        if (referenceDataList == null || referenceDataList.isEmpty()) {
            //Not found one.. so create one.
            try {
                ReferenceData referenceData = createReferenceData(check, ReferenceData.ITEM_TYPE.STOP);
                userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceData);
            } catch (ParseException e) {
                logger.error(String.format("Exception while parsing the date, please check with id %d it is in stop code but might not have corresponding reference data", check.getId()), e);
            }
        }else{
            userData.put(WorkflowService.STANDARD_MAP_KEYS.REFERENCE_DATA.name(), referenceDataList.get(0));
        }
        workflowService.performAction(check, "stop", userData);
    }



    @Override
    public void processStartChecks() throws CallbackException, WorkFlowServiceException {
        List<CheckStatus> checkStatusList = checkStatusDao.findByName(CheckStatus.START_STATUS_NAME);
        List<Check> checkList = checkDao.findChecksByCheckStatusIds(extract(checkStatusList, on(CheckStatus.class).getId()));
        processStartChecks(checkList);
    }

    private ReferenceData createReferenceData(Check check, ReferenceData.ITEM_TYPE item_type) throws ParseException {
        FileMetaData fileMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao,fileTypeDao);
        ReferenceData referenceData = new ReferenceData();
        referenceData.setAccount(check.getAccount());
        referenceData.setAmount(check.getIssuedAmount());
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setItemType(item_type);
        //Stop checks are not supposed to be entered in file upload, but we may get that from manual entry. In that case we have to
        //set the trace number as N/A
        referenceData.setTraceNumber("N/A");
        referenceData.setStopDate(DateUtils.getWALFormatDate(new Date()));
        referenceData.setFileMetaData(fileMetaData);
        referenceDataDao.save(referenceData);
        return referenceData;
    }
}
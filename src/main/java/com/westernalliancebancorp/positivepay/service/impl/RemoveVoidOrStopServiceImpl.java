package com.westernalliancebancorp.positivepay.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.AdjustmentCheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AdjustmentCheck;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckHistory;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.RemoveVoidOrStopService;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManager;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;
import com.westernalliancebancorp.positivepay.workflow.WorkflowUtil;

/**
 * User: gduggirala
 * Date: 16/7/14
 * Time: 11:47 PM
 */

@Service
public class RemoveVoidOrStopServiceImpl implements RemoveVoidOrStopService {
    @Loggable
    private Logger logger;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckDao checkDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    AdjustmentCheckDao adjustmentCheckDao;
    @Autowired
    WorkflowUtil workflowUtil;

    @Override
    public void removeVoidOrStop(Long checkId) throws Exception {
        List<CheckHistory> checkHistoryList = checkHistoryDao.findOrderedCheckHistory(checkId);
        int i = 0;
        WorkflowManager workflowManager = null;

        //Start iterating through that records.
        for (CheckHistory checkHistory : checkHistoryList) {
            //Skip the first record as this record holds the current state of the check, so use that to get the workflowManager of the current check.
            if (checkHistory.getAction().getActionType().equals(Action.ACTION_TYPE.NON_WORK_FLOW_ACTION)) {
                if (checkHistory.getAdjustmentCheck() != null) {
                    //go and delete that adjustment record
                    logger.info("Inactivating adjustment record with id"+checkHistory.getAdjustmentCheck().getId());
                    AdjustmentCheck adjCheck = adjustmentCheckDao.findById(checkHistory.getAdjustmentCheck().getId());
                    if(adjCheck != null)
                    {	
                	adjCheck.setCheck(null);
                	adjCheck.setAmount(BigDecimal.ZERO);
                	adjustmentCheckDao.update(adjCheck);
                    }
                }
             } 
            else if ((checkHistory.getAction().getName().equals(Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER.getName()))||(checkHistory.getAction().getName().equals(Action.ACTION_NAME.CHANGE_CHECK_NUMBER.getName())))
            {
                throw new RuntimeException(String.format("Cannot undo actions Change Check Number/Change Account number "));
            }
            else {
                if (i == 0) {
                    workflowManager = workflowManagerFactory.getWorkflowManagerById(checkHistory.getCheck().getWorkflow().getId());
                    //Some times the first record may get filtered out because of above filtering conditions, so we cannot blindly skip
                    //the first record assuming that, first record target status always hold the current state so lets check if its true first by comparing.
                    if (checkHistory.getTargetCheckStatus().getId().equals(checkHistory.getCheck().getCheckStatus().getId())) {
                        logger.info("Current check status and status from check history are same so I'll not touch it " +
                                "" + checkHistory.getTargetCheckStatus().getName());
                        ReferenceData referenceData = checkHistory.getReferenceData();
                        if (referenceData != null) {
                            referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                            referenceDataDao.update(referenceData);
                        }
                    } else {
                        if (moveToStableStatus(workflowManager, checkHistory)) break;
                    }
                } else {
                    if (moveToStableStatus(workflowManager, checkHistory)) break;
                }
                logger.info(String.format("%d : When the check in '%s' status then action '%s' is taken moving the check into '%s'",
                        checkHistory.getId(), checkHistory.getCheckStatus().getName(), checkHistory.getAction().getName(), checkHistory.getTargetCheckStatus().getName()));
                i++;
            }
        }
    }

    private boolean moveToStableStatus(WorkflowManager workflowManager, CheckHistory checkHistory) {
        //Moved the pointer into the next status so let's see if the target check status is autoResolvingStatus or not
        Boolean isAutoResolvingStatus = workflowManager.isAutoResolvingStatus(checkHistory.getTargetCheckStatus().getName());
        if (!isAutoResolvingStatus) {
            //The target status is not autoResolvable status, so lets set this as current status of the check.
            logger.info(String.format("I can set this status as the current check status : %s and the referenceDataId that I will set is : %d",
                    checkHistory.getTargetCheckStatus().getName(), checkHistory.getReferenceData() != null ? checkHistory.getReferenceData().getId() : 0l));
            String comment = "Remove Void/Stop completed";
            Check check = checkHistory.getCheck();
            workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.REMOVE_STOP_VOID,checkHistory.getTargetCheckStatus(),checkHistory.getReferenceData());
            check.setCheckStatus(checkHistory.getTargetCheckStatus());
            check.setPaymentStatus(workflowManager.getPaymentStatus(checkHistory.getAction().getName(), checkHistory.getCheckStatus().getName()));
            check.setReferenceData(checkHistory.getReferenceData());
            checkDao.update(check);

            
            if(checkHistory.getReferenceData() != null)
            {
        	ReferenceData referenceData = checkHistory.getReferenceData();
        	referenceData.setStatus(ReferenceData.STATUS.PROCESSED);
                referenceDataDao.update(referenceData);
            }
            return true;
        } else {
            logger.info("I cannot set this status as current check status because it is autoResolving status : " + checkHistory.getTargetCheckStatus().getName());
            ReferenceData referenceData = checkHistory.getReferenceData();
            if (referenceData != null) {
                referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
                referenceDataDao.update(referenceData);
            }
        }
        return false;
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.dto.ExceptionCheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.service.VoidAfterStopService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * Created with IntelliJ IDEA.
 * User: moumita
 * Date: 25/4/14
 * Time: 2:55 PM
 */
@Service
public class VoidAfterStopServiceImpl implements VoidAfterStopService {
    @Loggable
    Logger logger;

    @Autowired
    BatchDao batchDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    CheckDao checkDao;

    @Autowired
    ExceptionalCheckDao expCheckDao;

    @Autowired
    ExceptionTypeDao exceptionTypeDao;


    @Override
    public Map<String, Integer> markChecksVoidAfterStop() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.STOP_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());

        //List of all checks from check detail table which are in stop status where check number,
        // and amount are equal with exceptional check detail table and having status void and exception status duplicate in db
        List<ExceptionCheckDto> ExpCheckDtos = batchDao.findAllPaidOrStopButVoidDuplicateChecks(checkStatusIds);
        for (ExceptionCheckDto expCheckDto : ExpCheckDtos) {
            try {
                Map<String, Object> userData = new HashMap<String, Object>();
                /**
                 * Please do not nullify referenceData as it is going into some state which is related to void and as per the rules
                 * that we set for ourselves we have to disassociate reference data here.
                 * However during the next action available we have to reassociate the same reference_data will be used when "Stop" action is taken
                 * In case we have any additional action other than "Stop" or "Ignore" then we may have to deassociate and write a
                 * callback to associate it back when "Stop" action is taken.
                 */
                workflowService.performAction(expCheckDto.getId(), "voidAfterStop", userData);
                ExceptionalCheck expCheckToDelete = expCheckDao.findById(expCheckDto.getExceptionCheckId());
                expCheckDao.delete(expCheckToDelete);
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_VOID_AFTER_STOP_UNSUCCESSFUL, e.getMessage() + " Exceptional check Id " + expCheckDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_VOID_AFTER_STOP_UNSUCCESSFUL, e.getMessage() + " Exceptional check Id " + expCheckDto.getId(), e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_VOID_AFTER_STOP_UNSUCCESSFUL, re.getMessage() + " Exceptional check Id " + expCheckDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import com.westernalliancebancorp.positivepay.service.StaleVoidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User: moumita
 * Date: 03/6/14
 * Time: 2:55 PM
 */
@Component
public class StaleVoidServiceImpl implements StaleVoidService {
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
    public Map<String, Integer> markChecksStaleVoid() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.STALE_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        
        //List of all checks from check detail table which are in stale status where check number,
        // and amount are equal with exceptional check detail table and having status void and exception status duplicate in db
        List<ExceptionCheckDto> ExpCheckDtos = batchDao.findAllPaidOrStopButVoidDuplicateChecks(checkStatusIds);
        for (ExceptionCheckDto expCheckDto : ExpCheckDtos) {
            try {
                ExceptionalCheck expCheckToDelete = expCheckDao.findById(expCheckDto.getExceptionCheckId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name(), expCheckToDelete);
                workflowService.performAction(expCheckDto.getId(), "staleVoid", userData);
                expCheckDao.delete(expCheckToDelete);
                itemsProcessedSuccessfuly++;
                //After that move it to Issued automatically
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_STALE_VOID_UNSUCCESSFUL, e.getMessage() + " Exceptional check Id " + expCheckDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_STALE_VOID_UNSUCCESSFUL, e.getMessage() + " Exceptional check Id " + expCheckDto.getId(), e), e);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_STALE_VOID_UNSUCCESSFUL, re.getMessage() + " Exceptional check Id " + expCheckDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.utility.common.Constants;
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
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.service.VoidAfterPaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * Created with IntelliJ IDEA.
 * User: moumita
 * Date: 25/4/14
 * Time: 2:55 PM
 */
@Service
public class VoidAfterPaidServiceImpl implements VoidAfterPaidService {
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
    public Map<String, Integer> markChecksVoidAfterPaid() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.PAID_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        
        //List of all checks from check detail table which are in paid status where check number,
        // and amount are equal with exceptional check detail table and having status void and exception status duplicate in db
        List<ExceptionCheckDto> ExpCheckDtos = batchDao.findAllPaidOrStopButVoidDuplicateChecks(checkStatusIds);
        for (ExceptionCheckDto expCheckDto : ExpCheckDtos) {
            try {
                ExceptionalCheck expCheckToDelete = expCheckDao.findById(expCheckDto.getExceptionCheckId());
            	Map<String, Object> userData = new HashMap<String, Object>();
                /**
                 * Please do not nullify referenceData as it is going into some state which is related to void and as per the rules
                 * that we set for ourselvs we have to disassociate reference data here.
                 * However during the next action available we have to reassociate the same reference_data will be used when "Pay" action is taken
                 * In case we have any additional action other than "Pay" or "No Pay" then we may have to deassociate and write a
                 * callback to associate it back when "Pay" action is taken.
                 */
                userData.put(WorkflowService.STANDARD_MAP_KEYS.EXCEPTION_CHECK.name(), expCheckToDelete);
                workflowService.performAction(expCheckDto.getId(), "voidAfterPaid", userData);
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

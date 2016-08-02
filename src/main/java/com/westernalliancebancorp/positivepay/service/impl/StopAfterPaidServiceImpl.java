package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.StopAfterPaidService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.on;

/**
 * Check is in the paid status, but we have received request to Stop.
 * We take the action of "StopAfterPaidService" on that check. (SequenceException but will be referring to reference data table.
 * Manual entry inserts will be taken care in ManulaEntryService.
 *
 * User: gduggirala
 * Date: 15/6/14
 * Time: 9:49 AM
 */
@Component
public class StopAfterPaidServiceImpl implements StopAfterPaidService {
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
    AccountDao accountDao;

    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Override
    public Map<String, Integer> markChecksStopAfterPaid() {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.PAID_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        List<CheckDto> checkDtos =batchDao.findAllChecksByAnyStatusNoAmount(batchDao.getAllAccountsIds(), checkStatusIds, ReferenceData.ITEM_TYPE.STOP);
        for(CheckDto checkDto:checkDtos) {
            try {
                logger.debug("CheckDto id " + checkDto.getId());
                Map<String, Object> userData = new HashMap<String, Object>();
                userData.put(Constants.CHECK_DTO, checkDto);
                logger.debug("Started Performing workflow action stopAfterPaid for check id" + checkDto.getId());
                workflowService.performAction(checkDto.getId(), "stopAfterPaid", userData);
                logger.debug("Completed Performing workflow action stalePaid for check id" + checkDto.getId());
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_STALE_PAID_UNSUCCESSFUL, e.getMessage() + " CheckDto Id " + checkDto.getId(), e), e);
                itemsInError++;
            } catch (CallbackException ce) {
                logger.error(Log.event(Event.MARK_STALE_PAID_UNSUCCESSFUL, ce.getMessage() + " CheckDto Id " + checkDto.getId(), ce), ce);
                itemsInError++;
            } catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_STALE_PAID_UNSUCCESSFUL, re.getMessage() + " CheckDto Id " + checkDto.getId(), re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

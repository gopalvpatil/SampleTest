package com.westernalliancebancorp.positivepay.service.impl;

import static ch.lambdaj.Lambda.on;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.StopPresentedService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * User: gduggirala
 * Date: 26/3/14
 * Time: 3:01 PM
 */

@Deprecated
/**
 * This service has been deprecated as a background job as
 * 1. The logic is not right
 * 2. Decided that the moment stop return file parsing service has completed its task "StopReturnService" will be invoked.
 * 3. Please take a look at "StopReturnServiceImpl" which has the correct logic.
 */
@Service
public class StopPresentedServiceImpl implements StopPresentedService {
	
    @Loggable
    Logger logger;

    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckDao checkDao;

    @Autowired
    BatchDao batchDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    ReferenceDataDao referenceDataDao;
    
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    
    @Autowired
    ExceptionTypeDao exceptionTypeDao;

    @Autowired
    ItemTypeDao itemTypeDao;

    @Override
    public Map<String, Integer> markChecksStopByAccounts(List<Account> accounts) {
    	 List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksStopByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksStopByCompany(List<Company> company) {
        List<Long> companyIds = Lambda.extract(company, on(Company.class).getId());
        return markChecksStopByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckStopByBank(List<Bank> banks) {
    	List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksStopByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksStopByCompanyIds(List<Long> companyIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksStopByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStopByBankIds(List<Long> bankIds) {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksStopByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> markChecksStopByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<Long> referenceDataIds = batchDao.findAllStopNotIssuedChecks(accountIds);
        Workflow workflow = workflowManagerFactory.getLatestWorkflow();
        CheckStatus checkStatus = ModelUtils.retrieveOrCreateCheckStatus(workflowManagerFactory.getWorkflowManagerById(workflow.getId()), "start", checkStatusDao);
        if(!referenceDataIds.isEmpty()) {
        	List<ReferenceData> referenceDatas = referenceDataDao.findAllReferenceDataBy(referenceDataIds);       
        	if(!referenceDatas.isEmpty()) {
		        for (ReferenceData referenceData : referenceDatas) {
		            try {
                        logger.debug("Reference Data id "+referenceData.getId());
                        Check check = new Check();
                        check.setAccount(referenceData.getAccount());
                        check.setIssuedAmount(referenceData.getAmount());
                        check.setCheckNumber(referenceData.getCheckNumber());
                        check.setCheckStatus(checkStatus);
                        check.setRoutingNumber(referenceData.getAccount().getBank().getRoutingNumber());
                        check.setWorkflow(workflowManagerFactory.getLatestWorkflow());
                        check.setIssueDate(referenceData.getPaidDate());
                        check.setFileMetaData(referenceData.getFileMetaData());
                        check.setLineNumber(referenceData.getLineNumber());
                        check.setItemType(ModelUtils.getCheckDetailItemType(referenceData.getItemType(), itemTypeDao));
                        check.setDigest(referenceData.getAccount().getNumber()+""+referenceData.getCheckNumber());
                        checkDao.save(check);
                        logger.debug("Corresponding check id "+check.getId()+" for referencedata id: "+referenceData.getId());

		                 Map<String, Object> userData = new HashMap<String, Object>();
		                 CheckDto checkDto = new CheckDto();
		                 checkDto.setReferenceDataId(referenceData.getId());
		                 userData.put(Constants.CHECK_DTO, checkDto);
		                 logger.debug("Started Performing workflow action paidNotIssued for check id"+check.getId());
		                 workflowService.performAction(check.getId(), "stopNotIssued", userData);
                        logger.debug("Completed Performing workflow action paidNotIssued for check id"+check.getId());
                        itemsProcessedSuccessfuly++;
		            } catch (WorkFlowServiceException e) {
		                logger.error(Log.event(Event.MARK_STOPNOTISSUED_UNSUCCESSFUL, e.getMessage(), e), e);
                        itemsInError++;
		            } catch (CallbackException e) {
                        logger.error(Log.event(Event.MARK_STOPNOTISSUED_UNSUCCESSFUL, e.getMessage(), e), e);
                        itemsInError++;
                    }catch (RuntimeException re) {
                        logger.error(Log.event(Event.MARK_PAIDNOTISSUED_UNSUCCESSFUL, re.getMessage() +" ReferenceData Id "+referenceData.getId(), re), re);
                        itemsInError++;
                    }
                }
        	}
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

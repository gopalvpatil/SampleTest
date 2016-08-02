package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.service.MarkStaleService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import static ch.lambdaj.Lambda.on;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/9/14
 * Time: 2:14 PM
 */
@Service
public class MarkStaleServiceImpl implements MarkStaleService {

    @Loggable
    Logger logger;

    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckDao checkDao;

    @Autowired
    BatchDao batchDao;

    @Value("${positivepay.stale.period.in.days}")
    String stalePeriodInDays;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    WorkflowService workflowService;

    private String getStaleStringDate(Long accountId) {
        Account account = accountDao.findById(accountId);
        int staleDays = account.getStaleDays();
        Integer days = new Integer(staleDays);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.CHECK_ISSUE_DATE_SQL_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        String date = simpleDateFormat.format(calendar.getTime());
        return date;
    }

    @Override
    public Map<String, Integer> markChecksStaleByAccounts(List<Account> accounts) throws CallbackException {
        List<Long> accountIds = Lambda.extract(accounts, on(Account.class).getId());
        return markChecksStaleByAccountIds(accountIds);
    }

    @Override
    public Map<String, Integer> markChecksStaleByCompany(List<Company> companies) throws CallbackException {
        List<Long> companyIds = Lambda.extract(companies, on(Company.class).getId());
        return markChecksStaleByCompanyIds(companyIds);
    }

    @Override
    public Map<String, Integer> markCheckStaleByBank(List<Bank> banks) throws CallbackException {
        List<Long> bankIds = Lambda.extract(banks, on(Bank.class).getId());
        return markChecksStaleByBankIds(bankIds);
    }

    @Override
    public Map<String, Integer> markChecksStaleByCompanyIds(List<Long> companyIds) throws CallbackException {
        List<Account> accountsList = accountDao.findAllByCompanyIds(companyIds);
        return markChecksStaleByAccounts(accountsList);
    }

    @Override
    public Map<String, Integer> markChecksStaleByBankIds(List<Long> bankIds) throws CallbackException {
        List<Account> accountsList = accountDao.findAllByCompanyIds(bankIds);
        return markChecksStaleByAccounts(accountsList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Map<String, Integer> markChecksStaleByAccountIds(List<Long> accountIds) {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<CheckStatus> checkStatuses = checkStatusDao.findByName(CheckStatus.ISSUED_STATUS_NAME);
        List<Long> checkStatusIds = Lambda.extract(checkStatuses, on(CheckStatus.class).getId());
        Set<Long> checkIds = new HashSet<Long>();
        for(Long accountId:accountIds) {
            List<Long> accountList = new ArrayList<Long>(1);
            accountList.add(accountId);
            List<Long> checksList = batchDao.findAllStaleChecks(accountList, getStaleStringDate(accountId), checkStatusIds);
            checkIds.addAll(checksList);
        }
        Map<String, Object> userData = new HashMap<String, Object>();
        for (Long checkId : checkIds) {
            logger.debug("Check id " + checkId);
            try {
                logger.debug("Started Performing workflow action stale for check id " + checkId);
                workflowService.performAction(checkId,"stale" ,userData);
                logger.debug("Completed Performing workflow action stale for check id " + checkId);
                itemsProcessedSuccessfuly++;
            } catch (WorkFlowServiceException e) {
                logger.error(Log.event(Event.MARK_STALE_UNSUCCESSFUL, e.getMessage()+ " Check Id " + checkId,e),e);
                itemsInError++;
            } catch (CallbackException e) {
                logger.error(Log.event(Event.MARK_STALE_UNSUCCESSFUL, e.getMessage()+ " Check Id " + checkId,e),e);
                itemsInError++;
            }catch (RuntimeException re) {
                logger.error(Log.event(Event.MARK_STALE_UNSUCCESSFUL, re.getMessage() + " Check Id " + checkId,re), re);
                itemsInError++;
            }
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }
}

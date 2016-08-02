package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.AccountPaymentInfoDto;
import com.westernalliancebancorp.positivepay.dto.CustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.PaymentByDateDto;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.UserDetail;
import com.westernalliancebancorp.positivepay.service.DashboardService;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: gduggirala
 * Date: 3/6/14
 * Time: 7:01 PM
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    BatchDao batchDao;
    @Autowired
    UserDetailDao userDetailDao;

    @Override
    @Transactional(readOnly=true)
    public List<AccountPaymentInfoDto> getLoggedInnUserAllAccountsPaymentsData(List<String> accountNumbers, List<String> checkStatus) {
    	return batchDao.getAllPaymentsAndCountGroupedByCheckStatus(accountNumbers,checkStatus);
    }

    @Override
    public List<PaymentByDateDto> getAllPaymentsByDate(List<String> accountNumbers, Date fromDate, Date toDate) {
        if(fromDate == null) {
            Calendar fromCalendar = Calendar.getInstance();
            fromCalendar = DateUtils.truncate(fromCalendar, Calendar.DATE);
            fromCalendar.add(Calendar.DATE, -6);
            fromDate = fromCalendar.getTime();
        }
        if(toDate == null) {
        	Calendar toCalendar = Calendar.getInstance();
        	toCalendar.add(Calendar.DATE, 1);
        	toCalendar = DateUtils.truncate(toCalendar, Calendar.DATE);
        	toDate = toCalendar.getTime();
        }
        return batchDao.findAllPaymentsMadeBetweenDate(accountNumbers, fromDate, toDate);
    }
    
    @Override
    @Transactional(readOnly=true)
    public List<String> getUsersActiveAccountNumbers(String userName) {
    	UserDetail userDetail = userDetailDao.findByUserName(userName);
    	Set<Account> accounts =  userDetail.getAccounts();
    	List<String> accountNumbers = new ArrayList<String>();
    	for(Account account : accounts) {
    		if(account.isActive()) {
    			accountNumbers.add(account.getNumber());
    		}
    	}
    	return accountNumbers;
    }

}

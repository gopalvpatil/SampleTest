package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.service.ExceptionStatusService;
import com.westernalliancebancorp.positivepay.service.IssuedAfterStopService;
import com.westernalliancebancorp.positivepay.service.IssuedAfterVoidService;
import com.westernalliancebancorp.positivepay.service.StaleVoidService;
import com.westernalliancebancorp.positivepay.service.VoidAfterPaidService;
import com.westernalliancebancorp.positivepay.service.VoidAfterStopService;

/**
 * User:	Moumita Ghosh
 * Date:	May 28, 2014
 * Time:	5:31:50 PM
 */
@Service
public class ExceptionStatusServiceImpl implements ExceptionStatusService {

    @Autowired
    VoidAfterStopService voidAfterStopService;

    @Autowired
    VoidAfterPaidService voidAfterPaidService;

    @Autowired
    IssuedAfterStopService issuedAfterStopService;
    
    @Autowired
    IssuedAfterVoidService issuedAfterVoidService;
    
    @Autowired
    ExceptionStatusDao exceptionStatusDao;
    @Autowired
    StaleVoidService staleVoidService;


    @Override
	 public void processExceptionChecks() {
    	voidAfterStopService.markChecksVoidAfterStop();
    	voidAfterPaidService.markChecksVoidAfterPaid();
    	issuedAfterStopService.markChecksIssuedAfterStop();
    	issuedAfterVoidService.markChecksIssuedAfterVoid();
    	staleVoidService.markChecksStaleVoid();
    }
    
    @Override
    public List<ExceptionStatus> getAllExceptionStatus() {
    	return exceptionStatusDao.findAll();
    }

   
}
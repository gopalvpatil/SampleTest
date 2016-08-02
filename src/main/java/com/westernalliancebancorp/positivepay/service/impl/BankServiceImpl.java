package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.googlecode.ehcache.annotations.When;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.service.BankService;

/**
 * User:	Gopal Patil
 * Date:	Jan 28, 2014
 * Time:	1:07:34 PM
 */

@Service
public class BankServiceImpl implements BankService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	@Autowired
	private BankDao bankDao;
	
	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.BankService#isBankExist(int)
	 */
	@Override
	public boolean isBankExist(int nBankID) throws Exception {		
		Bank bank = bankDao.findById(Long.valueOf(nBankID));		
		if(bank != null) {
			return true;
		} else {		
			return false;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Bank findById(Long id) {
		return bankDao.findById(id);
	}
	
	/* (non-Javadoc)
	 * @see com.westernalliancebancorp.positivepay.service.BankService#findAll()
	 */
	@Override
    @Cacheable(cacheName = "findAllBanks")
	public List<Bank> findAll() {		
		return bankDao.findAll();
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	@TriggersRemove(cacheName = {"findAllBanks", "findByAssignedBankNumber"},when=When.AFTER_METHOD_INVOCATION, removeAll=true)
	public Bank saveOrUpdate(Bank bank) {
		if(bank.getId() == null) {
			bank = bankDao.save(bank);
		}else{
			bank = bankDao.update(bank);
		}
		return bank;
	}

	@Override
	@Transactional(readOnly=true)
    @Cacheable(cacheName = "findByAssignedBankNumber")
	public Bank findByAssignedBankNumber(Short assignedBankNumber) {
		 try{
			 return bankDao.findByAssignedBankNumber(assignedBankNumber);
		 }catch(EmptyResultDataAccessException ex) {
	        	//Ignore error and return null;
	     }
	     return null;
	}
}

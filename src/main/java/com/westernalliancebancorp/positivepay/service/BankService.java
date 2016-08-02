package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.Bank;

/**
 * User:	Gopal Patil
 * Date:	Jan 28, 2014
 * Time:	1:04:18 PM
 */
public interface BankService {
	boolean isBankExist(int nBankID) throws Exception;
	Bank findById(Long id);
	List<Bank> findAll();
	Bank saveOrUpdate(Bank bank);
	Bank findByAssignedBankNumber(Short id);
}

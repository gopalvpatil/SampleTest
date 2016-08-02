package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Bank;

import java.util.List;

/**
 * UserDetail: gduggirala
 * Date: 11/25/13
 * Time: 2:41 PM
 */
public interface BankDao extends GenericDao<Bank, Long> {
    List<Bank> findByParentId(Long parentBankId);
    Bank findByAssignedBankNumber(Short assignedBankNumber);
}

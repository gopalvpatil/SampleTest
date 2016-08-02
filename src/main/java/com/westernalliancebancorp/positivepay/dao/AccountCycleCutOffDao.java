package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.AccountCycleCutOff;

/**
 * @author Gopal Patil
 *
 */
public interface AccountCycleCutOffDao extends GenericDao<AccountCycleCutOff, Long> {
	AccountCycleCutOff fetchAccountCycleInfo(String number);
}

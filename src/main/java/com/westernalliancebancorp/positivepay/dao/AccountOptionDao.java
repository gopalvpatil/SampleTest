package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.AccountServiceOption;

/**
 * @author Gopal Patil
 *
 */
public interface AccountOptionDao extends GenericDao<AccountServiceOption, Long> {
	AccountServiceOption fetchAccountServiceOptionInfo(String number); 
}

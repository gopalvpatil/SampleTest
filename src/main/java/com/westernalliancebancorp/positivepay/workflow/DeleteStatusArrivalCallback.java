package com.westernalliancebancorp.positivepay.workflow;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * User: gduggirala
 * Date: 14/4/14
 * Time: 2:39 PM
 */
@Component("deleteStatusArrivalCallback")
public class DeleteStatusArrivalCallback implements StatusArrivalCallback {
    @Loggable
    Logger logger;
    @Autowired
    CheckDao checkDao;
    @Autowired
    AccountDao accountDao;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executeOnStatusArrival(CallbackContext callbackContext) throws CallbackException {
        Check check = checkDao.findById(callbackContext.getCheck().getId());
        String changedCheckNumber = "-"+check.getCheckNumber();
        Account account = accountDao.findById(check.getAccount().getId());
        /** Before setting the check number, we need to find out if a matching check already exists, if yes add a leading zero in the check number for the existing check**/
        Check matchingCheckFromDB = checkDao.findCheckBy(account.getNumber(), changedCheckNumber);
        if(matchingCheckFromDB != null)
        {
            String checkNumberWithLeadingZero = addPrecedingZero(matchingCheckFromDB.getCheckNumber());
            matchingCheckFromDB.setCheckNumber("-"+checkNumberWithLeadingZero);
            checkDao.update(matchingCheckFromDB);
        }
        check.setDigest(account.getNumber() +""+changedCheckNumber);
        check.setCheckNumber(changedCheckNumber);
        checkDao.update(check);
        return Boolean.TRUE;
    }
    
    private String addPrecedingZero(String checkNumber)
    {
	return Constants.ZERO_CHECK_NUMBER+checkNumber.substring(1);
    }

}

package com.westernalliancebancorp.positivepay.workflow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.utility.common.Constants;

/**
 * This class will be called when a check has departed from the status "Paid" This class will will set the MatchStatus to Unmatched
 * User: Moumita Ghosh
 * Date: 2/6/14
 * Time: 4:15 PM
 */
@Service("paidStatusDepartureCallback")
public class PaidStatusDepartureCallback implements StatusDepartureCallback{
	
    @Autowired
    CheckDao checkDao;
	
    public boolean executeStatusDepartureCallback(CallbackContext callbackContext) throws CallbackException
    {
    	 Check check = checkDao.findById(callbackContext.getCheck().getId());
         check.setMatchStatus(Constants.UNMATCHED);
         checkDao.update(check);
    	 return Boolean.TRUE;
    }
}

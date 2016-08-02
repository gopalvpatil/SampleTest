package com.westernalliancebancorp.positivepay.workflow;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * ChangeAccountNumber is
 *
 * @author Giridhar Duggirala
 */
@Service
@Scope("prototype")
public class ChangeAccountNumber implements PreActionCallback {
    @Autowired
    CheckDao checkDao;

    @Autowired
    AccountDao accountDao;

    @Autowired
    CheckHistoryDao checkHistoryDao;

    @Autowired
    CheckStatusDao checkStatusDao;

    @Autowired
    ActionDao actionDao;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executePreActionCallback(CallbackContext callbackContext) {
        Check check = callbackContext.getCheck();
        Map<String, Object> userData = callbackContext.getUserData();
        String accountNumber = (String)userData.get(WorkflowService.STANDARD_MAP_KEYS.ACCOUNT_NUMBER_NEW.name());
        Long bankId = (Long)userData.get(WorkflowService.STANDARD_MAP_KEYS.BANK_ID.name());
        Account account = accountDao.findByAccountNumberAndBankId(accountNumber, bankId);
        Account previousAccount = accountDao.findById(check.getAccount().getId());
        CheckStatus checkStatus = checkStatusDao.findById(1l);
        CheckHistory checkHistory = new CheckHistory();
        checkHistory.setSystemComment(String.format("Check account has changed from %s to %s ", previousAccount.getNumber(), account.getNumber()));
        checkHistory.setUserComment("None");
        checkHistory.setCheck(check);
        checkHistory.setFormerCheckStatus(checkStatus);
        checkHistory.setIssuedAmount(check.getIssuedAmount());
        checkHistory.setCheckAmount(check.getIssuedAmount()==null?check.getVoidAmount():check.getIssuedAmount());
        BeanUtils.copyProperties(check, checkHistory);
        checkHistory.setId(null);
        checkHistory.setAuditInfo(new AuditInfo());
        Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.CHANGE_ACCOUNT_NUMBER, -1, Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
        checkHistory.setAction(action);
        checkHistory.setMatchStatus(check.getMatchStatus()==null?Constants.UNMATCHED:check.getMatchStatus());
        checkHistoryDao.save(checkHistory);
        return Boolean.TRUE;
    }
}

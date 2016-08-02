package com.westernalliancebancorp.positivepay.workflow;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.AdjustmentCheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckLinkageDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AdjustmentCheck;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.CurrencyUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * User: gduggirala
 * Date: 13/7/14
 * Time: 9:20 PM
 */
@Component("invalidAmountAdjustAmountIssuedOrPaid")
public class InvalidAmountAdjustAmountIssuedOrPaid implements PreActionCallback {
    @Loggable
    Logger logger;
    @Autowired
    WorkflowService workflowService;
    @Autowired
    CheckDao checkDao;
    @Autowired
    CheckStatusDao checkStatusDao;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    WorkflowDao workflowDao;
    @Autowired
    AccountDao accountDao;
    @Autowired
    CheckLinkageDao checkLinkageDao;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    LinkageTypeDao linkageTypeDao;
    @Autowired
    FileDao fileDao;
    @Autowired
    WorkflowUtil workflowUtil;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    AdjustmentCheckDao adjustmentCheckDao;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean executePreActionCallback(CallbackContext callbackContext) throws CallbackException {
        Check check = callbackContext.getCheck();
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into void Paid there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        String actionNameToPerform = callbackContext.getActionNameToPerform();
        LinkageType linkageType =  ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ADJ_AMOUNT, linkageTypeDao);
        if (actionNameToPerform.equals(Action.ACTION_NAME.ADJUST_AMOUNT_ISSUED.getName())) {
            float previousAmount = check.getIssuedAmount().floatValue();
            check.setIssuedAmount(referenceData.getAmount());
            String comment = String.format("Adjust amount issued from $%s to $%s", CurrencyUtils.getWalFormattedCurrency(previousAmount), CurrencyUtils.getWalFormattedCurrency(referenceData.getAmount().floatValue()));
            AdjustmentCheck adjCheck = createAdjustmentCheck(check,linkageType);
            workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.ADJUST_AMOUNT_ISSUED, callbackContext,adjCheck);
        } else if (actionNameToPerform.equals(Action.ACTION_NAME.ADJUST_AMOUNT_PAID.getName())) {
            float previousAmount = referenceData.getAmount().floatValue();
            referenceData.setAmount(check.getIssuedAmount());
            String comment = String.format("Adjust amount paid from $%s to $%s", CurrencyUtils.getWalFormattedCurrency(previousAmount), CurrencyUtils.getWalFormattedCurrency(check.getIssuedAmount().floatValue()));
            AdjustmentCheck adjCheck = createAdjustmentCheck(check,linkageType);
            workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.ADJUST_AMOUNT_PAID, callbackContext,adjCheck);
        }
        else if (actionNameToPerform.equals(Action.ACTION_NAME.ADJUST_AMOUNT_STOP.getName())) {
            float previousAmount = referenceData.getAmount().floatValue();
            referenceData.setAmount(check.getIssuedAmount());
            String comment = String.format("Adjust amount stop from $%s to $%s", CurrencyUtils.getWalFormattedCurrency(previousAmount), CurrencyUtils.getWalFormattedCurrency(check.getIssuedAmount().floatValue()));
            AdjustmentCheck adjCheck = createAdjustmentCheck(check,linkageType);
            workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.ADJUST_AMOUNT_STOP, callbackContext,adjCheck);
        }
        return Boolean.TRUE;
    }
    
    private  AdjustmentCheck createAdjustmentCheck(Check check,LinkageType linkageType)
    {
	AdjustmentCheck adjustmentCheck = new AdjustmentCheck();
        adjustmentCheck.setCheck(check);
        adjustmentCheck.setAmount(check.getIssuedAmount());
        adjustmentCheck.setLinkageType(linkageType);
        adjustmentCheck.setDigest(accountDao.findById(check.getAccount().getId()).getNumber() + "" + check.getCheckNumber());
        AdjustmentCheck adjCheck = adjustmentCheckDao.save(adjustmentCheck);
        return adjCheck;
    }
}

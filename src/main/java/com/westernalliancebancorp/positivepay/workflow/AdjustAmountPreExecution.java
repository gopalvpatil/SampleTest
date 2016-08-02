package com.westernalliancebancorp.positivepay.workflow;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import com.westernalliancebancorp.positivepay.model.*;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
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
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.CurrencyUtils;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * User: gduggirala
 * Date: 1/5/14
 * Time: 4:48 PM
 */
@Component("adjustAmountPreExecution")
public class AdjustAmountPreExecution implements PreActionCallback, PostActionCallback {
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
    public boolean executePostActionCallback(CallbackContext callbackContext) throws CallbackException {
        return Boolean.FALSE;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executePreActionCallback(CallbackContext callbackContext) throws CallbackException {
       Check check = callbackContext.getCheck();
        String checkNumber = "-" + check.getCheckNumber();
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into void Paid there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        if(check.getIssuedAmount()==null) /** Special cases for paidNotIssued where no issuedAmount/voidAmount is present **/
        {
            check.setIssuedAmount(referenceData.getAmount());
            check.setIssueDate(referenceData.getPaidDate());
        }
        //BigDecimal paidAmount = referenceData.getAmount();
        //This will be called when the check states are PaidNotIssued, VoidPaid, StopPaid and the customer has taken the action "No Pay"
        // adjustment amount should be created based on the paid amount.
        //Existing check will be moving into inactive state when the action is completed. So let's not touch that.

        //Get the linkage type Adjustment amount
        LinkageType linkageType =  ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ADJ_AMOUNT, linkageTypeDao);
        String comment = null;
        try {
            //TODO: setup currency conversion.
        	Double amount;        	
        	if(check.getIssuedAmount() != null ) {
        		amount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(check.getIssuedAmount().floatValue()));
        	} else {
        		amount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(check.getVoidAmount().floatValue()));
        	}
            //Double issuedAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(check.getIssuedAmount().floatValue()));
            Double paidAmount= Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceData.getAmount().floatValue()));
            comment = "Adjust amount issued from $"+ NumberFormat.getNumberInstance(Locale.US).format(amount)+ " to $"+NumberFormat.getNumberInstance(Locale.US).format(paidAmount);
            logger.info("Adjustment amount of $" + CurrencyUtils.getWalFormattedCurrency(paidAmount.floatValue()) + " has been created on " + DateUtils.getWALFormatDate(new Date()));
        } catch (ParseException e) {
            throw new CallbackException(e);
        }
        AdjustmentCheck adjustmentCheck = new AdjustmentCheck();
        adjustmentCheck.setCheck(check);
        adjustmentCheck.setAmount(check.getIssuedAmount());
        adjustmentCheck.setLinkageType(linkageType);
        adjustmentCheck.setDigest(accountDao.findById(check.getAccount().getId()).getNumber() + "" + checkNumber);
        AdjustmentCheck adjCheck = adjustmentCheckDao.save(adjustmentCheck);

        CheckHistory checkHistory = workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.NO_PAY, callbackContext,adjCheck);
        //TODO: Adjustment record should be set.

        return Boolean.TRUE;
    }
}

package com.westernalliancebancorp.positivepay.workflow;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.WorkFlowExecutionSequence;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.ActionDao;
import com.westernalliancebancorp.positivepay.dao.AdjustmentCheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckHistoryDao;
import com.westernalliancebancorp.positivepay.dao.CheckLinkageDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.LinkageTypeDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.WorkflowDao;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.AdjustmentCheck;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.LinkageType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.utility.common.CurrencyUtils;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;

/**
 * User: gduggirala
 * Date: 24/4/14
 * Time: 2:26 PM
 */
@Service("adjustAmountNotMatching")
public class AdjustAmountNotMatchingPreExecution implements PreActionCallback, PostActionCallback {

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
    ActionDao actionDao;
    @Autowired
    CheckHistoryDao checkHistoryDao;
    @Autowired
    WorkflowUtil workflowUtil;
    @Autowired
    ExceptionTypeDao exceptionTypeDao;
    @Autowired
    AdjustmentCheckDao adjustmentCheckDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean executePostActionCallback(CallbackContext callbackContext) throws CallbackException {
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    @WorkFlowExecutionSequence
    public boolean executePreActionCallback(CallbackContext callbackContext) throws CallbackException {
        Check check = callbackContext.getCheck();
        if (check.getReferenceData() == null) {
            throw new CallbackException(String.format("Reference Data should not be null, when the check is moved into void Paid there must be reference data set, check id :%d", check.getId()));
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        BigDecimal paidAmount = referenceData.getAmount();
        BigDecimal issuedAmount = check.getIssuedAmount();
        try {
            if (paidAmount.compareTo(issuedAmount) > 0) {
                //Paid amount is greater than issued amount
                handleAdjustmentAmount(check, paidAmount.subtract(issuedAmount), Boolean.TRUE, referenceData, callbackContext);
                ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.PaidAmtExceededException);
                check.setExceptionType(exceptionType);
                check.setExceptionCreationDate(new Date());
                checkDao.update(check);
            } else if (paidAmount.compareTo(issuedAmount) < 0) {
                //Paid amount is less than issued amount
                handleAdjustmentAmount(check, paidAmount.subtract(issuedAmount), Boolean.FALSE, referenceData, callbackContext);
                ExceptionType exceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.IssuedAmtExceededException);
                check.setExceptionType(exceptionType);
                check.setExceptionCreationDate(new Date());
                checkDao.update(check);
            } else {
                //Why am i here? it should not be here amount is matched then it should not be arrive into this state.
                //This case should be handled by AdjustAmountPreExecution
            }
        } catch (WorkFlowServiceException wfse) {
            throw new CallbackException(wfse);
        }
        return false;
    }

    private void handleAdjustmentAmount(Check check, BigDecimal amount, boolean isPaidAmountExceeded, ReferenceData referenceData, CallbackContext callbackContext) throws CallbackException, WorkFlowServiceException {
        String checkNumber = "-" + check.getCheckNumber();
        Account account = accountDao.findById(check.getAccount().getId());
        LinkageType linkageType = null;
        if (isPaidAmountExceeded) {
            linkageType = ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ADJ_PAID_AMOUNT_EXCEEDED, linkageTypeDao);
        } else {
            linkageType = ModelUtils.retrieveOrCreateLinkageType(LinkageType.NAME.ADJ_ISSUED_AMOUNT_EXCEEDED, linkageTypeDao);
        }
        String comment;
        try {
            //TODO: setup currency conversion
            Double issuedAmount = Double.valueOf(CurrencyUtils.getWalFormattedCurrency(check.getIssuedAmount().floatValue()));
            Double paidAmount= Double.valueOf(CurrencyUtils.getWalFormattedCurrency(referenceData.getAmount().floatValue()));
            comment = "Adjust amount issued from $"+ NumberFormat.getNumberInstance(Locale.US).format(issuedAmount)+" to $"+NumberFormat.getNumberInstance(Locale.US).format(paidAmount);
            logger.info("Adjustment amount of $" + CurrencyUtils.getWalFormattedCurrency(amount.floatValue()) + " has been created on " + DateUtils.getWALFormatDate(new Date()));
        } catch (ParseException e) {
            throw new CallbackException(e);
        }
        AdjustmentCheck adjustmentCheck = new AdjustmentCheck();
        adjustmentCheck.setDigest(account.getNumber() + checkNumber);
        adjustmentCheck.setLinkageType(linkageType);
        adjustmentCheck.setAmount(amount);
        adjustmentCheck.setCheck(check);
        AdjustmentCheck adjCheck = adjustmentCheckDao.save(adjustmentCheck);
        workflowUtil.insertNonWorkflowActionIntoHistory(check, comment, Action.ACTION_NAME.ADJUST_AMOUNT, callbackContext,adjCheck);
    }
}

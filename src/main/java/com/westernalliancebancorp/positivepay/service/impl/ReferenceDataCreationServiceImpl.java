package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataCreationService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.text.ParseException;

@Service
public class ReferenceDataCreationServiceImpl implements ReferenceDataCreationService {

    @Loggable
    Logger logger;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckDao checkDao;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    AccountService accountService;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData createNewReferenceDataForCheck(Check check) {
        ReferenceData referenceData = new ReferenceData();
        referenceData.setStatus(ReferenceData.STATUS.PROCESSED);
        referenceData.setAmount(check.getIssuedAmount() == null ? check.getVoidAmount() : check.getIssuedAmount());
        referenceData.setCheckNumber(check.getCheckNumber());
        referenceData.setTraceNumber("N/A");
        referenceData.setLineNumber(check.getLineNumber());
        referenceData.setDigest(check.getAccount().getNumber() + "" + check.getCheckNumber());
        referenceData.setAccount(check.getAccount());
        referenceData.setAssignedBankNumber(check.getAccount().getBank().getAssignedBankNumber());
        referenceData.setFileMetaData(check.getFileMetaData());
        if (check.getCheckStatus().getName().equals(CheckStatus.PAID_STATUS_NAME) || check.getCheckStatus().getName().equals(CheckStatus.STALE_PAID)) {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
            referenceData.setPaidDate(check.getIssueDate() == null ? check.getVoidDate() : check.getIssueDate());
        } else if (check.getCheckStatus().getName().equals(CheckStatus.STOP_STATUS_NAME) || check.getCheckStatus().getName().equals(CheckStatus.STALE_STOP)) {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
            referenceData.setStopDate(check.getIssueDate() == null ? check.getVoidDate() : check.getIssueDate());
        }

        referenceDataDao.save(referenceData);
        return referenceData;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData createReferenceData(ExceptionalReferenceData exceptionalReferenceData, Account account) throws ParseException {
        ReferenceData referenceData = new ReferenceData();
        referenceData.setAssignedBankNumber(exceptionalReferenceData.getAssignedBankNumber());
        referenceData.setAccount(account);
        referenceData.setAmount(new BigDecimal(StringUtils.trim(exceptionalReferenceData.getAmount())));
        referenceData.setCheckNumber(exceptionalReferenceData.getCheckNumber());
        if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.name())) {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
            referenceData.setPaidDate(DateUtils.getCRSPaidFileDateFormat(StringUtils.trim(exceptionalReferenceData.getPaidDate())));
            referenceData.setTraceNumber(StringUtils.trim(exceptionalReferenceData.getTraceNumber()));
        } else if (exceptionalReferenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.toString())) {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
            referenceData.setStopDate(DateUtils.getDailyStopFileDateFormat(StringUtils.trim(exceptionalReferenceData.getStopDate())));
            referenceData.setTraceNumber(exceptionalReferenceData.getTraceNumber());
        } else {
            referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP_PRESENTED);
            referenceData.setStopPresentedDate(DateUtils.getStopPresentedFileDateFormat(StringUtils.trim(exceptionalReferenceData.getStopPresentedDate())));
            referenceData.setTraceNumber(exceptionalReferenceData.getTraceNumber());
            referenceData.setStopPresentedReason(exceptionalReferenceData.getStopPresentedReason());
        }
        referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        referenceData.setDigest(fileUploadUtils.getDigest(account.getNumber(), exceptionalReferenceData.getCheckNumber()));
        // referenceData.setFileMetaData(ModelUtils.retrieveOrCreateExceptionalReferenceDataFile(fileDao, fileTypeDao));
        referenceData.setFileMetaData(exceptionalReferenceData.getFileMetaData());
        referenceData.setLineNumber(exceptionalReferenceData.getLineNumber());
        //Need to set this as, trace number is mandatory in reference data
        if (exceptionalReferenceData.getTraceNumber() == null) {
            referenceData.setTraceNumber("N/A");
        }
        return referenceData;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData, Account account) throws ParseException {
        ExceptionalReferenceData exceptionalReferenceData = new ExceptionalReferenceData();
        exceptionalReferenceData.setCheckNumber(referenceData.getCheckNumber());
        exceptionalReferenceData.setAccountNumber(account.getNumber());
        exceptionalReferenceData.setStopPresentedReason(referenceData.getStopPresentedReason());
        //exceptionalReferenceData.setExceptionType(ExceptionalReferenceData.EXCEPTION_TYPE.DUPLICATE_DATA_IN_DB);
        exceptionalReferenceData.setItemType(referenceData.getItemType());
        exceptionalReferenceData.setLineNumber(referenceData.getLineNumber()+"");
        exceptionalReferenceData.setAmount(referenceData.getAmount() + "");
        exceptionalReferenceData.setAssignedBankNumber(referenceData.getAssignedBankNumber());
        exceptionalReferenceData.setExceptionStatus(ExceptionalReferenceData.EXCEPTION_STATUS.OPEN);
        if (referenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.toString())) {
            exceptionalReferenceData.setPaidDate(DateUtils.getCRSPaidFileStringFormat(referenceData.getPaidDate()));
            exceptionalReferenceData.setTraceNumber(StringUtils.trim(referenceData.getTraceNumber()));
            exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException));
            exceptionalReferenceData.setReferenceData(referenceData);
        } else if (referenceData.getItemType().name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.toString())) {
            exceptionalReferenceData.setStopDate(DateUtils.getDailyStopFileStringFormat(referenceData.getStopDate()));
            exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicateStopItemException));
            exceptionalReferenceData.setReferenceData(referenceData);
        } else {
            exceptionalReferenceData.setStopPresentedDate(StringUtils.trim(DateUtils.getDailyStopFileStringFormat(referenceData.getStopPresentedDate())));
            exceptionalReferenceData.setStopPresentedReason(StringUtils.trim(referenceData.getStopPresentedReason()));
            exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicateStopItemException));
            exceptionalReferenceData.setReferenceData(referenceData);
        }
        exceptionalReferenceData.setFileMetaData(referenceData.getFileMetaData());
        return exceptionalReferenceData;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ReferenceData createReferenceData(ExceptionalReferenceData exceptionalReferenceData) throws ParseException, AccountNotFoundException {
        Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getAssignedBankNumber() + "");
        if (account != null) {
            return createReferenceData(exceptionalReferenceData, account);
        } else
            throw new AccountNotFoundException("Account with number " + exceptionalReferenceData.getAccountNumber() + " and assigned bank number " + exceptionalReferenceData.getAssignedBankNumber() + " is not found or user is not authorized to access.");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData) throws ParseException {
        Account account = accountService.findById(referenceData.getAccount().getId());
        return createExceptionalReferenceData(referenceData, account);
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Bank;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.BankService;
import com.westernalliancebancorp.positivepay.service.ExceptionalReferenceDataService;
import com.westernalliancebancorp.positivepay.service.FiservService;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;

/**
 * User: gduggirala
 * Date: 6/5/14
 * Time: 7:13 PM
 */
@Service
public class FiservServiceImpl implements FiservService {

    @Loggable
    private Logger logger;

    @Autowired
    private CheckDao checkDao;

    @Autowired
    private ReferenceDataDao referenceDataDao;

    @Autowired
    private AccountDao accountDao;

    @Value("${file.serv.server.url}")
    private String hostName;

    @Value("${file.serv.doc.type.check.number}")
    private String checkDocType;

    @Value("${file.serv.zeroed.check.number.value}")
    private String zeroedCheckNumberValue;

    @Autowired
    private BankDao bankDao;

    @Autowired
    private BankService bankService;

    @Autowired 
    ExceptionalReferenceDataDao exceptionalReferenceDataDao;


    @Autowired
    ExceptionalReferenceDataService exceptionalReferenceDataService;

    /**
     * The fields that we require to get the fiServ Url are
     * doc - Document Type Number requested, a constant in our case which is equal to 29
     * rt - FI’s Routing & Transit No.  (9 digits)
     * Acct - Customer Account Number
     * Amt - Amount of item requested
     * Num - Check number
     * Inst - Premier Institution Number
     * Trc - Item Trace Number
     * Date - Item Date (date posted) MM-dd-ccyy
     * Side - This is not part of the URL, but I think we may need it. as per the description in the API i see it as
     * Side/page of check image
     * f=Front
     * b=Rear
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String getFiServUrl(Long  checkId, String side) {
        Check check = checkDao.findById(checkId);
        String returnString = null;
        String hostName = getHostName();
        String docInformation = String.format("doc=%s&", getDocInformation());
        String rt = String.format("rt=%s&", getRoutingAndTransitNumber(check));
        String accountNumber = String.format("acct=%s&", getAccountNumber(check));
        String amt = String.format("amt=%s&", getAmount(check));
        String num = String.format("num=%s&", getCheckNumber(check));
        String inst = String.format("inst=%s&", getInstitutionNumber(check));
        String trc = String.format("trc=%s&", getTraceNumber(check));
        String date = String.format("date=%s&", getDate(check));
        String si = String.format("side=%s&", side);
        returnString = hostName+docInformation+rt+accountNumber+amt+num+inst+trc+date+si;
        logger.info("Final URL that is formed is :: "+returnString);
        return returnString;
    }

    /**
     * This method will only be called for ZeroedCheckNumbers.
     * @param traceNumber
     * @param amount
     * @param accountNumber
     * @param side
     * @return
     */
    @Override
    public String getFiServUrl(String traceNumber, String amount, String accountNumber, String side) {
        ExceptionalReferenceData exceptionalRefereneceData =exceptionalReferenceDataService.findBy(traceNumber, amount, accountNumber);
        String returnString = null;
        String hostName = getHostName();
        String docInformation = String.format("doc=%s&", getDocInformation());
        String rt = String.format("rt=%s&", getRoutingAndTransitNumber(exceptionalRefereneceData));
        String accountNumberStr = String.format("acct=%s&", accountNumber);
        String amt = String.format("amt=%s&", amount);
        String num = String.format("num=%s&", zeroedCheckNumberValue);
        String inst = String.format("inst=%s&", getInstitutionNumber(exceptionalRefereneceData));
        String trc = String.format("trc=%s&", traceNumber);
        String date = String.format("date=%s&", getDate(exceptionalRefereneceData));
        String si = String.format("side=%s&", side);
        returnString = hostName+docInformation+rt+accountNumber+amt+num+inst+trc+date+si;
        logger.info("Final URL that is formed is :: "+returnString);
        return returnString;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String getFiServUrlForExceptionId(Long  exceptionId, String side) {
        ExceptionalReferenceData exceptionalReferenceData = exceptionalReferenceDataDao.findById(exceptionId);
        String returnString = null;
        String hostName = getHostName();
        String docInformation = String.format("doc=%s&", getDocInformation());
        String rt = String.format("rt=%s&", getRoutingAndTransitNumber(exceptionalReferenceData));
        String accountNumber = String.format("acct=%s&", exceptionalReferenceData.getAccountNumber());
        String amt = String.format("amt=%s&", exceptionalReferenceData.getAmount());
        String num = String.format("num=%s&", exceptionalReferenceData.getCheckNumber());
        String inst = String.format("inst=%s&", exceptionalReferenceData.getAssignedBankNumber());
        String trc = String.format("trc=%s&", exceptionalReferenceData.getTraceNumber());
        String date = String.format("date=%s&", getDate(exceptionalReferenceData));
        String si = String.format("side=%s&", side);
        returnString = hostName+docInformation+rt+accountNumber+amt+num+inst+trc+date+si;
        return returnString;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getDate(Check check) {
        if (check.getReferenceData() == null)
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        try {
            logger.info("Reference Data date : "+referenceData.getPaidDate()+" and the formatted one "+DateUtils.getFiservDateFormat(referenceData.getPaidDate()));
            return DateUtils.getFiservDateFormat(referenceData.getPaidDate());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getDate(ExceptionalReferenceData exceptionalReferenceData) {
        try {
            Date crsPaidDateFormat = DateUtils.getCRSPaidFileDateFormat(exceptionalReferenceData.getPaidDate());
            logger.info("CRSPaid format that is received from exceptionalReferenceData.getPaidDate with value " + exceptionalReferenceData.getPaidDate() + " is " + crsPaidDateFormat);
            logger.info("And the fileServ formatDate that is received from the date " + crsPaidDateFormat + " is " + DateUtils.getFiservDateFormat(crsPaidDateFormat));
            return DateUtils.getFiservDateFormat(crsPaidDateFormat);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getTraceNumber(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        return referenceData.getTraceNumber();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private Short getInstitutionNumber(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        Account account = accountDao.findById(referenceData.getAccount().getId());
        Bank bank = bankDao.findById(account.getBank().getId());
        //As per Steve's email: "Institution will be the value stored in bank.assigned_bank_number"
        return bank.getAssignedBankNumber();
    }

    private Short getInstitutionNumber(ExceptionalReferenceData exceptionalReferenceData) {
        Bank bank = bankService.findByAssignedBankNumber(exceptionalReferenceData.getAssignedBankNumber());
        return bank.getAssignedBankNumber();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getCheckNumber(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        return referenceData.getCheckNumber();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getAmount(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        return referenceData.getAmount().toPlainString();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getAccountNumber(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        Account account = accountDao.findById(referenceData.getAccount().getId());
        return account.getNumber();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private String getAccountNumber(ExceptionalReferenceData exceptionalReferenceData) {
        return exceptionalReferenceData.getAccountNumber();
    }

    private String getRoutingAndTransitNumber(Check check) {
        if (check.getReferenceData() == null) {
            throw new RuntimeException("There is no CRS paid record associated with this check, please check!!");
        }
        ReferenceData referenceData = referenceDataDao.findById(check.getReferenceData().getId());
        Account account = accountDao.findById(referenceData.getAccount().getId());
        Bank bank = bankDao.findById(account.getBank().getId());
        return bank.getRoutingNumber();
    }
    
    private String getRoutingAndTransitNumber(ExceptionalReferenceData exceptionalReferenceData) {
        Bank bank = bankService.findByAssignedBankNumber(exceptionalReferenceData.getAssignedBankNumber());
        return bank.getRoutingNumber();
    }

    private String getDocInformation() {
        if (checkDocType == null) {
            throw new RuntimeException("Check doc type cannot be null");
        }
        return checkDocType;
    }

    private String getHostName() {
        String formattedHostName = hostName.endsWith("?") ? hostName : (hostName + "?");
        return formattedHostName;
    }
}

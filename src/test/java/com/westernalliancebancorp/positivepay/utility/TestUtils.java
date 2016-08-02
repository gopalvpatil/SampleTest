package com.westernalliancebancorp.positivepay.utility;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.CompanyDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.ManualEntryService;
import com.westernalliancebancorp.positivepay.service.PaidService;
import com.westernalliancebancorp.positivepay.service.ReferenceDataCreationService;
import com.westernalliancebancorp.positivepay.service.StartStatusService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.WorkflowService;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

/**
 * User: gduggirala
 * Date: 20/6/14
 * Time: 1:53 AM
 */
@Component
public class TestUtils {
    @Loggable
    private Logger logger;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;

    @Autowired
    private ManualEntryService manualEntryService;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private CheckDao checkDao;
    @Autowired
    private CheckStatusDao checkStatusDao;
    @Autowired
    private PaidService paidService;
    @Autowired
    ReferenceDataDao referenceDataDao;
    @Autowired
    CheckService checkService;
    @Autowired
    StartStatusService startStatusService;
    @Autowired
    ExceptionTypeService exceptionTypeService;

    private HttpServletRequest mockHttpServletRequest;
    @Autowired
    private ExceptionalReferenceDataDao exceptionalReferenceDataDao;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private CompanyDao companyDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("jenos");
        PositivePayThreadLocal.setSource(PositivePayThreadLocal.SOURCE.Unknown.name());
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
        mockHttpServletRequest = Mockito.mock(HttpServletRequest.class);
    }



    	public ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData, Account account) throws ParseException {
        ExceptionalReferenceData exceptionalReferenceData = referenceDataCreationService.createExceptionalReferenceData(referenceData, account);
        List<ExceptionalReferenceData> exceptionalReferenceDatas = new ArrayList<ExceptionalReferenceData>();
        exceptionalReferenceDatas.add(exceptionalReferenceData);
        //Save the exceptional reference data.
        exceptionalReferenceDataDao.saveAll(exceptionalReferenceDatas);
        ExceptionType duplicateDataInDbExceptionType = null;
        if (referenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
            duplicateDataInDbExceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException);
        } else {
            duplicateDataInDbExceptionType = exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicateStopItemException);
        }
        exceptionalReferenceDatas = exceptionalReferenceDataDao.findAllExceptionalReferenceDataByExceptionTypeId(duplicateDataInDbExceptionType.getId());
        for (ExceptionalReferenceData exceptionalReferenceData1 : exceptionalReferenceDatas) {
            if (exceptionalReferenceData.getCheckNumber().equals(exceptionalReferenceData1.getCheckNumber()) &&
                    exceptionalReferenceData.getAccountNumber().equals(exceptionalReferenceData1.getAccountNumber())) {
                logger.info("Exceptional reference Data id " + exceptionalReferenceData1.getId());
                return exceptionalReferenceData;
            }
        }
        return null;
    }

    	public ReferenceData createReferenceData(Account account, String checkNumber, String issueCode) throws CallbackException, WorkFlowServiceException {
        CheckDto paidCheckDto = new CheckDto();
        paidCheckDto.setAccountNumber(account.getNumber());
        paidCheckDto.setIssueCode(issueCode);
        paidCheckDto.setPayee("NeverMind");
        paidCheckDto.setIssueDate(new Date());
        paidCheckDto.setCompanyId(account.getCompany().getId());
        paidCheckDto.setCheckNumber(checkNumber);
        paidCheckDto.setIssuedAmount(new BigDecimal(100));
        List<CheckDto> paidCheckDtoList = new ArrayList<CheckDto>();
        paidCheckDtoList.add(paidCheckDto);
        manualEntryService.saveManualEntries(paidCheckDtoList);
        return referenceDataDao.findByCheckNumberAndAccountId(checkNumber, account.getId()).get(0);
    }

    	public Check createCheck(Account account, String checkNumber, String issueCode) throws CallbackException, WorkFlowServiceException {
        CheckDto checkDto = new CheckDto();
        checkDto.setAccountNumber(account.getNumber());
        checkDto.setIssueCode(issueCode);
        checkDto.setPayee("NeverMindTestPayee");
        checkDto.setIssueDate(new Date());
        checkDto.setCompanyId(account.getCompany().getId());
        checkDto.setCheckNumber(checkNumber);
        checkDto.setIssuedAmount(new BigDecimal(100));
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>();
        logger.debug("Check DTO to save " + checkDto.toString());
        checkDtoList.add(checkDto);
        manualEntryService.saveManualEntries(checkDtoList);
        Check check =checkDao.findCheckBy(checkDto.getAccountNumber(), checkDto.getCheckNumber(), checkDto.getIssuedAmount());
        return check;
    }
}

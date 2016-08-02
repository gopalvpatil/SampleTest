package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.*;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * User: gduggirala
 * Date: 20/6/14
 * Time: 1:53 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class CheckWorkflowActionsControllerTest {
    @Loggable
    private Logger logger;
    @Autowired
    ReferenceDataCreationService referenceDataCreationService;

    @Autowired
    private CheckWorkflowActionsController checkWorkflowActionsController;
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
    CheckNonWorkflowActionsController checkNonWorkflowActionsController;
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

    @Test
    public void testDuplicatePaidChangeCheckNumber() throws Exception {
        String changedCheckNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
        ExceptionalReferenceData exceptionalReferenceData = prepareDataForDuplicatePaidAndStopForTesting("P");
        logger.info("Changed check number is "+changedCheckNumber+" Account number is "+exceptionalReferenceData.getAccountNumber());
        checkNonWorkflowActionsController.changeDuplicateReferenceDataCheckNumber(exceptionalReferenceData.getId(), changedCheckNumber, "Changed by Test case");
    }

    @Test
    public void testDuplicatePaidChangeAccountNumber() throws Exception {
        //String changedCheckNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
        Set<Account> accountSet = userService.getAccountByUserDetailId(userDetailDao.findByUserName(PositivePayThreadLocal.get()).getId());
        Account account = null;
        if (accountSet != null && accountSet.size()>1) {
            account = accountSet.toArray(new Account[0])[1];
        }else {
            throw new RuntimeException("Data insufficient more than one account must be there to change to another account we have only one account for user "+PositivePayThreadLocal.get());
        }
        ExceptionalReferenceData exceptionalReferenceData = prepareDataForDuplicatePaidAndStopForTesting("P");
        logger.info("Check number is "+exceptionalReferenceData.getCheckNumber()+" Changed Account number is "+account.getNumber());
        checkNonWorkflowActionsController.changeDuplicateReferenceDataAccountNumber(exceptionalReferenceData.getId(), account.getNumber(), "Changed by Test case");
    }

    @Test
    public void testDuplicateStopChangeCheckNumber() throws Exception {
        String changedCheckNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
        ExceptionalReferenceData exceptionalReferenceData = prepareDataForDuplicatePaidAndStopForTesting("S");
        logger.info("Changed check number is "+changedCheckNumber+" Account number is "+exceptionalReferenceData.getAccountNumber());
        checkNonWorkflowActionsController.changeDuplicateReferenceDataCheckNumber(exceptionalReferenceData.getId(), changedCheckNumber, "Changed by Test case");
    }

    @Test
    public void testDuplicateStopChangeAccountNumber() throws Exception {
        //String changedCheckNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);
        Set<Account> accountSet = userService.getAccountByUserDetailId(userDetailDao.findByUserName(PositivePayThreadLocal.get()).getId());
        Account account = null;
        if (accountSet != null && accountSet.size()>1) {
            account = accountSet.toArray(new Account[0])[1];
        }else {
            throw new RuntimeException("Data insufficient more than one account must be there to change to another account we have only one account for user "+PositivePayThreadLocal.get());
        }
        ExceptionalReferenceData exceptionalReferenceData = prepareDataForDuplicatePaidAndStopForTesting("S");
        logger.info("Check number is "+exceptionalReferenceData.getCheckNumber()+" Changed Account number is "+account.getNumber());
        checkNonWorkflowActionsController.changeDuplicateReferenceDataAccountNumber(exceptionalReferenceData.getId(), account.getNumber(), "Changed by Test case");
    }

    @Test
    public void prepareDataForDuplicatePaid() throws Exception {
        prepareDataForDuplicatePaidAndStopForTesting("P");
    }

    @Test
    public void prepareDataForDuplicateStop() throws Exception{
        prepareDataForDuplicatePaidAndStopForTesting("S");
    }

    //Preparation of all the required data should be below this line
    @Transactional(propagation = Propagation.REQUIRED)
    public ExceptionalReferenceData prepareDataForDuplicatePaidAndStopForTesting(String refrenceDataIssueCode) throws Exception {
        Set<Account> accountSet = userService.getAccountByUserDetailId(userDetailDao.findByUserName(PositivePayThreadLocal.get()).getId());
        Account account = null;
        if (accountSet != null) {
            for (Account tempAccount : accountSet) {
                account = tempAccount;
                break;
            }
        }
        if (account == null) {
            throw new RuntimeException("Cannot create any further data records for check or accunt as user "+PositivePayThreadLocal.get()+" is not associated with any accounts");
        }
        String checkNumber = RandomStringUtils.random(10, Boolean.FALSE, Boolean.TRUE);

        //Create and issued Check
        Check check = createCheck(account, checkNumber, "I", refrenceDataIssueCode);
        List<Check> checkList = new ArrayList<Check>();
        checkList.add(check);

        //Create reference data
        List<ReferenceData> referenceDataList = createReferenceData(account, checkNumber, refrenceDataIssueCode);
        if (referenceDataList == null || referenceDataList.isEmpty()) {
            logger.error("Data creation error, Unable to create reference data record for check number " + checkNumber + " and account number " + account.getNumber());
            throw new RuntimeException("Data creation error, Unable to create reference data record for check number " + checkNumber + " and account number " + account.getNumber());
        }
        ReferenceData referenceData = referenceDataList.get(0);
        //Create exceptional reference data
        ExceptionalReferenceData exceptionalReferenceData = createExceptionalReferenceData(referenceData, account);
        if (exceptionalReferenceData == null) {
            logger.error("Data creation error, Unable to creat eexceptionalReferenceData record for check number " + checkNumber + " and account number " + account.getNumber());
            throw new RuntimeException("Data creation error, Unable to creat eexceptionalReferenceData record for check number " + checkNumber + " and account number " + account.getNumber());
        }
        List<Account> accountList = new ArrayList<Account>();
        accountList.add(account);
        //By this time the check should be in issued status, move it to paid status by taking the action matched and paid.
        paidService.markChecksPaidByAccounts(accountList);

        Company company = companyDao.findById(account.getCompany().getId());
        logger.info(String.format("** Reference Data id is %d check number is %s account number is %s duplicate identifier is %s",
                referenceData.getId(), referenceData.getCheckNumber(), referenceData.getAccount().getNumber(), referenceData.getDigest()));
        logger.info(String.format("** Check id is %d check number is %s account number is %s duplicate_identifier is : %s Company name is : %s",
                check.getId(), check.getCheckNumber(), account.getNumber(), check.getDigest(), company.getName()));
        logger.info(String.format("** Exceptional reference data id is: %d check number is : %s account number is :%s",
                exceptionalReferenceData.getId(), exceptionalReferenceData.getCheckNumber(), exceptionalReferenceData.getAccountNumber()));

        return exceptionalReferenceData;
    }

    private ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData, Account account) throws ParseException {
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

    private List<ReferenceData> createReferenceData(Account account, String checkNumber, String issueCode) throws CallbackException, WorkFlowServiceException {
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
        return referenceDataDao.findByCheckNumberAndAccountId(checkNumber, account.getId());
    }

    private Check createCheck(Account account, String checkNumber, String issueCode,String refrenceDataIssueCode) throws CallbackException, WorkFlowServiceException {
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
        if(refrenceDataIssueCode.equals("P")) {
            check.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicatePaidItemException));
        }else {
            check.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.DuplicateStopItemException));
        }
        check.setExceptionCreationDate(new Date());
        checkDao.update(check);
        return check;
    }
}

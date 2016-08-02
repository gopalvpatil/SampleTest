package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.FileTypeDao;
import com.westernalliancebancorp.positivepay.dto.CheckDto;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.ItemTypeService;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * User: gduggirala
 * Date: 29/5/14
 * Time: 7:11 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
public class ManualEntryControllerTest {
    @Autowired
    ManualEntryController manualEntryController;
    @Autowired
    CheckService checkService;
    @Autowired
    AccountService accountService;
    @Autowired
    ItemTypeService itemTypeService;
    @Autowired
    FileDao fileDao;
    @Autowired
    FileTypeDao fileTypeDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    public void testPostManualEntry() throws CallbackException, WorkFlowServiceException, ParseException {
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>();
        checkDtoList.addAll(createStopCheckDtos(10));
        checkDtoList.addAll(createPaidCheckDtos(10));
        checkDtoList.addAll(createIssuedCheckDtos(10));
        checkDtoList.addAll(createVoidCheckDtos(10));
        manualEntryController.postManualEntryPage(checkDtoList, null);
    }

    @Test
    @Ignore //We have to ignore because some of the bacground tasks are getting triggered and this application is stopping even beore they are stopped
    //So we are not aquring the lock
    public void testIssuedAfterVoidSequenceException() throws Exception{
        //First insert a VoidCheck.
        List<CheckDto> voidCheck = createVoidCheckDtos(1);
        manualEntryController.postManualEntryPage(voidCheck, null);
        Thread.sleep(15000);
        //Now insert an issuedCheck, it should go into issuedAfterVoid
        CheckDto issuedCheckDto = new CheckDto();
        BeanUtils.copyProperties(voidCheck.get(0), issuedCheckDto);
        issuedCheckDto.setIssueCode("I");
        List<CheckDto> issuedCheck = new ArrayList<CheckDto>();
        issuedCheck.add(issuedCheckDto);
        manualEntryController.postManualEntryPage(issuedCheck, null);
    }

    @Test
    @Ignore //We have to ignore because some of the bacground tasks are getting triggered and this application is stopping even beore they are stopped
    //So we are not aquring the lock
    public void testVoidAfterStopSequenceException() throws Exception{
        //First insert a VoidCheck.
        List<CheckDto> stopCheckDtoList = createStopCheckDtos(1);
        manualEntryController.postManualEntryPage(stopCheckDtoList, null);
        Thread.sleep(15000);
        //Now insert an voidCheck, it should go into voidAfterStop
        CheckDto voidCheckDto = new CheckDto();
        BeanUtils.copyProperties(stopCheckDtoList.get(0), voidCheckDto);
        voidCheckDto.setIssueCode("V");
        List<CheckDto> voidCheckDtoList = new ArrayList<CheckDto>();
        voidCheckDtoList.add(voidCheckDto);
        manualEntryController.postManualEntryPage(voidCheckDtoList, null);
        System.out.println("Wait till compelted");
    }

    @Test
    @Ignore //We have to ignore because some of the bacground tasks are getting triggered and this application is stopping even beore they are stopped
    //So we are not aquring the lock
    public void testIssuedAfterStop() throws Exception{
        //First insert a VoidCheck.
        List<CheckDto> stopCheckDtoList = createStopCheckDtos(1);
        manualEntryController.postManualEntryPage(stopCheckDtoList, null);
        Thread.sleep(15000);
        //Now insert an issuedCheck, it should go into issuedAfterStop
        CheckDto issuedCheckDto = new CheckDto();
        BeanUtils.copyProperties(stopCheckDtoList.get(0), issuedCheckDto);
        issuedCheckDto.setIssueCode("I");
        List<CheckDto> issuedCheckDtoList = new ArrayList<CheckDto>();
        issuedCheckDtoList.add(issuedCheckDto);
        manualEntryController.postManualEntryPage(issuedCheckDtoList, null);
        System.out.println("Wait till compelted");
    }

    @Test
    public void testPostManualEntryForIssuedChecks() throws CallbackException, WorkFlowServiceException, ParseException {
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>();
        checkDtoList.addAll(createIssuedCheckDtos(10));
        manualEntryController.postManualEntryPage(checkDtoList, null);
    }

    @Test
    public void testPostManualEntryWithDuplicates() throws CallbackException, WorkFlowServiceException, ParseException {
        List<CheckDto> voidCheckDtos = createVoidCheckDtos(10);
        FileMetaData manualEntryMetaData = ModelUtils.retrieveOrCreateManualEntryFile(fileDao, fileTypeDao);
        List<Check> checksToSave = new ArrayList<Check>();
        //Add the void check's first.
        for(CheckDto fromCheck: voidCheckDtos){
            Check toCheck = new Check();
            BeanUtils.copyProperties(fromCheck, toCheck);
            //Get Account from the account number String
            Account account = accountService.findByAccountNumberAndCompanyId(fromCheck.getAccountNumber(), fromCheck.getCompanyId());
            toCheck.setAccount(account);
            //Set ItemType from issue Code
            toCheck.setItemType(itemTypeService.findByCode(fromCheck.getIssueCode()));
            //Set the digest
            toCheck.setDigest(account.getNumber()+""+fromCheck.getCheckNumber());
            toCheck.setFileMetaData(manualEntryMetaData);
            checksToSave.add(toCheck);
        }
        checkService.saveAll(checksToSave);
        //Then you create Issued check's to see IssuedAfterVoid is working as expected.
        //Make sure that the issued check has the same checknumber and account number as void check's but different item code.
        List<CheckDto> issuedCheckDtosList = createIssuedCheckDtos(voidCheckDtos);
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>();
        checkDtoList.addAll(issuedCheckDtosList);
        manualEntryController.postManualEntryPage(checkDtoList, null);
    }

    private List<CheckDto> createIssuedCheckDtos(List<CheckDto> voidCheckDtos) {
        List<CheckDto> checkDtoList = new ArrayList<CheckDto>(voidCheckDtos.size());
        for(CheckDto voidCheck:voidCheckDtos){
            CheckDto checkDto = new CheckDto();
            BeanUtils.copyProperties(voidCheck,checkDto);
            checkDto.setIssueCode("I");
            checkDtoList.add(checkDto);
        }
        return checkDtoList;
    }

    private List<CheckDto> createStopCheckDtos(int total) {
        List<CheckDto> checkDtos = new ArrayList<CheckDto>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < total; i++) {
            CheckDto checkDto = new CheckDto();
            Date date = Calendar.getInstance().getTime();
            checkDto.setAccountNumber("8010254715");
            checkDto.setIssueCode("S");
            checkDto.setCompanyId(4l);
            checkDto.setCheckNumber(RandomStringUtils.random(5, Boolean.FALSE, Boolean.TRUE)+random.nextInt(100000000));
            checkDto.setIssuedAmount(new BigDecimal(100));
            checkDto.setIssueDate(date);
            checkDto.setPayee("Payee "+i);
            checkDtos.add(checkDto);
        }
        return checkDtos;
    }

    private List<CheckDto> createPaidCheckDtos(int total) {
        List<CheckDto> checkDtos = new ArrayList<CheckDto>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < total; i++) {
            CheckDto checkDto = new CheckDto();
            Date date = Calendar.getInstance().getTime();
            checkDto.setAccountNumber("8010254715");
            checkDto.setIssueCode("P");
            checkDto.setCompanyId(4l);
            checkDto.setCheckNumber(RandomStringUtils.random(5, Boolean.FALSE, Boolean.TRUE)+random.nextInt(100000000));
            checkDto.setIssuedAmount(new BigDecimal(100));
            checkDto.setIssueDate(date);
            checkDto.setPayee("Payee "+i);
            checkDtos.add(checkDto);
        }
        return checkDtos;
    }

    private List<CheckDto> createIssuedCheckDtos(int total) {
        List<CheckDto> checkDtos = new ArrayList<CheckDto>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < total; i++) {
            CheckDto checkDto = new CheckDto();
            Date date = Calendar.getInstance().getTime();
            checkDto.setAccountNumber("8010254715");
            checkDto.setIssueCode("I");
            checkDto.setCompanyId(4l);
            checkDto.setCheckNumber(RandomStringUtils.random(5, Boolean.FALSE, Boolean.TRUE)+random.nextInt(100000000));
            checkDto.setIssuedAmount(new BigDecimal(100));
            checkDto.setIssueDate(date);
            checkDto.setPayee("Payee "+i);
            checkDtos.add(checkDto);
        }
        return checkDtos;
    }

    private List<CheckDto> createVoidCheckDtos(int total) {
        List<CheckDto> checkDtos = new ArrayList<CheckDto>();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < total; i++) {
            CheckDto checkDto = new CheckDto();
            Date date = Calendar.getInstance().getTime();
            checkDto.setAccountNumber("8010254715");
            checkDto.setIssueCode("V");
            checkDto.setCompanyId(4l);
            checkDto.setCheckNumber(RandomStringUtils.random(5, Boolean.FALSE, Boolean.TRUE)+random.nextInt(100000000));
            checkDto.setIssuedAmount(new BigDecimal(100));
            checkDto.setIssueDate(date);
            checkDto.setPayee("Payee "+i);
            checkDtos.add(checkDto);
        }
        return checkDtos;
    }
}

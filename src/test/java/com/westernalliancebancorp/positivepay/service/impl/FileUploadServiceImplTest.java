package com.westernalliancebancorp.positivepay.service.impl;

import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.MultipartFile;

import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 10/4/14
 * Time: 12:01 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class FileUploadServiceImplTest {
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    UserDetailDao userDao;

    @Before
    public void before() {
        PositivePayThreadLocal.set("akumar");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));;
    }

    @Test
    @Ignore
    //TODO: Anand Please correct it.. or set the test case to throw an exception.
    public void testProcessMainframeFile() throws Exception {
        //Process Stop Presented File
    	MultipartFile stopPresentedFile = getStopPresentedFile();
        fileUploadService.processMainframeFile(stopPresentedFile);
        //Process Daily Stop File
        MultipartFile dailyStopFile = getDailyStopFile();
        fileUploadService.processMainframeFile(dailyStopFile);
        //Process crs Paid File.
        MultipartFile crsPaidFile = getCRSPaidFile();
        fileUploadService.processMainframeFile(crsPaidFile);
    }
    
    @Test
    @Ignore
    //TODO: Anand Please correct it.. or set the test case to throw an exception.
    public void testUploadFile() throws Exception {
    	//fileUploadService.uploadFile(getFileToUpload(), new Long(1)) ;
    	fileUploadService.uploadFile(getTXTFileToUpload(), new Long(2)) ;
    	//fileUploadService.uploadFile(getTXTDelimittedFileToUpload(), new Long(3)) ;
    }
    
    private MultipartFile getCSVFileToUpload(){
    	StringBuilder uploadFileContent = new StringBuilder("213233244,825958101679818670,8981,I,4/10/2014,33.67,Duplicate In database\n");
    	/*uploadFileContent.append("213233244,825958101679818670,1001,I,4/5/2014,33.67,Valid Record\n");
    	uploadFileContent.append("213233244,825958101679818672,1001,I,4/5/2014,33.67,Account Number not present\n");
    	uploadFileContent.append("213233244,825958101679818670,1001,I,4/5/2014,33.67,Duplicate Data in file.\n");*/
    	PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        multipartFile.setContents(uploadFileContent.toString().getBytes());
        multipartFile.setFileSize(uploadFileContent.toString().getBytes().length);
        multipartFile.setFileName("Customer Uploaded File.csv");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
    	return multipartFile;
    }
    
    private MultipartFile getTXTFileToUpload(){
    	StringBuilder uploadFileContent = new StringBuilder("122105980825958101679818670000000882712/17/20130000037749I\n");
    	uploadFileContent.append("122105980825958101679818670000000882712/17/20130000037749I\n");
    	uploadFileContent.append("122105980825958101689818670000000882712/19/20130000037749I\n");
    	PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        multipartFile.setContents(uploadFileContent.toString().getBytes());
        multipartFile.setFileSize(uploadFileContent.toString().getBytes().length);
        multipartFile.setFileName("Customer TXt File.txt");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
    	return multipartFile;
    }
    
    private MultipartFile getTXTDelimittedFileToUpload(){
    	StringBuilder uploadFileContent = new StringBuilder("122105980,8010080052,I,35695,12/18/2013,11278.21\n");
    	/*uploadFileContent.append("213233244,825958101679818670,1001,I,4/5/2014,33.67,Valid Record\n");
    	uploadFileContent.append("213233244,825958101679818672,1001,I,4/5/2014,33.67,Account Number not present\n");*/
    	PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        multipartFile.setContents(uploadFileContent.toString().getBytes());
        multipartFile.setFileSize(uploadFileContent.toString().getBytes().length);
        multipartFile.setFileName("Customer delimitted File.txt");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
    	return multipartFile;
    }

    private MultipartFile getStopPresentedFile(){
        PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        StringBuilder stopPresentedFileContent = new StringBuilder("4  825958101679818670      NON-POSTED RETU       80032000787       419340      03/26/14            78.00\n");
        multipartFile.setContents(stopPresentedFileContent.toString().getBytes());
        multipartFile.setFileSize(stopPresentedFileContent.toString().getBytes().length);
        multipartFile.setFileName("STOPRTN002_Test_File.dat");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
        return multipartFile;
    }
    
    private MultipartFile getDailyStopFile(){
        PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        StringBuilder dailyStopFileContent = new StringBuilder("\"Header Information usually ignored\"\n");
        dailyStopFileContent.append("\"4,825958101679818670,1245,1247,23.48,23.48,2014-02-25 00:00:00,1\"\n");
        multipartFile.setContents(dailyStopFileContent.toString().getBytes());
        multipartFile.setFileSize(dailyStopFileContent.toString().getBytes().length);
        multipartFile.setFileName("DailyStopSampleFile.csv");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
        return multipartFile;
    }
    
    private MultipartFile getCRSPaidFile(){
    	PositivePayFtpFile multipartFile = new PositivePayFtpFile();
        StringBuilder crsPaidFileContent = new StringBuilder("4 825958101679818670 00000447.76 0000013197 0080033009671 02262014\n");
        multipartFile.setContents(crsPaidFileContent.toString().getBytes());
        multipartFile.setFileSize(crsPaidFileContent.toString().getBytes().length);
        multipartFile.setFileName("CRSPAIDFile");
        multipartFile.setModifiedDate(new Date());
        multipartFile.setPath(".");
        return multipartFile;
    }
    
    /*private UserDetail createTestUser(){
    	UserDetail user = new UserDetail();
    	user.set
    	user.setActive(Boolean.TRUE);
    	user.setCorporateUserName("junitfileuploaduser");
    	user.setEmail("junitfileuploaduser@test.com");
    	Set<FileMapping> fileMappings = new HashSet<FileMapping>();
    	fileMappings.add(getCSVFileMapping());
    	fileMappings.add(getFixedWidthTextFileMapping());
    	fileMappings.add(getTextDelimittedFileMapping());
    	user.setFileMappings(fileMappings);
    	user.setFirstName("junit");
    	user.setInstitutionId("idtest");
    	user.setLastName("Upload user");
    	user.setLocked(Boolean.FALSE);
    	user.setPassword("password");
    	UserAccountRole userAccountRole = new UserAccountRole();
    	Account account = new Account();
    	account.set
    	userAccountRole.setAccount(account);
    	userAccountRole.setRole(role);
    	user.setUserAccountRoles(userAccountRoles);
    	user.setUserName("junitfileuploaduser");
        return userDao.save(user);
    }
    private FileMapping getCSVFileMapping(){
    	FileMapping fileMappingCSV = new FileMapping();
    	fileMappingCSV.setAccountNumberPosition("2");
    	fileMappingCSV.setCheckAmountPosition("6");
    	fileMappingCSV.setCheckNumberPosition("3");
    	//fileMappingCSV.setDelimiter(new Delimiter());
    	fileMappingCSV.setFileMappingName("CSV");
    	fileMappingCSV.setFileType("CSV");
    	fileMappingCSV.setIssueCodePosition("4");
    	fileMappingCSV.setIssueDatePosition("5");
    	fileMappingCSV.setPayeePosition("7");
    	fileMappingCSV.setRoutingNumberPosition("1");
    	return fileMappingCSV;
    }
    
    private FileMapping getTextDelimittedFileMapping(){
    	Delimiter delimiter = new Delimiter();
    	delimiter.setName("Comma");
    	delimiter.setSymbol(",");
    	delimiter.setValue(",");
    	FileMapping fileMappingTXTDelimitted = new FileMapping();
    	fileMappingTXTDelimitted.setAccountNumberPosition("2");
    	fileMappingTXTDelimitted.setCheckAmountPosition("6");
    	fileMappingTXTDelimitted.setCheckNumberPosition("3");
    	fileMappingTXTDelimitted.setDelimiter(delimiter);
    	fileMappingTXTDelimitted.setFileMappingName("Text Delimitted with comma");
    	fileMappingTXTDelimitted.setFileType("TXT_DELIMITER");
    	fileMappingTXTDelimitted.setIssueCodePosition("4");
    	fileMappingTXTDelimitted.setIssueDatePosition("5");
    	fileMappingTXTDelimitted.setPayeePosition("7");
    	fileMappingTXTDelimitted.setRoutingNumberPosition("1");
    	return fileMappingTXTDelimitted;
    }
    
    private FileMapping getFixedWidthTextFileMapping(){
    	FileMapping fileMappingFixedWidth = new FileMapping();
    	fileMappingFixedWidth.setAccountNumberPosition("10-27");
    	fileMappingFixedWidth.setCheckAmountPosition("47-58");
    	fileMappingFixedWidth.setCheckNumberPosition("28-37");
    	//fileMappingCSV.setDelimiter(new Delimiter());
    	fileMappingFixedWidth.setFileMappingName("TXT Fixed Width");
    	fileMappingFixedWidth.setFileType("TXT_FIXED_WIDTH");
    	fileMappingFixedWidth.setIssueCodePosition("38-38");
    	fileMappingFixedWidth.setIssueDatePosition("39-46");
    	fileMappingFixedWidth.setPayeePosition("59-80");
    	fileMappingFixedWidth.setRoutingNumberPosition("1-9");
    	return fileMappingFixedWidth;
    }*/
}

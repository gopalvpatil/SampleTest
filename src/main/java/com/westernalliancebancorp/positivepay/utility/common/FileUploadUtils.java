package com.westernalliancebancorp.positivepay.utility.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BankDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionStatusDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dao.ItemTypeDao;
import com.westernalliancebancorp.positivepay.dto.LineItemDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.CheckStatus;
import com.westernalliancebancorp.positivepay.model.Delimiter;
import com.westernalliancebancorp.positivepay.model.ExceptionStatus;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.ItemType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.model.Workflow;
import com.westernalliancebancorp.positivepay.service.AccountService;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import com.westernalliancebancorp.positivepay.service.FileMappingService;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * Utility class for File Upload Process
 * @author Anand Kumar
 *
 */
@Component
public class FileUploadUtils {
	
	@Loggable
	private static Logger logger;

	@Autowired
	private UserService userService;
	@Autowired
	private WorkflowManagerFactory workflowManagerFactory;
	@Autowired
	CheckStatusDao checkStatusDao;
	@Autowired
	BatchDao batchDao;
	@Autowired
	BankDao bankDao;
	@Autowired
	AccountDao accountDao;
	@Autowired
	ItemTypeDao itemTypeDao;
	@Value("${file.upload.directory}")
	private String fileUploadDirectory;
    @Autowired
    FileMappingService fileMappingService;
    @Autowired
    private FileDao fileDao;
    @Autowired
    private ExceptionTypeDao exceptionTypeDao;
    @Autowired
    private ExceptionStatusDao exceptionStatusDao;
    @Autowired
    public AccountService accountService;
    @Autowired
    public ExceptionTypeService exceptionTypeService;
    //CRS Paid File Positions
    @Value("${crspaid.file.assigned.bank.number.position}")
    private String crsPaidFileAssignedBankNumberPosition;
    @Value("${crspaid.file.account.number.position}")
    private String crsPaidFileAccountNumberPosition;
    @Value("${crspaid.file.amount.position}")
    private String crsPaidFileAmountPosition;
    @Value("${crspaid.file.check.number.position}")
    private String crsPaidFileCheckNumberPosition;
    @Value("${crspaid.file.trace.number.position}")
    private String crsPaidFileTraceNumberPosition;
    @Value("${crspaid.file.date.paid.position}")
    private String crsPaidFileDatePaidPosition;
    //Daily Stop File positions
    @Value("${dailystop.file.assigned.bank.number.position}")
    private String dailyStopFileAssignedBankNumberPosition;
    @Value("${dailystop.file.account.number.position}")
    private String dailyStopFileAccountNumberPosition;
    @Value("${dailystop.file.from.check.number.position}")
    private String dailyStopFileFromCheckNumberPosition;
    @Value("${dailystop.file.to.check.number.position}")
    private String dailyStopFileToCheckNumberPosition;
    @Value("${dailystop.file.from.amount.position}")
    private String dailyStopFileFromAmountPosition;
    @Value("${dailystop.file.to.amount.position}")
    private String dailyStopFileToAmountPosition;
    @Value("${dailystop.file.stop.item.date.position}")
    private String dailyStopFileStopItemDatePosition;
    @Value("${dailystop.file.item.type.position}")
    private String dailyStopFileItemType;
    @Value("${stoprtn.file.assigned.bank.number.position}")
    private String stoprtnFileAssignedBankNumberPosition;
	@Value("${stoprtn.file.account.number.position}")
	private String stoprtnFileAccountNumberPosition;
	@Value("${stoprtn.file.reason.position}")
	private String stoprtnFileReasonPosition;
	@Value("${stoprtn.file.check.number.position}")
	private String stoprtnFileCheckNumberPosition;
	@Value("${stoprtn.file.transaction.date.position}")
	private String stoprtnFileTransactionDatePostition;
	@Value("${stoprtn.file.check.amount.position}")
	private String stoprtnFileCheckAmountPosition;
	@Value("${stoprtn.file.trace.number.position}")
	private String stoprtnFileTraceNumberPosition;
	private static String VALID_CHECKS = "VALID_CHECKS";
    
	/**
	 * The utility method to process to file uploaded in csv format.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> processCSV(MultipartFile file, FileMapping fileMapping, FileMetaData fileMetaData) throws IOException, Exception {
		//Get the userDetail Info first
		String accountNumberPosition = fileMapping.getAccountNumberPosition();
		String routingNumberPosition = fileMapping.getRoutingNumberPosition();
		String checkAmountPosition = fileMapping.getCheckAmountPosition();
		String checkNumberPosition = fileMapping.getCheckNumberPosition();
		String issueCodePosition = fileMapping.getIssueCodePosition();
		String issueDatePosition = fileMapping.getIssueDatePosition();
		String payeePosition = fileMapping.getPayeePosition();
		
		CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
		List<LineItemDto> lineItems = new ArrayList<LineItemDto>();
		String [] nextLine;
		int lineNumber = 0;
	    while ((nextLine = csvReader.readNext()) != null) {
	    	lineNumber++;
	    	if(nextLine.length > 5){
	    		String issueDate = nextLine[Integer.parseInt(issueDatePosition) - 1];
				String accountNumber = nextLine[Integer.parseInt(accountNumberPosition) - 1];
				String routingNumber = nextLine[Integer.parseInt(routingNumberPosition) - 1];
				String checkAmount = nextLine[Integer.parseInt(checkAmountPosition) - 1];
				String checkNumber = nextLine[Integer.parseInt(checkNumberPosition) - 1];
				String issueCode = nextLine[Integer.parseInt(issueCodePosition) - 1];
				String payee = null;
				String line = Arrays.toString(nextLine);
	    		if(nextLine.length == 7){
	    			payee = nextLine[Integer.parseInt(payeePosition) - 1];
	    		}
				//Make the line item now
		    	LineItemDto lineItem = getLineItemDto(accountNumber, checkAmount, checkNumber, issueCode, issueDate, routingNumber, payee, line, lineNumber);
		    	//Add to the list
		    	lineItems.add(lineItem);
	    	} else {
	    		logger.info("Skipping the blank or incomplete line.");
	    	}
	    }
	    csvReader.close();
	    return processLineItems(lineItems, fileMetaData);
	}
	
	public Map<String, Object> processDelimittedTXTFile(MultipartFile file, FileMapping fileMapping, FileMetaData fileMetaData) throws IOException, Exception {
		//Get the userDetail Info first
		long startTimeForFileMapping = System.currentTimeMillis();
		String accountNumberPosition = fileMapping.getAccountNumberPosition();
		String routingNumberPosition = fileMapping.getRoutingNumberPosition();
		String checkAmountPosition = fileMapping.getCheckAmountPosition();
		String checkNumberPosition = fileMapping.getCheckNumberPosition();
		String issueCodePosition = fileMapping.getIssueCodePosition();
		String issueDatePosition = fileMapping.getIssueDatePosition();
		String payeePosition = fileMapping.getPayeePosition();
		
		long stopTimeForFileMapping = System.currentTimeMillis();
	    long elapsedTimeForFileMapping = stopTimeForFileMapping - startTimeForFileMapping;
	    logger.info("Time taken to to read preferences = {}", elapsedTimeForFileMapping);
		
		long startTimeForFileReading = System.currentTimeMillis();
		Scanner scanner = new Scanner(file.getInputStream());
	    List<LineItemDto> lineItems = new ArrayList<LineItemDto>();
	    int lineNumber = 0;
	    while (scanner.hasNextLine()) {
	    	lineNumber++;
	    	String line = scanner.nextLine();
	    	if(line == null || line.trim().length()<=0){
				logger.info("Blank line so ignoring..");
                continue;
            }
	    	Pattern splitRegex = Pattern.compile(fileMapping.getDelimiter().getValue());
			String[] tokens = splitRegex.split(line);
			int tokenNumber = 0;
			//Make the line item now
	    	LineItemDto lineItem = new LineItemDto();
			for(String token : tokens) {
				tokenNumber++;
				if (Integer.parseInt(accountNumberPosition) == tokenNumber) {
					lineItem.setAccountNumber(StringUtils.trim(token));
				} else if (Integer.parseInt(routingNumberPosition) == tokenNumber) {
					lineItem.setRoutingNumber(StringUtils.trim(token));
				} else if (Integer.parseInt(checkAmountPosition) == tokenNumber) {
					lineItem.setCheckAmount(StringUtils.trim(token));
				} else if (Integer.parseInt(checkNumberPosition) == tokenNumber) {
					lineItem.setCheckNumber(StringUtils.trim(token));
				} else if (Integer.parseInt(issueDatePosition) == tokenNumber) {
					lineItem.setIssueDate(StringUtils.trim(token));
				} else if (Integer.parseInt(issueCodePosition) == tokenNumber) {
					lineItem.setIssueCode(StringUtils.trim(token));
				} else if (payeePosition != null && Integer.parseInt(payeePosition) == tokenNumber) {
					lineItem.setPayee(StringUtils.trim(token));
				}
			}
			lineItem.setLineItem(StringUtils.trim(line));
	    	lineItem.setLineNumber(String.valueOf(lineNumber));
	    	//Add to the list
	    	lineItems.add(lineItem);
	    }
	    scanner.close();
	    long stopTimeForFileReading = System.currentTimeMillis();
	    long elapsedTimeForFileReading = stopTimeForFileReading - startTimeForFileReading;
	    logger.info("Time Taken to read the file {} = {} ms", file.getOriginalFilename(), elapsedTimeForFileReading);
	    return processLineItems(lineItems, fileMetaData);
	}
	
	/**
	 * Method to process the text files.
	 * @param file
	 * @throws Exception
	 */
	public Map<String, Object> processTXTFile(MultipartFile file, FileMapping fileMapping, FileMetaData fileMetaData) throws Exception {
		//Get the userDetail Info first
		long startTimeForPreferences = System.currentTimeMillis();
		String accountNumberPosition = fileMapping.getAccountNumberPosition();
		String routingNumberPosition = fileMapping.getRoutingNumberPosition();
		String checkAmountPosition = fileMapping.getCheckAmountPosition();
		String checkNumberPosition = fileMapping.getCheckNumberPosition();
		String issueCodePosition = fileMapping.getIssueCodePosition();
		String issueDatePosition = fileMapping.getIssueDatePosition();
		String payeePosition = fileMapping.getPayeePosition();
		
		String[] accountNumberPositions = accountNumberPosition.split("-");
		String accountNumberStartPos = accountNumberPositions[0];
		String accountNumberEndPos = accountNumberPositions[1];
		
		String[] routingNumberPositions = routingNumberPosition.split("-");
		String routingNumberStartPos = routingNumberPositions[0];
		String routingNumberEndPos = routingNumberPositions[1];
		
		String[] checkAmountPositions = checkAmountPosition.split("-");
		String checkAmountStartPos = checkAmountPositions[0];
		String checkAmountEndPos = checkAmountPositions[1];
		
		String[] checkNumberPositions = checkNumberPosition.split("-");
		String checkNumberStartPos = checkNumberPositions[0];
		String checkNumberEndPos = checkNumberPositions[1];
		
		String[] issueCodePositions = issueCodePosition.split("-");
		String issueCodeStartPos = issueCodePositions[0];
		String issueCodeEndPos = issueCodePositions[1];
		
		String[] issueDatePositions = issueDatePosition.split("-");
		String issueDateStartPos = issueDatePositions[0];
		String issueDateEndPos = issueDatePositions[1];
		
		String[] payeePositions;
		String payeeStartPos = "";
		//String payeeEndPos = "";
		if (payeePosition != null && !payeePosition.equalsIgnoreCase("-")) {
			payeePositions = payeePosition.split("-");
			payeeStartPos = payeePositions[0];
			//payeeEndPos = payeePositions[1];
		}
		
		long stopTimeForPreferences = System.currentTimeMillis();
	    long elapsedTimeForPreferences = stopTimeForPreferences - startTimeForPreferences;
	    logger.info("Time taken to to read preferences = {}", elapsedTimeForPreferences);
	    
        long startTimeForFileReading = System.currentTimeMillis();
	    Scanner scanner = new Scanner(file.getInputStream());
	    List<LineItemDto> lineItems = new ArrayList<LineItemDto>();
	    int lineNumber = 0;
	    while (scanner.hasNextLine()) {
	    	String line = scanner.nextLine();
	    	lineNumber++;
	    	if(line == null || line.trim().length()<=0){
				logger.info("Blank line so ignoring..");
                continue;
            }
	    	try {
	    		String accountNumber = line.substring(Integer.parseInt(accountNumberStartPos) - 1, Integer.parseInt(accountNumberEndPos));
		    	//logger.debug("accountNumber = "+accountNumber);
		    
		    	String routingNumber = line.substring(Integer.parseInt(routingNumberStartPos) - 1, Integer.parseInt(routingNumberEndPos));
		    	logger.debug("routingNumber = "+routingNumber);
		    	
		    	String checkAmount = line.substring(Integer.parseInt(checkAmountStartPos) - 1, Integer.parseInt(checkAmountEndPos));
		    	logger.debug("checkAmount = "+checkAmount);
		    	
		    	String checkNumber = line.substring(Integer.parseInt(checkNumberStartPos) - 1, Integer.parseInt(checkNumberEndPos));
		    	logger.debug("checkNumber = "+checkNumber);
		    	
		    	String issueDate = line.substring(Integer.parseInt(issueDateStartPos) - 1, Integer.parseInt(issueDateEndPos));
		    	logger.debug("issueDate = "+issueDate);
		    	
		    	String issueCode = line.substring(Integer.parseInt(issueCodeStartPos) - 1, Integer.parseInt(issueCodeEndPos));
		    	logger.debug("issueCode = "+issueCode);
		    	
		    	String payee = null;
		    	if (payeePosition != null && !payeePosition.equalsIgnoreCase("-")) {
			    	payee = line.substring(Integer.parseInt(payeeStartPos) - 1, line.length());
			    	logger.info("payee = "+payee);
		    	}
		    	//Make the line item now
		    	LineItemDto lineItem = getLineItemDto(accountNumber, checkAmount, checkNumber, issueCode, issueDate, routingNumber, payee, line, lineNumber);
		    	//Add to the list
		    	lineItems.add(lineItem);
	    	} catch(Exception exception) {
	    		logger.warn("Line number {} being ignored because it is an invalid record", lineNumber, exception);
	    	}
	    }
	    scanner.close();
	    long stopTimeForFileReading = System.currentTimeMillis();
	    long elapsedTimeForFileReading = stopTimeForFileReading - startTimeForFileReading;
	    logger.info("Time Taken to read the file {} = {} ms", file.getOriginalFilename(), elapsedTimeForFileReading);
	    return processLineItems(lineItems, fileMetaData);
	}

    public byte[] readBytes(String fileName,String path) throws IOException {
        return readBytes(path+File.separator+fileName);
    }

    public byte[] readBytes(String fullPathAndFileName) throws IOException{
        File fileToRead = new File(fullPathAndFileName);
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(fileToRead)));
        byte[] fileData = new byte[(int) fileToRead.length()];
        inputStream.readFully(fileData);
        return fileData;
    }
    
    public byte[] readBytes(InputStream is, long fileSize) throws IOException{
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(is));
        byte[] fileData = new byte[(int) fileSize];
        inputStream.readFully(fileData);
        return fileData;
    }

    public byte[] readUtfBytes(String fullPathAndFileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPathAndFileName), "UTF-16LE"));
        StringBuffer lineBuffer = new StringBuffer();
        String sCurrentLine;
        while ((sCurrentLine = bufferedReader.readLine()) != null) {
            lineBuffer.append(sCurrentLine).append(System.lineSeparator());
        }
        return lineBuffer.toString().getBytes();
    }

    /**
	 * This method saves the file being uploaded to the local disk after reading the disk path from the application.properties
	 * @param file to save in the disk
	 * @param newFileName that the file being saved needs to be renamed to (UUID)
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveFileToDisk(MultipartFile file, String newFileName) throws FileNotFoundException, IOException {
		File directory = new File(fileUploadDirectory);
		File fileToSave = new File(fileUploadDirectory+File.separator+newFileName);
		if (!directory.exists()) {
			logger.info("fileUploadDirectory {} doesn't exist, so creating one..", fileUploadDirectory);
			directory.mkdirs();
		}
		fileToSave.createNewFile();
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileToSave));
        outputStream.write(file.getBytes());
        outputStream.flush();
		outputStream.close();
	}
	
	/**
	 * This method returns the checksum for a given file.
	 * @param file for which the checksum needs to be calculated
	 * @return the checksum of the file
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public String calculateCheckSum(Object file) throws NoSuchAlgorithmException, IOException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		InputStream fileInput = null;
		if (file instanceof MultipartFile) {
			fileInput = ((MultipartFile)file).getInputStream();
		} else if (file instanceof PositivePayFtpFile) {
			fileInput = new ByteArrayInputStream(((PositivePayFtpFile)file).getContents());
		}else {
			fileInput = new FileInputStream((File)file);
		}
		byte[] dataBytes = new byte[1024];

		int bytesRead = 0;

		while ((bytesRead = fileInput.read(dataBytes)) != -1) {
			messageDigest.update(dataBytes, 0, bytesRead);
		}

		byte[] digestBytes = messageDigest.digest();

		StringBuffer filechecksumBuffer = new StringBuffer("");
		
		for (int i = 0; i < digestBytes.length; i++) {
			filechecksumBuffer.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		logger.info("Checksum for the File: " + filechecksumBuffer.toString());
		fileInput.close();
		return filechecksumBuffer.toString();
	}
	
	public String getDigest(String accountNumber, String checkNumber) {
		return accountNumber+""+PPUtils.stripLeadingZeros(checkNumber);
	}
	
	private Map<String, Object> processLineItems(List<LineItemDto> lineItems, FileMetaData fileMetaData) {
		long startTimeForProcessingLineItems = System.currentTimeMillis();
		List<Check> checks = new ArrayList<Check>();
	    List<ExceptionalCheck> duplicatesChecksWithinFile = new ArrayList<ExceptionalCheck>();
	    List<ExceptionalCheck> checksWithNoAccountNumberMatch = new ArrayList<ExceptionalCheck>();
	    List<ExceptionalCheck> checksInWrongDataFormat = new ArrayList<ExceptionalCheck>();
	    List<ExceptionalCheck> checksWithWrongItemCodeList = new ArrayList<ExceptionalCheck>();
	    List<ExceptionalCheck> paidAndStopChecks = new ArrayList<ExceptionalCheck>();
	    //Update the total number of records read
	    fileMetaData.setItemsReceived(new Long(lineItems.size()));
	    fileDao.update(fileMetaData);
	    List<Account> userAccounts = userService.getUserAccounts();
		for(LineItemDto lineItem: lineItems) {
			Check check = new Check();
			ExceptionalCheck exceptionalCheck = new ExceptionalCheck();
			try {
				boolean accountNumberNotFound = false;
		    	boolean duplicateWithinFile = false;
		    	exceptionalCheck = getExceptionalCheckForFileItem(fileMetaData,lineItem.getAccountNumber(), lineItem.getCheckNumber(),
		    				lineItem.getCheckAmount(), lineItem.getIssueCode(), lineItem.getIssueDate(), lineItem.getPayee(), 
		    				lineItem.getRoutingNumber(), lineItem.getLineNumber(), null);
		    	//Check if the item code is not valid
                ItemType itemType = null;
                if (lineItem.getIssueCode() != null && !lineItem.getIssueCode().isEmpty()) {
                	//Ignore Paid and Stop Item Codes right away
                	if(lineItem.getIssueCode().equalsIgnoreCase("S") || lineItem.getIssueCode().equalsIgnoreCase("P")) {
                		logger.info("Line Number {} being ignored because it contains Paid or Stop check {}", lineItem.getLineNumber(), lineItem.getIssueCode());
                        exceptionalCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.PAID_OR_STOP_CHECK_NOT_ALLOWED));
    			    	paidAndStopChecks.add(exceptionalCheck);
    			    	continue;
                	}
                    itemType = itemTypeDao.findByCode(lineItem.getIssueCode());
                }
			    if (itemType == null) {
			    	logger.info("Line Number {} being ignored because it contains an invalid item code {}", lineItem.getLineNumber(), lineItem.getIssueCode());
			    	exceptionalCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.CHECK_IN_WRONG_ITEM_CODE));
			    	checksWithWrongItemCodeList.add(exceptionalCheck);
			    	continue;
			    }
		    	//Get the account Number Model from the accountNumber String and routingNumber
				Account account = accountService.getAccountFromAccountNumberString(lineItem.getAccountNumber(), lineItem.getRoutingNumber(), userAccounts);
				if (account == null) {
					accountNumberNotFound = true;
				} else {
					check.setAccount(account);
					check.setRoutingNumber(lineItem.getRoutingNumber());
					BigDecimal issuedAmount = new BigDecimal (lineItem.getCheckAmount());
					if (lineItem.getCheckAmount().indexOf(".") == -1) {
						issuedAmount = issuedAmount.divide(new BigDecimal(100));
					}
					check.setIssuedAmount(issuedAmount);
					check.setCheckNumber(lineItem.getCheckNumber());
					check.setItemType(itemType);
					check.setIssueDate(DateUtils.getUploadedFileDateFormat(lineItem.getIssueDate()));
					//WALPP-251
					if(lineItem.getIssueCode().equalsIgnoreCase("V")){
						check.setVoidDate(DateUtils.getUploadedFileDateFormat(lineItem.getIssueDate()));
						check.setVoidAmount(issuedAmount);
						check.setIssuedAmount(null);
						check.setIssueDate(null);
					}
					check.setPayee(lineItem.getPayee());
					check.setDigest(getDigest(lineItem.getAccountNumber(), lineItem.getCheckNumber()));
					//check.setLineItem(lineItem.getLineItem());
					check.setLineNumber(lineItem.getLineNumber());
					// Set fileMetaData into check
					check.setFileMetaData(fileMetaData);
					//Set workflow
					Workflow latestWorkFlow = workflowManagerFactory.getLatestWorkflow();
					check.setWorkflow(latestWorkFlow);
					//Set Check Status
					CheckStatus checkStatus = checkStatusDao.findByNameAndVersion("start", latestWorkFlow.getVersion());
					check.setCheckStatus(checkStatus);
					//check if this check is already present in the list
					if (checks.size() > 0) {
						for(Check alreadyAddedCheck: checks) {
							if (alreadyAddedCheck.getAccount().getNumber().equalsIgnoreCase(lineItem.getAccountNumber())
									&& alreadyAddedCheck.getCheckNumber().equalsIgnoreCase(lineItem.getCheckNumber())) {
								duplicateWithinFile = true;
							}
						}
					}
				}					
				if (accountNumberNotFound) {
					logger.info("Line Number {} being ignored because there is a no matching account number in PP database", lineItem.getLineNumber());
					exceptionalCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.ACCOUNT_NOT_FOUND));
					checksWithNoAccountNumberMatch.add(exceptionalCheck);
				} else if (duplicateWithinFile) {
					logger.info("Line Number {} being ignored because there is a matching duplicate in the same file.", lineItem.getLineNumber());
					exceptionalCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_FILE));
					duplicatesChecksWithinFile.add(exceptionalCheck);
				}
				else {
					logger.info("Line Number {} being processed.", lineItem.getLineNumber());
					//Strip leading zeroes for the good checks alone
					check.setCheckNumber(PPUtils.stripLeadingZeros(lineItem.getCheckNumber()));
					checks.add(check);
				}
			} catch(Exception exceptionWhileProcessingLineItem) {
				logger.info("Line Number {} being ignored because the data was found in a wrong format", lineItem.getLineNumber(), exceptionWhileProcessingLineItem );
				exceptionalCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.CHECK_IN_WRONG_DATA_FORMAT));
				checksInWrongDataFormat.add(exceptionalCheck);
			}
		}
		//Find Check Duplicates from the database using digest
		List<Check> validChecks = new ArrayList<Check>();
		List<Check> duplicatesWithinDatabase = new ArrayList<Check>();
		logger.info("Before removing duplicates. There are {} checks", checks.size());
		if (checks.size() > 0) {
			duplicatesWithinDatabase = batchDao.findAllDuplicateChecks(checks);
			logger.info("{} Duplicate checks found in the database", duplicatesWithinDatabase.size());
		    Set<Check> setOfValidChecks = new HashSet<Check>();
		    //Add all the checks
		    setOfValidChecks.addAll(checks);
		    //Remove the duplicates to get the validChecks
		    setOfValidChecks.removeAll(duplicatesWithinDatabase);
		    logger.info("set of valid checks size={}", setOfValidChecks.size());
		    validChecks = new ArrayList<Check>(setOfValidChecks);
		}	    
	    logger.info("Total Number of exception checks = {}", (duplicatesChecksWithinFile.size()+checksWithNoAccountNumberMatch.size()
	    		+duplicatesWithinDatabase.size()+checksWithWrongItemCodeList.size()+checksInWrongDataFormat.size()+paidAndStopChecks.size()));
	    logger.info("Number of duplicates within the file = {}", duplicatesChecksWithinFile.size());
	    logger.info("Number of checks with no account number = {}", checksWithNoAccountNumberMatch.size());
	    logger.info("Number of duplicates within the database = {}", duplicatesWithinDatabase.size());
	    logger.info("Number of checks in wrong data format = {}", checksInWrongDataFormat.size());
	    logger.info("Number of checks with wrong item code = {}", checksWithWrongItemCodeList.size());
	    logger.info("Number of STOP and PAID checks = {}", paidAndStopChecks.size());
        Map<String, Object> checksMap = new HashMap<String, Object>();
        checksMap.put(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_FILE.name(), duplicatesChecksWithinFile);
        checksMap.put(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE.name(), toExceptionalCheckList(fileMetaData, duplicatesWithinDatabase));
        checksMap.put(EXCEPTION_TYPE.ACCOUNT_NOT_FOUND.name(), checksWithNoAccountNumberMatch);
        checksMap.put(EXCEPTION_TYPE.CHECK_IN_WRONG_DATA_FORMAT.name(), checksInWrongDataFormat);
        checksMap.put(EXCEPTION_TYPE.CHECK_IN_WRONG_ITEM_CODE.name(), checksWithWrongItemCodeList);
        checksMap.put(EXCEPTION_TYPE.PAID_OR_STOP_CHECK_NOT_ALLOWED.name(), paidAndStopChecks);
        checksMap.put(VALID_CHECKS, validChecks);
        long stopTimeForProcessingLineItems = System.currentTimeMillis();
	    long elapsedTimeForProcessingLineItems = stopTimeForProcessingLineItems - startTimeForProcessingLineItems;
	    logger.info("Time taken to to read {} records = {}", validChecks.size() + duplicatesChecksWithinFile.size() 
	    		+ duplicatesWithinDatabase.size() + checksWithNoAccountNumberMatch.size() + checksInWrongDataFormat.size() 
	    		+ checksWithWrongItemCodeList.size() + paidAndStopChecks.size(), elapsedTimeForProcessingLineItems);
	    return checksMap;
	}
	
	//This method return null when a matching account if not found in the list of UserAccounts for a given accountNumber and routingNumber
	public Account getAccountFromAccountNumberString(String accountNumber, String routingNumber, List<Account> userAccounts) {
		//Get the account Number Model from the accountNumber String and routingNumber
    	List<Account> matchedAccounts = new ArrayList<Account>();
    	for(Account userAccount: userAccounts) {
    		if (userAccount.getNumber().equalsIgnoreCase(accountNumber)) {
    			matchedAccounts.add(userAccount);
    		}
    	}
    	if (matchedAccounts.size() ==0) {
    		logger.info("account number was not found");
    		return null;
    	} else if (matchedAccounts.size() ==1) {
    		logger.info("One matched account was found");
    		return matchedAccounts.get(0);
    	}else {
    		//More than 1 accounts with the same accountNumber
    		//Match the routing number too to find the correct account number
    		logger.info("More than 1 accounts with the same accountNumber");
    		for(Account matchedAccount: matchedAccounts) {
    			if (matchedAccount.getBank().getRoutingNumber().equalsIgnoreCase(routingNumber)) {
    				return matchedAccount;
    			}
    		}
    	}
    	return null;
	}
	
	/**
	 * This method processes the crsPaidFile and returns a list of ReferenceData to be inserted into the reference data table.
	 * @param crsPaidFile
	 * @param fileMetaData
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> processCRSPaidFile(MultipartFile crsPaidFile, FileMetaData fileMetaData) throws IOException {		
		Scanner scanner = new Scanner(crsPaidFile.getInputStream());
		List<ExceptionalReferenceData> referenceDataWithNoAccountNumberMatchList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> duplicateReferenceDataWithinCRSPaidFileList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> crsPaidItemsInWrongFormatList = new ArrayList<ExceptionalReferenceData>();
        List<ExceptionalReferenceData> zeroNumberedCheckList = new ArrayList<ExceptionalReferenceData>();
		List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			String lineItem = scanner.nextLine();
            lineNumber++;
            if(lineItem == null || lineItem.trim().length()<=0){
				logger.info("Blank line so ignoring..");
                continue;
            }

			//Set fields onto reference data
	    	ReferenceData referenceData = new ReferenceData();
	    	ExceptionalReferenceData exceptionalReferenceData = new ExceptionalReferenceData();
			try {
				//Split by space to get the tokens
				String[] tokens = lineItem.split(Delimiter.SINGLE_SPACE);
				//Now read tokens and initialize them to variables.
				String accountNumber = tokens[Integer.parseInt(crsPaidFileAccountNumberPosition) - 1];
		    	//logger.debug("accountNumber = " + accountNumber);
		    	
		    	String amount = tokens[Integer.parseInt(crsPaidFileAmountPosition) - 1];
		    	logger.debug("amount = " + amount);
		    	
		    	String assignedBankNumber = tokens[Integer.parseInt(crsPaidFileAssignedBankNumberPosition) - 1];
		    	logger.debug("assignedBankNumber = " + assignedBankNumber);
		    	
		    	String checkNumber = tokens[Integer.parseInt(crsPaidFileCheckNumberPosition) - 1];
		    	logger.debug("checkNumber = " + checkNumber);
		    	
		    	String paidDate = tokens[Integer.parseInt(crsPaidFileDatePaidPosition) - 1];
		    	logger.debug("datePaid = " + paidDate);
		    	
		    	String traceNumber = tokens[Integer.parseInt(crsPaidFileTraceNumberPosition) - 1];
		    	logger.debug("traceNumber = " + traceNumber);
	    		boolean accountNumberNotFound = false;
		    	boolean duplicateWithinFile = false;
                boolean isCheckNumberZero=false;
		    	exceptionalReferenceData = getExceptionalReferenceDataForFileItem(accountNumber, checkNumber, amount, paidDate, assignedBankNumber, 
		    			ReferenceData.ITEM_TYPE.PAID, traceNumber, lineNumber, null, null, fileMetaData);
		    	//Get the account Number Model from the accountNumber String and assignedBankNumber
				Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(accountNumber, assignedBankNumber);
				if (account == null) {
					accountNumberNotFound = true;
				} else {
					referenceData = getReferenceDataForFileItem(account, checkNumber, amount, paidDate, assignedBankNumber, 
							fileMetaData, traceNumber, ReferenceData.ITEM_TYPE.PAID.toString(), lineItem, lineNumber, null);
					//duplicate within the same file
                    isCheckNumberZero = (referenceData.getCheckNumber() == null || referenceData.getCheckNumber().isEmpty() || referenceData.getCheckNumber().equals(Constants.ZERO_CHECK_NUMBER));
					duplicateWithinFile = isDuplicateWithinFile(referenceDataList, referenceData);
				}
				if (duplicateWithinFile) {
					logger.info("Line Number {} being ignored because there is a matching duplicate in the same file.", lineNumber);
                    duplicateReferenceDataWithinCRSPaidFileList.add(exceptionalReferenceData);
				} else if (accountNumberNotFound) {
					logger.info("Line Number {} being ignored because there is a no matching account number in PP database", lineNumber);
					//As per Guhn's suggesting taking off AccountNotFound records from ReferenceDataException table.
					exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.ACCOUNT_NOT_FOUND));
					referenceDataWithNoAccountNumberMatchList.add(exceptionalReferenceData);
				} else if(isCheckNumberZero){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.ZERO_NUMBERED_CHECK));
                    zeroNumberedCheckList.add(exceptionalReferenceData);
                } else {
					logger.info("Line Number {} being processed.", lineNumber);
					referenceDataList.add(referenceData);
				}
	    	} catch (Exception exceptionWhileProcessingCRSPaidItem) {
	    		logger.info("Line Number {} being ignored because the data was found in a wrong format", lineNumber, exceptionWhileProcessingCRSPaidItem );
				exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE.PAID_DATA_IN_WRONG_FORMAT));
				crsPaidItemsInWrongFormatList.add(exceptionalReferenceData);
	    	}
		}
		scanner.close();
		Map<String, Object> returnMap = getReferenceDataMap(referenceDataList, duplicateReferenceDataWithinCRSPaidFileList,
	    		referenceDataWithNoAccountNumberMatchList, crsPaidItemsInWrongFormatList);
        returnMap.put(ExceptionalReferenceData.EXCEPTION_TYPE.ZERO_NUMBERED_CHECK.name(), zeroNumberedCheckList);
        return returnMap;
	}
	
	public Map<String, Object> processDailyStopFile(MultipartFile dailyStopFile, FileMetaData fileMetaData) throws IOException {
		CSVReader csvReader = new CSVReader(new InputStreamReader(dailyStopFile.getInputStream()));
		String [] nextLine;
		List<ExceptionalReferenceData> referenceDataWithNoAccountNumberMatchList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> duplicateReferenceDataWithinDailyStopFileList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> dailyStopItemsInWrongFormatList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> dailyStopItemsWithWrongItemTypeList = new ArrayList<ExceptionalReferenceData>();
		List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
		int lineNumber = 0;
	    while ((nextLine = csvReader.readNext()) != null) {
	    	lineNumber++;
	    	if (lineNumber == 1) {
	    		logger.info("File Header being ignored.");
	    		continue;
	    	}
	    	if(nextLine.length < 8){
	    		logger.info("Ignoring the blank or incomplete line");
	    		continue;
	    	}
	    	ReferenceData referenceData = new ReferenceData();
			ExceptionalReferenceData exceptionalReferenceData = new ExceptionalReferenceData();
	    	try {
		    	boolean duplicateWithinFile = false;
		    	//Get the record in the first column and ignore all other columns
				//String[] tokens = nextLine[0].split(Delimiter.COMMA);
				//Now read tokens and initialize them to variables.
				String assignedBankNumber = nextLine[Integer.parseInt(dailyStopFileAssignedBankNumberPosition) - 1];
				String accountNumber = nextLine[Integer.parseInt(dailyStopFileAccountNumberPosition) - 1];
				String fromCheckNumber = (nextLine[Integer.parseInt(dailyStopFileFromCheckNumberPosition) - 1]);
				String toCheckNumber = (nextLine[Integer.parseInt(dailyStopFileToCheckNumberPosition) - 1]);
				String fromAmount = nextLine[Integer.parseInt(dailyStopFileFromAmountPosition) - 1];
				String toAmount = nextLine[Integer.parseInt(dailyStopFileToAmountPosition) - 1];
				String stopItemDate = nextLine[Integer.parseInt(dailyStopFileStopItemDatePosition) - 1];
				String itemType = nextLine[Integer.parseInt(dailyStopFileItemType) - 1];
				String amount = "";
				exceptionalReferenceData = 
						getExceptionalReferenceDataForFileItem(accountNumber, fromCheckNumber, fromAmount, 
								stopItemDate, assignedBankNumber, ReferenceData.ITEM_TYPE.STOP, null, lineNumber, null, null,fileMetaData);
				//Get the account Number Model from the accountNumber String and assignedBankNumber
				Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(accountNumber, assignedBankNumber);
				if (account == null) {
					logger.info("Line Number {} being ignored because there is a no matching account number in PP database", lineNumber);
					referenceDataWithNoAccountNumberMatchList.add(exceptionalReferenceData);
					
				} else {
					if (Integer.parseInt(fromCheckNumber) == Integer.parseInt(toCheckNumber)) {
						if (new BigDecimal(fromAmount).compareTo(new BigDecimal(toAmount)) == 0) {
							amount = fromAmount;
						}
						//incorrect item type
						if (Integer.parseInt(itemType) != 1) {
							logger.info("Line number {} has incorrect item type = {}, so this line is being reported as an error.", lineNumber, itemType);
							dailyStopItemsWithWrongItemTypeList.add(exceptionalReferenceData);
							continue;
						}
						referenceData = getReferenceDataForFileItem(account, fromCheckNumber, amount, stopItemDate, assignedBankNumber, 
								fileMetaData, "N/A", ReferenceData.ITEM_TYPE.STOP.toString(), nextLine[0], lineNumber, null);
						//duplicate within the same file
						duplicateWithinFile = isDuplicateWithinFile(referenceDataList, referenceData);
						if (duplicateWithinFile) {
							logger.info("Line Number {} being ignored because there is a matching duplicate in the same file.", lineNumber);
                            duplicateReferenceDataWithinDailyStopFileList.add(exceptionalReferenceData);
						} else {
							logger.info("Line Number {} being processed.", lineNumber);
							referenceDataList.add(referenceData);
						}
					} else if (Integer.parseInt(fromCheckNumber) <  Integer.parseInt(toCheckNumber)) {
						for (int checkNumber = Integer.parseInt(fromCheckNumber); checkNumber <= Integer.parseInt(toCheckNumber); checkNumber++) {
							//incorrect item type
							if (Integer.parseInt(itemType) != 1) {
								logger.info("Line number {} has incorrect item type = {}, so this line is being reported as an error.");
								dailyStopItemsWithWrongItemTypeList.add(exceptionalReferenceData);
								continue;
							}
							referenceData = getReferenceDataForFileItem(account, String.valueOf(checkNumber), fromAmount, stopItemDate, assignedBankNumber, 
									fileMetaData, "N/A", ReferenceData.ITEM_TYPE.STOP.toString(), nextLine[0], lineNumber, null);
							//duplicate within the same file
							duplicateWithinFile = isDuplicateWithinFile(referenceDataList, referenceData);
							if (duplicateWithinFile) {
								logger.info("Line Number {} being ignored because there is a matching duplicate in the same file.", lineNumber);
								duplicateReferenceDataWithinDailyStopFileList.add(exceptionalReferenceData);
							} else {
								logger.info("Line Number {} being processed.", lineNumber);
								referenceDataList.add(referenceData);
							}
						}
					}
				}	
			}
			catch(Exception exceptionWhileProcessingDailyStopItem) {
				logger.info("Line Number {} being ignored because the data was found in a wrong format", lineNumber, exceptionWhileProcessingDailyStopItem);
	    		dailyStopItemsInWrongFormatList.add(exceptionalReferenceData);
			}
	    }
	    csvReader.close();
	    return getReferenceDataMap(referenceDataList, duplicateReferenceDataWithinDailyStopFileList,
	    		referenceDataWithNoAccountNumberMatchList, dailyStopItemsInWrongFormatList);
	}
	
	public Map<String, Object> processStopPresentedFile(MultipartFile stopPresentedFile, FileMetaData fileMetaData) throws IOException{		
		Scanner scanner = new Scanner(stopPresentedFile.getInputStream());
		List<ExceptionalReferenceData> referenceDataWithNoAccountNumberMatchList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> duplicateReferenceDataWithinCRSPaidFileList = new ArrayList<ExceptionalReferenceData>();
		List<ExceptionalReferenceData> crsPaidItemsInWrongFormatList = new ArrayList<ExceptionalReferenceData>();
		List<ReferenceData> referenceDataList = new ArrayList<ReferenceData>();
		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			String lineItem = scanner.nextLine();
			lineNumber++;
			if(lineItem == null || lineItem.trim().length()<=0){
				logger.info("Blank line so ignoring..");
                continue;
            }
			//Set fields onto reference data
	    	ReferenceData referenceData = new ReferenceData();
	    	ExceptionalReferenceData exceptionalReferenceData = new ExceptionalReferenceData();
	    	try {
				//Split by space to get the tokens
				String[] tokens = lineItem.split(Delimiter.DOUBLE_SPACE);
				List<String> tokensList = new ArrayList<String>(Arrays.asList(tokens));
				tokensList.removeAll(Arrays.asList("", null));
				tokensList.toArray(tokens);
				//Now read tokens and initialize them to variables.
				String accountNumber = tokens[Integer.parseInt(stoprtnFileAccountNumberPosition) - 1];
		    	//logger.debug("accountNumber = " + accountNumber);
		    	
		    	String amount = tokens[Integer.parseInt(stoprtnFileCheckAmountPosition) - 1];
		    	logger.debug("amount = " + amount);
		    	
		    	String assignedBankNumber = tokens[Integer.parseInt(stoprtnFileAssignedBankNumberPosition) - 1];
		    	logger.debug("assignedBankNumber = " + assignedBankNumber);
		    	
		    	String checkNumber = tokens[Integer.parseInt(stoprtnFileCheckNumberPosition) - 1];
		    	logger.debug("checkNumber = " + checkNumber);
		    	
		    	String trnDate = tokens[Integer.parseInt(stoprtnFileTransactionDatePostition) - 1];
		    	logger.debug("trnDate = " + trnDate);
		    	
		    	String stopPresentedReason = tokens[Integer.parseInt(stoprtnFileReasonPosition) - 1];
		    	logger.debug("reason = " + stopPresentedReason);
		    	
		    	String traceNumber = tokens[Integer.parseInt(stoprtnFileTraceNumberPosition) - 1];
		    	logger.debug("traceNumber = " + traceNumber);
	    		boolean accountNumberNotFound = false;
		    	boolean duplicateWithinFile = false;
		    	exceptionalReferenceData = getExceptionalReferenceDataForFileItem(accountNumber, checkNumber, amount, trnDate, assignedBankNumber, 
		    			ReferenceData.ITEM_TYPE.STOP_PRESENTED, traceNumber, lineNumber, null, stopPresentedReason, fileMetaData);
		    	//Get the account Number Model from the accountNumber String and assignedBankNumber
				Account account = accountService.getAccountFromAccountNumberAndAssignedBankNumber(accountNumber, assignedBankNumber);
				if (account == null) {
					accountNumberNotFound = true;
				} else {
					referenceData = getReferenceDataForFileItem(account, checkNumber, amount, trnDate, assignedBankNumber, 
							fileMetaData, traceNumber, ReferenceData.ITEM_TYPE.STOP_PRESENTED.toString(), lineItem, lineNumber, stopPresentedReason);
					//duplicate within the same file
					duplicateWithinFile = isDuplicateWithinFile(referenceDataList, referenceData);
				}
				if (duplicateWithinFile) {
					logger.info("Line Number {} being ignored because there is a matching duplicate in the same file.", lineNumber);
					duplicateReferenceDataWithinCRSPaidFileList.add(exceptionalReferenceData);
				} else if (accountNumberNotFound) {
					logger.info("Line Number {} being ignored because there is a no matching account number in PP database", lineNumber);
					referenceDataWithNoAccountNumberMatchList.add(exceptionalReferenceData);
				} else {
					logger.info("Line Number {} being processed.", lineNumber);
					referenceDataList.add(referenceData);
				}
	    	} catch (Exception exceptionWhileProcessingStopTrnItem) {
	    		logger.info("Line Number {} being ignored because the data was found in a wrong format", lineNumber, exceptionWhileProcessingStopTrnItem);
				crsPaidItemsInWrongFormatList.add(exceptionalReferenceData);
	    	}
		}
		scanner.close();
		return getReferenceDataMap(referenceDataList, duplicateReferenceDataWithinCRSPaidFileList,
	    		referenceDataWithNoAccountNumberMatchList, crsPaidItemsInWrongFormatList);
	}

    private LineItemDto getLineItemDto(String accountNumber, String checkAmount, String checkNumber, String issueCode,
			String issueDate, String routingNumber, String payee, String line, int lineNumber) {
		//Make the line item now
    	LineItemDto lineItem = new LineItemDto();
    	lineItem.setAccountNumber(StringUtils.trim(accountNumber));
    	//remove comma if any
    	if(checkAmount.indexOf(",")!= -1) {
    		checkAmount = checkAmount.replace(",", "");
    	}
    	lineItem.setCheckAmount(StringUtils.trim(checkAmount));
    	lineItem.setCheckNumber(StringUtils.trim(checkNumber));
    	lineItem.setIssueCode(StringUtils.trim(issueCode));
    	lineItem.setIssueDate(StringUtils.trim(issueDate));
    	lineItem.setRoutingNumber(StringUtils.trim(routingNumber));
    	if(payee != null){
    		lineItem.setPayee(StringUtils.trim(payee));
    	}
    	lineItem.setLineItem(StringUtils.trim(line));
    	lineItem.setLineNumber(String.valueOf(lineNumber));
    	return lineItem;
	}
	
	private ExceptionalCheck getExceptionalCheckForFileItem(FileMetaData fileMetaData, String accountNumber, String checkNumber, String checkAmount,
			String issueCode, String issueDate, String payee, String routingNumber, String lineNumber, ExceptionType exceptionType) throws ParseException {
		ExceptionalCheck exceptionalCheck = new ExceptionalCheck();
        if (fileMetaData != null) {
            exceptionalCheck.setFileMetaData(fileMetaData);
        }
        exceptionalCheck.setAccountNumber(StringUtils.trim(accountNumber));
		exceptionalCheck.setIssuedAmount(StringUtils.trim(checkAmount));
		exceptionalCheck.setCheckNumber(StringUtils.trim(checkNumber));
		exceptionalCheck.setIssueCode(StringUtils.trim(issueCode));
		exceptionalCheck.setIssueDate(StringUtils.trim(issueDate));
		exceptionalCheck.setPayee(StringUtils.trim(payee));
		exceptionalCheck.setRoutingNumber(StringUtils.trim(routingNumber));
		//exceptionalCheck.setLineItem(StringUtils.trim(lineItem));
		exceptionalCheck.setLineNumber(StringUtils.trim(lineNumber));
		exceptionalCheck.setExceptionType(exceptionType);
		//Set Exceptional Status
		exceptionalCheck.setExceptionStatus(ModelUtils.createOrRetrieveExceptionStatus(ExceptionStatus.STATUS.OPEN, exceptionStatusDao));
		if (exceptionalCheck.getIssueCode().equalsIgnoreCase("I")) {
			exceptionalCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.ISSUED);
		} else if (exceptionalCheck.getIssueCode().equalsIgnoreCase("V")) {
			exceptionalCheck.setCheckStatus(ExceptionalCheck.CHECK_STATUS.VOID);
		}
		return exceptionalCheck;
	}
	
	public ReferenceData getReferenceDataForFileItem(Account account, String checkNumber, String amount, String itemDate, String assignedBankNumber,
			FileMetaData fileMetaData, String traceNumber, String itemType, String lineItem, int lineNumber, String stopPresentedReason) throws ParseException {
		//If this method is being corrected please make sure that you are correcting the same method in
        //checkService.createReferenceData(ExceptionalReferenceData exceptionalReferenceData, Account account);
        ReferenceData referenceData = new ReferenceData();
		if (account != null)
			referenceData.setAccount(account);
		referenceData.setCheckNumber(PPUtils.stripLeadingZeros(StringUtils.trim(checkNumber)));
		//remove comma if any
    	if(amount.contains(",")) {
    		amount = amount.replace(",", "");
    	}
		referenceData.setAmount(new BigDecimal(StringUtils.trim(amount)));
		referenceData.setAssignedBankNumber(Short.parseShort(assignedBankNumber));
		referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
		referenceData.setFileMetaData(fileMetaData);
		referenceData.setLineNumber(String.valueOf(lineNumber));
		referenceData.setDigest(getDigest(account.getNumber(), checkNumber));
		if (itemType.equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.toString())) {
			referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
			referenceData.setPaidDate(DateUtils.getCRSPaidFileDateFormat(StringUtils.trim(itemDate)));
		}
		else if (itemType.equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.toString())) {
			referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
			referenceData.setStopDate(DateUtils.getDailyStopFileDateFormat(StringUtils.trim(itemDate)));
		} else {
			referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP_PRESENTED);
			referenceData.setStopPresentedDate(DateUtils.getStopPresentedFileDateFormat(StringUtils.trim(itemDate)));
			referenceData.setStopPresentedReason(StringUtils.trim(stopPresentedReason));
		}
        referenceData.setTraceNumber(traceNumber==null?"N/A":traceNumber);
		return referenceData;
	}
	
	private List<ExceptionalCheck> toExceptionalCheckList(FileMetaData fileMetaData, List<Check> checkList) {
		List<ExceptionalCheck> exceptionalCheckList = new ArrayList<ExceptionalCheck>();
		for(Check check: checkList) {
			Date checkDate = check.getIssueDate();
			BigDecimal checkAmount = check.getIssuedAmount();
			if(check.getItemType().getItemCode().equalsIgnoreCase(ItemType.CODE.V.name())){
				checkDate = check.getVoidDate();
				checkAmount= check.getVoidAmount();
			}
			try {
                ItemType itemType = itemTypeDao.findById(check.getItemType().getId());
				ExceptionalCheck exceptionalCheck = getExceptionalCheckForFileItem(fileMetaData,check.getAccount().getNumber(), check.getCheckNumber(),
						String.valueOf(checkAmount), itemType.getItemCode(),
						DateUtils.getStringFromDate(checkDate), check.getPayee(), check.getRoutingNumber(),
						 check.getLineNumber() ,exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE));
				exceptionalCheckList.add(exceptionalCheck);
			}
			catch(Exception exception) {
				logger.warn("Exception while converting referenceData to ExceptionalReferenceData", exception);
			}
		}
		return exceptionalCheckList;
	}
	
	private boolean isDuplicateWithinFile(List<ReferenceData> referenceDataList, ReferenceData referenceData) {
		if (referenceDataList.size() > 0) {
			for(ReferenceData alreadyAddedRefData: referenceDataList) {
				if (alreadyAddedRefData.getAccount().getNumber().equalsIgnoreCase(referenceData.getAccount().getNumber())
						&& alreadyAddedRefData.getCheckNumber().equalsIgnoreCase(String.valueOf(referenceData.getCheckNumber()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	private ExceptionalReferenceData getExceptionalReferenceDataForFileItem(String accountNumber, String checkNumber, String amount, 
			String itemDate, String assignedBankNumber, ReferenceData.ITEM_TYPE itemType, String traceNumber, int lineNumber,
			EXCEPTION_TYPE exceptionType, String stopPresentedReason, FileMetaData fileMetaData) throws ParseException {
		ExceptionalReferenceData exceptionalReferenceData = new ExceptionalReferenceData();
		exceptionalReferenceData.setAccountNumber(StringUtils.trim(accountNumber));
		exceptionalReferenceData.setCheckNumber(StringUtils.trim(checkNumber));
        exceptionalReferenceData.setAmount(StringUtils.trim(amount));
		exceptionalReferenceData.setAssignedBankNumber(Short.parseShort(assignedBankNumber));
		exceptionalReferenceData.setLineNumber(String.valueOf(lineNumber));
		exceptionalReferenceData.setItemType(itemType);
		if (itemType.name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.toString())) {
			exceptionalReferenceData.setPaidDate(StringUtils.trim(itemDate));
			exceptionalReferenceData.setTraceNumber(StringUtils.trim(traceNumber));
		}
		else if (itemType.name().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.toString())) {
			exceptionalReferenceData.setStopDate(StringUtils.trim(itemDate));
		} else {
			exceptionalReferenceData.setStopPresentedDate(StringUtils.trim(itemDate));
			exceptionalReferenceData.setStopPresentedReason(StringUtils.trim(stopPresentedReason));
			exceptionalReferenceData.setTraceNumber(StringUtils.trim(traceNumber));
		}
		//Set exception Status
		exceptionalReferenceData.setExceptionStatus(ExceptionalReferenceData.EXCEPTION_STATUS.OPEN);
        if(exceptionType != null)
		    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(exceptionType));
        exceptionalReferenceData.setFileMetaData(fileMetaData);
		return exceptionalReferenceData;
	}
	
	private List<ExceptionalReferenceData> toExceptionalReferenceDataList(List<ReferenceData> referenceDataList) {
		List<ExceptionalReferenceData> exceptionalReferenceDataList = new ArrayList<ExceptionalReferenceData>();
		for(ReferenceData referenceData: referenceDataList) {
			try {
                String itemDateString = null;
                if (referenceData.getItemType().toString().equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.toString())) {
                    itemDateString =  DateUtils.getCRSPaidFileStringFormat(referenceData.getPaidDate());
                } else if (referenceData.getItemType().toString().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP.toString())){
                    itemDateString =  DateUtils.getDailyStopFileStringFormat(referenceData.getStopDate());
                }else if (referenceData.getItemType().toString().equalsIgnoreCase(ReferenceData.ITEM_TYPE.STOP_PRESENTED.toString())){
                    itemDateString =  DateUtils.getDailyStopFileStringFormat(referenceData.getStopPresentedDate());
                }
				ExceptionalReferenceData exceptionalReferenceData = getExceptionalReferenceDataForFileItem(referenceData.getAccount().getNumber(), 
						referenceData.getCheckNumber(), String.valueOf(referenceData.getAmount()),itemDateString,
						String.valueOf(referenceData.getAssignedBankNumber()), referenceData.getItemType(), referenceData.getTraceNumber(),
                        Integer.parseInt(referenceData.getLineNumber()),
						EXCEPTION_TYPE.DUPLICATE_DATA_IN_DATABASE, referenceData.getStopPresentedReason(), referenceData.getFileMetaData());
                exceptionalReferenceData.setReferenceData(referenceData);
				exceptionalReferenceDataList.add(exceptionalReferenceData);
			} catch(Exception exception) {
				logger.warn("Exception while converting referenceData to ExceptionalReferenceData", exception);
                throw new RuntimeException(exception);
			}
		}
		return exceptionalReferenceDataList;
	}
	
	private Map<String, Object> getReferenceDataMap(List<ReferenceData> referenceDataList, 
			List<ExceptionalReferenceData> duplicatesWithinFile, List<ExceptionalReferenceData> dataWithNoAccountNumberMatchList, List<ExceptionalReferenceData> dataInWrongFormatList) {
		List<ReferenceData> validReferenceData = new ArrayList<ReferenceData>();
		List<ReferenceData> duplicatesWithinDatabase = new ArrayList<ReferenceData>();
		if (referenceDataList.size() > 0) {
			duplicatesWithinDatabase = batchDao.findAllDuplicateReferenceDatas(referenceDataList);
			logger.info("{} Duplicate checks found in the database", duplicatesWithinDatabase.size());
		    Set<ReferenceData> setOfValidReferenceDatas = new HashSet<ReferenceData>();
		    //Add all the checks
            //The referenceData's with different items codes are resolved here. as equals method now considers item_type as well
		    setOfValidReferenceDatas.addAll(referenceDataList);
		    //Remove the duplicates to get the validChecks
		    setOfValidReferenceDatas.removeAll(duplicatesWithinDatabase);
		    logger.info("set of valid checks size={}", setOfValidReferenceDatas.size());
		    validReferenceData = new ArrayList<ReferenceData>(setOfValidReferenceDatas);
		}
	    Map<String, Object> referenceDataMap = new HashMap<String, Object>();
	    referenceDataMap.put(ExceptionalReferenceData.EXCEPTION_TYPE.DUPLICATE_DATA_IN_DB.name(), toExceptionalReferenceDataList(duplicatesWithinDatabase));
	    referenceDataMap.put(ExceptionalReferenceData.EXCEPTION_TYPE.DUPLICATE_DATA_IN_FILE.name(), duplicatesWithinFile);
	    referenceDataMap.put(ExceptionalReferenceData.EXCEPTION_TYPE.ACCOUNT_NOT_FOUND.name(), dataWithNoAccountNumberMatchList);
	    referenceDataMap.put(ExceptionalReferenceData.EXCEPTION_TYPE.DATA_IN_WRONG_FORMAT.name(), dataInWrongFormatList);
	    referenceDataMap.put(VALID_CHECKS, validReferenceData);
	    return referenceDataMap;
	}
}

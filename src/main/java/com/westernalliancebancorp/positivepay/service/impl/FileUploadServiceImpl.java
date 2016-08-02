package com.westernalliancebancorp.positivepay.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import com.westernalliancebancorp.positivepay.dao.*;
import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.service.*;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.westernalliancebancorp.positivepay.dto.FileUploadResponse;
import com.westernalliancebancorp.positivepay.exception.FileFormatNotMatchingWithMappingException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Action;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.ExceptionType.EXCEPTION_TYPE;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.FileType;
import com.westernalliancebancorp.positivepay.model.ReferenceData;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;
import com.westernalliancebancorp.positivepay.utility.common.ModelUtils;
import com.westernalliancebancorp.positivepay.workflow.WorkflowManagerFactory;

/**
 * providing implementation for service methods to work with the FileMetaData.
 * @author Anand Kumar
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    /**
     * The logger object
     */
    @Loggable
    private Logger logger;

    /**
     * The SampleDao dependency
     */
    @Autowired
    private FileDao fileDao;
    @Autowired
    FileMappingDao fileMappingDao;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    WorkflowManagerFactory workflowManagerFactory;
    @Autowired
    CheckStatusDao checkStatusDao;    
	@Autowired
	CheckService checkService;
	@Autowired
	ExceptionalCheckService exceptionalCheckService;
	@Autowired
	ReferenceDataService referenceDataService;
	@Autowired
	ExceptionalReferenceDataService exceptionalReferenceDataService;
	@Autowired
	UserService userService;
	@Autowired
	FileTypeDao fileTypeDao;
    @Autowired
    ExceptionTypeService exceptionTypeService;
    @Autowired
    ReferenceDataDao  referenceDataDao;
    @Autowired
    CheckDao checkDao;
    @Autowired
    ReferenceDataProcessorService referenceDataProcessorService;
    @Autowired
    ActionDao actionDao;

    @Value("${file.upload.directory}")
    private String fileUploadDirectory;
    @Value("${crspaid.file.allowed.names}")
    private String crsPaidFileAllowedNames;
    @Value("${dailystop.file.allowed.names}")
    private String dailyStopFileAllowedNames;
    @Value("${stoprtn.file.allowed.names}")
    private String stopPresentedFileAllowedNames;
    private static String VALID_CHECKS = "VALID_CHECKS";

    @SuppressWarnings("unchecked")
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileUploadResponse uploadFile(MultipartFile fileToProcess, Long fileMappingId) throws Exception {
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        if (isDuplicateFile(fileToProcess)) {
            throw new PositivePayDuplicateFileFoundException("File " + fileToProcess.getName() + " is duplicate please check");
        }
        String newFileName = String.valueOf(UUID.randomUUID()) + "." + FilenameUtils.getExtension(fileToProcess.getOriginalFilename());
        //Step 1 : Save file meta data into DB
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setFileName(newFileName);
        fileMetaData.setFileSize(fileToProcess.getSize());
        fileMetaData.setOriginalFileName(fileToProcess.getOriginalFilename());
        fileMetaData.setStatus(FileMetaData.STATUS.PROCESSED);
        fileMetaData.setUploadDirectory(fileUploadDirectory);
        fileMetaData.setChecksum(fileUploadUtils.calculateCheckSum(fileToProcess));
        //Temporarily save the items receieved as 0
        fileMetaData.setItemsReceived(new Long(0));
        //Set file type
	    fileMetaData.setFileType(ModelUtils.retrieveOrCreateFileType(FileType.FILE_TYPE.CUSTOMER_UPLOAD,fileTypeDao));
        Company userCompany = userService.getLoggedInUserCompany();
		// Get the userDetail preferences first if userDetail has selected it already
		FileMapping fileMapping = fileMappingDao.findByCompanyIdAndFileMappingId(userCompany.getId(), fileMappingId);
        fileMetaData.setFileMapping(fileMapping);
        //Now save the file metadata
        fileMetaData = fileDao.save(fileMetaData);
        //Step 2: Process file now
        String fileName = fileToProcess.getOriginalFilename();
        logger.info("Saved the file : fileName: {} and the UUID of the file is : {}", fileName, newFileName);
        String fileType = fileMapping.getFileType();
        try{
        	if(fileType.equalsIgnoreCase("CSV")) {
            	if((FilenameUtils.getExtension(fileName).toLowerCase()).indexOf("csv") == -1) {
            		throw new FileFormatNotMatchingWithMappingException
            		("Expected a csv file as per the mapping specified but found = "+fileToProcess.getOriginalFilename());
            	} else {
            		Map<String, Object> checksMap = fileUploadUtils.processCSV(fileToProcess, fileMapping, fileMetaData);
                    List<Check> checksToBeInserted = (List<Check>)checksMap.get(VALID_CHECKS);
                    fileUploadResponse = buildFileUploadResponse(checksMap, fileMetaData);
                    if(checksToBeInserted.size() > 0) {
                    	logger.info("{} records being added in check detail", checksToBeInserted.size());
                    	checkService.saveAll(checksToBeInserted);
                    }
                    //Step 3 : insert all exceptional checks
                    insertAllExceptionalChecks(checksMap);
                    //Step 4 : Save file to disk
                    fileUploadUtils.saveFileToDisk(fileToProcess, newFileName);
            	}
            } else if(fileType.equalsIgnoreCase("TXT_DELIMITER")) {
            	if((FilenameUtils.getExtension(fileName).toLowerCase()).indexOf("txt") == -1) {
            		throw new FileFormatNotMatchingWithMappingException
            		("Expected a txt file as per the mapping specified but found = "+fileToProcess.getOriginalFilename());
            	} else {
            		Map<String, Object> checksMap = fileUploadUtils.processDelimittedTXTFile(fileToProcess, fileMapping, fileMetaData);
                    List<Check> checksToBeInserted = (List<Check>)checksMap.get(VALID_CHECKS);
                    fileUploadResponse = buildFileUploadResponse(checksMap, fileMetaData);
                    if(checksToBeInserted.size() > 0) {
                    	checkService.saveAll(checksToBeInserted);
                    }
                    //Step 3 : insert all exceptional checks
                    insertAllExceptionalChecks(checksMap);
                    //Step 4 : Save file to disk
                    fileUploadUtils.saveFileToDisk(fileToProcess, newFileName);
            	}
            } else if(fileType.equalsIgnoreCase("TXT_FIXED_WIDTH")) {
            	if((FilenameUtils.getExtension(fileName).toLowerCase()).indexOf("txt") == -1) {
            		throw new FileFormatNotMatchingWithMappingException
            		("Expected a txt file as per the mapping specified but found = "+fileToProcess.getOriginalFilename());
            	} else {
            		Map<String, Object> checksMap = fileUploadUtils.processTXTFile(fileToProcess, fileMapping, fileMetaData);
                    List<Check> checksToBeInserted = (List<Check>)checksMap.get(VALID_CHECKS);
                    fileUploadResponse = buildFileUploadResponse(checksMap, fileMetaData);
                    if(checksToBeInserted.size() > 0) {
                    	checkService.saveAll(checksToBeInserted);
                    }
                    //Step 3 : insert all exceptional checks
                    insertAllExceptionalChecks(checksMap);
                    //Step 4 : Save file to disk
                    fileUploadUtils.saveFileToDisk(fileToProcess, newFileName);
            	}
            } else {
            	//Unknown file type, just ignore.
            	logger.info("Unknown file type {}, just ignoring", fileToProcess.getOriginalFilename());
            }
        }
        catch(Exception exception) {
        	logger.warn("Could not upload the file, hence deleting the file info that was saved.", exception);
        	fileDao.delete(fileMetaData);
        	throw exception;
        }       
        return fileUploadResponse;
    }
    
    /**
     * This method checks if the file being uploaded is a duplicate file
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public boolean isDuplicateFile(MultipartFile fileToProcess) throws IOException, NoSuchAlgorithmException {
        //String originalFileName = fileToProcess.getOriginalFilename();
        //List<FileMetaData> fileMetaDataList = fileDao.findByChecksum(originalFileName, fileUploadUtils.calculateCheckSum(fileToProcess));
    	List<FileMetaData> fileMetaDataList = fileDao.findByChecksum(fileUploadUtils.calculateCheckSum(fileToProcess));
        if (fileMetaDataList.size() > 0)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
    
    @SuppressWarnings("unchecked")
	private FileUploadResponse buildFileUploadResponse(Map<String, Object> checksMap, FileMetaData fileMetaData) {
    	FileUploadResponse fileUploadResponse = new FileUploadResponse();
    	int noOfchecksWithNoAccountNumberMatch = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.ACCOUNT_NOT_FOUND.name())).size();
    	int noOfduplicatesChecksWithinDatabase = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE.name())).size();
    	int noOfduplicatesChecksWithinFile = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_FILE.name())).size();
    	int noOfChecksInWrongDataFormat = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.CHECK_IN_WRONG_DATA_FORMAT.name())).size();
    	int noOfChecksWithWrongItemCode = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.CHECK_IN_WRONG_ITEM_CODE.name())).size();
    	int noOfPaidAndStopChecks = ((List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.PAID_OR_STOP_CHECK_NOT_ALLOWED.name())).size();
    	int noOfValidChecks = ((List<Check>)checksMap.get(VALID_CHECKS)).size();
    	int totalChecksInFile = noOfchecksWithNoAccountNumberMatch + noOfduplicatesChecksWithinDatabase + noOfduplicatesChecksWithinFile 
    			+noOfChecksInWrongDataFormat + noOfChecksWithWrongItemCode + noOfPaidAndStopChecks + noOfValidChecks;
    	fileUploadResponse.setNoOfchecksWithNoAccountNumberMatch(noOfchecksWithNoAccountNumberMatch);
		fileUploadResponse.setNoOfduplicatesChecksWithinDatabase(noOfduplicatesChecksWithinDatabase);
		fileUploadResponse.setNoOfduplicatesChecksWithinFile(noOfduplicatesChecksWithinFile);
		fileUploadResponse.setNoOfChecksInWrongDataFormat(noOfChecksInWrongDataFormat);
		fileUploadResponse.setNoOfChecksWithWrongItemCode(noOfChecksWithWrongItemCode);
		fileUploadResponse.setNoOfPaidAndStopChecks(noOfPaidAndStopChecks);
		fileUploadResponse.setNoOfValidChecks(noOfValidChecks);
		fileUploadResponse.setTotalChecksInFile(totalChecksInFile);
		return fileUploadResponse;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processMainframeFile(MultipartFile mainFrameFile) throws Exception{
        if (isDuplicateFile(mainFrameFile)) {
            throw new PositivePayDuplicateFileFoundException("File " + mainFrameFile.getName() + " is duplicate. Please check");
        }
        String newFileName = String.valueOf(UUID.randomUUID()) + "." + FilenameUtils.getExtension(mainFrameFile.getOriginalFilename());
        //Step 1 : Save file meta data into DB
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setFileName(newFileName);
        fileMetaData.setFileSize(mainFrameFile.getSize());
        fileMetaData.setOriginalFileName(mainFrameFile.getOriginalFilename());
        fileMetaData.setStatus(FileMetaData.STATUS.PROCESSED);
        fileMetaData.setUploadDirectory(fileUploadDirectory);
        fileMetaData.setItemsReceived(new Long(0));
        fileMetaData.setChecksum(fileUploadUtils.calculateCheckSum(mainFrameFile));
        //Set file type
        if(isDailyStopFile(mainFrameFile.getOriginalFilename())) {
        	fileMetaData.setFileType(ModelUtils.retrieveOrCreateFileType(FileType.FILE_TYPE.DAILY_STOP,fileTypeDao));
        } else if (isCRSPaidFile(mainFrameFile.getOriginalFilename())) {
        	fileMetaData.setFileType(ModelUtils.retrieveOrCreateFileType(FileType.FILE_TYPE.CRS_PAID,fileTypeDao));
        } else if (isStopPresentedFile(mainFrameFile.getOriginalFilename())) {
        	fileMetaData.setFileType(ModelUtils.retrieveOrCreateFileType(FileType.FILE_TYPE.STOP_PRESENTED,fileTypeDao));
        } else{
        	//Unknown file type, just ignore.
        	logger.info("Unknown file type {}, just ignoring", mainFrameFile.getOriginalFilename());
        	return;
        }
        //Now save the file metadata
        fileMetaData = fileDao.save(fileMetaData);
        try{
        	if(isDailyStopFile(mainFrameFile.getOriginalFilename())) {
            	//This is a Daily STOP File
            	Map<String, Object> referenceDataMap = fileUploadUtils.processDailyStopFile(mainFrameFile, fileMetaData);
                List<ReferenceData> referenceDataListToBeAdded = (List<ReferenceData>)referenceDataMap.get(VALID_CHECKS);
            	if(referenceDataListToBeAdded.size() > 0) {
            		logger.info("{} records being added in reference data", referenceDataListToBeAdded.size());
            		referenceDataService.saveAll(referenceDataListToBeAdded);
            	}
            	//insert all exceptional reference data
            	insertAllExceptionalReferenceData(referenceDataMap);
            	//save file to disk finally
            	fileUploadUtils.saveFileToDisk(mainFrameFile, newFileName);
            } else if (isCRSPaidFile(mainFrameFile.getOriginalFilename())) {
            	//CRS Paid file
            	Map<String, Object> referenceDataMap = fileUploadUtils.processCRSPaidFile(mainFrameFile, fileMetaData);
            	List<ReferenceData> referenceDataListToBeAdded = (List<ReferenceData>)referenceDataMap.get(VALID_CHECKS);
            	if(referenceDataListToBeAdded.size() > 0) {
            		logger.info("{} records being added in reference data", referenceDataListToBeAdded.size());
            		referenceDataService.saveAll(referenceDataListToBeAdded);
            	}
            	//insert all exceptional reference data
            	insertAllExceptionalReferenceData(referenceDataMap);
            	//save file to disk finally
            	fileUploadUtils.saveFileToDisk(mainFrameFile, newFileName);
            } else if (isStopPresentedFile(mainFrameFile.getOriginalFilename())) {
            	//Stop Presented File
            	Map<String, Object> referenceDataMap = fileUploadUtils.processStopPresentedFile(mainFrameFile, fileMetaData);
            	List<ReferenceData> referenceDataListToBeAdded = (List<ReferenceData>)referenceDataMap.get(VALID_CHECKS);
            	if(referenceDataListToBeAdded.size() > 0) {
            		logger.info("{} records being added in reference data", referenceDataListToBeAdded.size());
            		referenceDataService.saveAll(referenceDataListToBeAdded);
            	}
            	//insert all exceptional reference data
            	insertAllExceptionalReferenceData(referenceDataMap);
            	//save file to disk finally
            	fileUploadUtils.saveFileToDisk(mainFrameFile, newFileName);
            }
            else {
            	//Unknown file type, just ignore.
            	logger.info("Unknown file type {}, just ignoring", mainFrameFile.getOriginalFilename());
            }
        }catch(Exception exception) {
        	logger.warn("Could not process the file, hence deleting the file info that was saved.", exception);
        	fileDao.delete(fileMetaData);
        	throw exception;
        }
    }
    
    private boolean isDailyStopFile(String originalFileName) {
    	String[] allowedFileNames = dailyStopFileAllowedNames.split(",");
    	logger.info("originalFileName= {} and allowedFileNames= {}", originalFileName, allowedFileNames);
    	for(String allowedFileName: allowedFileNames) {
    		if((originalFileName.toUpperCase()).contains(allowedFileName.toUpperCase())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean isCRSPaidFile(String originalFileName) {
    	String[] allowedFileNames = crsPaidFileAllowedNames.split(",");
    	logger.info("originalFileName= {} and allowedFileNames= {}", originalFileName, allowedFileNames);
    	for(String allowedFileName: allowedFileNames) {
    		if((originalFileName.toUpperCase()).contains(allowedFileName.toUpperCase())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean isStopPresentedFile(String originalFileName) {
    	String[] allowedFileNames = stopPresentedFileAllowedNames.split(",");
    	logger.info("originalFileName= {} and allowedFileNames= {}", originalFileName, allowedFileNames);
    	for(String allowedFileName: allowedFileNames) {
    		if((originalFileName.toUpperCase()).contains(allowedFileName.toUpperCase())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    @SuppressWarnings("unchecked")
	private void insertAllExceptionalReferenceData(Map<String, Object> referenceDataMap) {
    	List<ExceptionalReferenceData> referenceDataWithNoAccountNumberMatchList = (List<ExceptionalReferenceData>)referenceDataMap.get(ExceptionalReferenceData.EXCEPTION_TYPE.ACCOUNT_NOT_FOUND.toString());
    	List<ExceptionalReferenceData> referenceDataInWrongFormatList = (List<ExceptionalReferenceData>)referenceDataMap.get(ExceptionalReferenceData.EXCEPTION_TYPE.DATA_IN_WRONG_FORMAT.toString());
    	List<ExceptionalReferenceData> duplicateReferenceDataWithinFileList = (List<ExceptionalReferenceData>)referenceDataMap.get(ExceptionalReferenceData.EXCEPTION_TYPE.DUPLICATE_DATA_IN_FILE.toString());
    	List<ExceptionalReferenceData> duplicatesWithinDatabaseList = (List<ExceptionalReferenceData>)referenceDataMap.get(ExceptionalReferenceData.EXCEPTION_TYPE.DUPLICATE_DATA_IN_DB.toString());
        List<ExceptionalReferenceData> zeroNumberedCheckList = (List<ExceptionalReferenceData>)referenceDataMap.get(ExceptionalReferenceData.EXCEPTION_TYPE.ZERO_NUMBERED_CHECK.toString());

        List<ExceptionalReferenceData> allExceptionalReferenceDataList = new ArrayList<ExceptionalReferenceData>();
    	if(referenceDataWithNoAccountNumberMatchList.size() > 0) {
            for(ExceptionalReferenceData exceptionalReferenceData:referenceDataWithNoAccountNumberMatchList){
                if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.PAID_ACCOUNT_NOT_FOUND));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.STOP_ACCOUNT_NOT_FOUND));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.STOP_PRESENTED_ACCOUNT_NOT_FOUND));
                }
                allExceptionalReferenceDataList.add(exceptionalReferenceData);
            }
    	}
    	if(referenceDataInWrongFormatList.size() > 0) {
            for(ExceptionalReferenceData exceptionalReferenceData:referenceDataInWrongFormatList){
                if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.PAID_DATA_IN_WRONG_FORMAT));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.STOP_DATA_IN_WRONG_FORMAT));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.STOP_PRESENTED_DATA_IN_WRONG_FORMAT));
                }
                allExceptionalReferenceDataList.add(exceptionalReferenceData);
            }
    	}
    	if(duplicateReferenceDataWithinFileList.size() > 0) {
            for(ExceptionalReferenceData exceptionalReferenceData:duplicateReferenceDataWithinFileList){
                if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_PAID_ITEM_IN_FILE));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_STOP_ITEM_IN_FILE));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DUPLICATE_STOP_PRESENTED_ITEM_IN_FILE));
                }
                allExceptionalReferenceDataList.add(exceptionalReferenceData);
            }
    	}
    	if(duplicatesWithinDatabaseList.size() > 0) {
            for(ExceptionalReferenceData exceptionalReferenceData:duplicatesWithinDatabaseList){
                if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicatePaidItemException));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicateStopException));
                }else if(exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)){
                    exceptionalReferenceData.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicatePresentedItemException));
                }
                allExceptionalReferenceDataList.add(exceptionalReferenceData);
            }
    	}
        if(zeroNumberedCheckList != null && zeroNumberedCheckList.size()>0){
            allExceptionalReferenceDataList.addAll(zeroNumberedCheckList);
        }
    	if(allExceptionalReferenceDataList.size() > 0) {
    		exceptionalReferenceDataService.saveAll(allExceptionalReferenceDataList);
            processDuplicatesInDbToMarkCheckExceptionType(duplicatesWithinDatabaseList);
    	} else{
    		logger.info("No Exceptional Reference Data found in this file.");
    	}
    }

    private void processDuplicatesInDbToMarkCheckExceptionType(List<ExceptionalReferenceData> duplicatesWithinDatabaseList) {
        if(duplicatesWithinDatabaseList == null || duplicatesWithinDatabaseList.isEmpty()){
            return;
        }
        for (ExceptionalReferenceData exceptionalReferenceData : duplicatesWithinDatabaseList) {
        	List <String> digestList = new ArrayList<String> ();
        	Check existingCheck = null;
        	String digest = fileUploadUtils.getDigest(exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getCheckNumber());
        	digestList.add(digest);
            List<Check> existingChecks = checkDao.finalAllChecksByDigest(digestList);
            if(existingChecks != null && existingChecks.size()>0)
            {
            	existingCheck = existingChecks.get(0);
            }
            if (existingCheck != null) {
        	Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_CREATED, existingCheck.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
                if (exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
                    existingCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicatePaidItemException));
                    existingCheck.setExceptionCreationDate(new Date());
                    checkService.addHistoryEntryForDuplicateChecks(existingCheck, "DuplicatePaidException on Check "+existingCheck.getCheckNumber(), action);
                } else if (exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP) || exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)) {
                    existingCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicateStopItemException));
                    existingCheck.setExceptionCreationDate(new Date());
                    checkService.addHistoryEntryForDuplicateChecks(existingCheck, "DuplicateStopException on Check "+existingCheck.getCheckNumber(), action);
                }
                checkDao.update(existingCheck);
            } else {
                List<ReferenceData> referenceDataList = referenceDataDao.findByCheckNumberAccountNumberAndItemType(exceptionalReferenceData.getCheckNumber(),
                        exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getItemType());
                if (referenceDataList != null && referenceDataList.size() == 1) {
                    ReferenceData referenceData = referenceDataList.get(0);
                    try{
                    referenceDataProcessorService.processNonDuplicateReferenceData(referenceData);
                    Check newCheck = checkDao.finalAllChecksByDigest(digestList).get(0);
                    if (newCheck != null) {
                	Action action = ModelUtils.createOrRetrieveAction(Action.ACTION_NAME.DUPLICATE_STOP_PAID_CREATED, newCheck.getCheckStatus().getVersion(), Action.ACTION_TYPE.NON_WORK_FLOW_ACTION, actionDao);
                    	if (exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.PAID)) {
                    		newCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicatePaidItemException));
                            newCheck.setExceptionCreationDate(new Date());
                    		checkService.addHistoryEntryForDuplicateChecks(newCheck, "DuplicatePaidException on Check "+newCheck.getCheckNumber(), action);
                        } else if (exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP) || exceptionalReferenceData.getItemType().equals(ReferenceData.ITEM_TYPE.STOP_PRESENTED)) {
                        	newCheck.setExceptionType(exceptionTypeService.createOrRetrieveExceptionType(EXCEPTION_TYPE.DuplicateStopItemException));
                            newCheck.setExceptionCreationDate(new Date());
                        	checkService.addHistoryEntryForDuplicateChecks(newCheck, "DuplicateStopException on Check "+newCheck.getCheckNumber(), action);
                        }
                    }
                    checkDao.update(newCheck);
                    }catch (CallbackException e) {
                        logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, e.getMessage() + " ReferenceData Id " + referenceData, e), e);
                        e.printStackTrace();
                    } catch (WorkFlowServiceException e) {
                        logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, e.getMessage() + " ReferenceData Id " + referenceData, e), e);
                        e.printStackTrace();
                    } catch (RuntimeException re) {
                        logger.error(Log.event(Event.REFERENCE_DATA_PROCESSOR_UNSUCCESSFUL, re.getMessage() + " ReferenceData Id " + referenceData, re), re);
                    }
                }  else if (referenceDataList == null || referenceDataList.isEmpty()) {
                    logger.error(String.format("Functionality error: It is an impossible case we should have a reference data record with the check number '%s' and account number '%s' and item code type '%s' else this should not be an exceptional case the fileMetaDataId is " + exceptionalReferenceData.getFileMetaData().getId(),
                            exceptionalReferenceData.getCheckNumber(), exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getItemType().name()));
                }else if (referenceDataList.size() > 0) {
                    logger.error(String.format("Functionality error cannot have more than once referenceRecord with the same account number '%s', check number '%s' and itemType '%s' ",
                            exceptionalReferenceData.getAccountNumber(), exceptionalReferenceData.getCheckNumber(), exceptionalReferenceData.getItemType().name()));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
	private void insertAllExceptionalChecks(Map<String, Object> checksMap) {
    	List<ExceptionalCheck> checkWithNoAccountNumberMatchList = (List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.ACCOUNT_NOT_FOUND.name());
    	List<ExceptionalCheck> checkInWrongFormatList = (List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.CHECK_IN_WRONG_DATA_FORMAT.name());
    	List<ExceptionalCheck> duplicatesWithinFileList = (List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_FILE.name());
    	List<ExceptionalCheck> duplicatesWithinDatabaseList = (List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.DUPLICATE_CHECK_IN_DATABASE.name());
    	List<ExceptionalCheck> checksWithWrongItemCodeList = (List<ExceptionalCheck>)checksMap.get(EXCEPTION_TYPE.CHECK_IN_WRONG_ITEM_CODE.name());
    	List<ExceptionalCheck> allExceptionalCheckList = new ArrayList<ExceptionalCheck>();
    	if(checkWithNoAccountNumberMatchList.size() > 0) {
    		allExceptionalCheckList.addAll(checkWithNoAccountNumberMatchList);
    	}
    	if(checkInWrongFormatList.size() > 0) {
    		allExceptionalCheckList.addAll(checkInWrongFormatList);
    	}
    	if(duplicatesWithinFileList.size() > 0) {
    		allExceptionalCheckList.addAll(duplicatesWithinFileList);
    	}
    	if(duplicatesWithinDatabaseList.size() > 0) {
    		allExceptionalCheckList.addAll(duplicatesWithinDatabaseList);
    	}
    	if(checksWithWrongItemCodeList.size() > 0) {
    		allExceptionalCheckList.addAll(checksWithWrongItemCodeList);
    	}
    	if(allExceptionalCheckList.size() > 0) {
    		exceptionalCheckService.saveAll(allExceptionalCheckList);
    	} else{
    		logger.info("No Exceptional Checks found in this file.");
    	}
    }

    @Override
    public PositivePayFtpFile downloadFile(String uid) throws IOException {
        PositivePayFtpFile positivePayFtpFile = new PositivePayFtpFile();
        FileMetaData fileMetaData = fileDao.findByFileName(uid);
        positivePayFtpFile.setFileSize(fileMetaData.getFileSize());
        positivePayFtpFile.setFileName(fileMetaData.getOriginalFileName());
        positivePayFtpFile.setPath(fileMetaData.getUploadDirectory());
        positivePayFtpFile.setFileSize(fileMetaData.getFileSize());
        positivePayFtpFile.setContents(fileUploadUtils.readBytes(uid, fileMetaData.getUploadDirectory()));
        return positivePayFtpFile;
    }
}

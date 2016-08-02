package com.westernalliancebancorp.positivepay.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import com.westernalliancebancorp.positivepay.annotation.PositivePaySecurity;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dto.FileUploadResponse;
import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.exception.FileFormatNotMatchingWithMappingException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.model.FileUploadForm;
import com.westernalliancebancorp.positivepay.model.Permission;
import com.westernalliancebancorp.positivepay.model.UploadedFile;
import com.westernalliancebancorp.positivepay.service.CheckService;
import com.westernalliancebancorp.positivepay.service.FileMappingService;
import com.westernalliancebancorp.positivepay.service.FileMetaDataService;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.PositivePayDuplicateFileFoundException;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;
import com.westernalliancebancorp.positivepay.utility.common.DateUtils;
import com.westernalliancebancorp.positivepay.web.validator.FileUploadValidator;

/**
 * Spring Controller to tackle file upload by customers
 *
 * @author Anand Kumar
 */
@Controller
public class FileManagementController {

    @Loggable
    private Logger logger;

    @Autowired
    FileUploadValidator fileUploadValidator;
    @Autowired
    FileMetaDataService fileMetaDataService;
    @Autowired
    FileMappingService fileMappingService;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired 
    UserService userService;
    @Autowired
    CheckService checkService;
    @Autowired
    AccountDao accountDao;
    private static int FILE_UPLOAD_STATUS_OK = 1000;
    private static int FILE_UPLOAD_STATUS_FAILED = 1001;

    /**
     * Method that is called by the spring framework to show the file management page.
     *
     * @param model
     * @param request
     * @return the file upload jsp page
     * @throws Exception
     */
    @RequestMapping(value = "/user/filemanagement", method = RequestMethod.GET)
    public String filemanagement(Model model, HttpServletRequest request)
            throws Exception {
        //Get all the FileMappings for the user
        List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
        if (fileMappings.size() == 0) {
            //forward to the filemapping page with a message to create file mapping first
            model.addAttribute("FILE_MAPPING_NOT_SET", "File Mapping Not Set");
            return "site.file.mapping.page";

        }
        model.addAttribute("fileMappings", fileMappings);
        //Get all the accounts for the user to be shown in the account dropdown
  		List<Account> userAccounts = userService.getUserAccounts();
  		model.addAttribute("userAccounts", userAccounts);
  		//Check file download permissions
  		if (SecurityUtility.hasPermission(Permission.NAME.DOWNLOAD_FILES)) {
  			logger.info("User can download files");
  			model.addAttribute("DOWNLOAD_FILES_PERMISSION", "TRUE");
  		}
        return "site.file.management.page";
    }
    
    @RequestMapping(value = "/user/newfilemanagement", method = RequestMethod.GET)
    public String newfilemanagement(Model model, HttpServletRequest request,  @RequestHeader(value="User-Agent") String userAgent)
            throws Exception {
        //Get all the FileMappings for the user
        List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
        if (fileMappings.size() == 0) {
            //forward to the filemapping page with a message to create file mapping first
            model.addAttribute("FILE_MAPPING_NOT_SET", "File Mapping Not Set");
            return "site.file.mapping.page";

        }
        model.addAttribute("fileMappings", fileMappings);
        //Get all the accounts for the user to be shown in the account dropdown
  		List<Account> userAccounts = userService.getUserAccounts();
  		model.addAttribute("userAccounts", userAccounts);
  		//Check file download permissions
  		if (SecurityUtility.hasPermission(Permission.NAME.DOWNLOAD_FILES)) {
  			logger.info("User can download files");
  			model.addAttribute("DOWNLOAD_FILES_PERMISSION", "TRUE");
  		}
  		if(userAgent.contains("MSIE 9.0"))
  			return "site.multiple.file.management.ie.page";
  		else
  			return "site.multiple.file.management.page";
    }

    /**
     * This method is invoked when the user uploads the file
     *
     * @param uploadedFile
     * @param result
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/filemanagement", method = RequestMethod.POST)
    public String filemanagement(@ModelAttribute("uploadedFile") UploadedFile uploadedFile,
                                 BindingResult result, HttpServletRequest request, Model model, MultipartRequest multipartRequest) throws Exception {
        String requestMapping = (String) request.getServletPath();
        logger.info("processing url : {}", requestMapping);
        fileUploadValidator.validate(uploadedFile, result);
        //Get all the FileMappings for the user
        List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
        model.addAttribute("fileMappings", fileMappings);
        if (result.hasErrors()) {
            model.addAttribute("validationError", "true");
            return "site.file.management.page";
        }
        MultipartFile fileToProcess = uploadedFile.getFile();
        //process the file now
        try {
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(fileToProcess, uploadedFile.getFileMappingId());
            List<String> warningMessages = new ArrayList<String>();
            String successMessage = fileUploadResponse.getNoOfValidChecks() + " of " + fileUploadResponse.getTotalChecksInFile() + " records in the file " + fileToProcess.getOriginalFilename() + " were successfully uploaded.";
            if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() > 0) {
                if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " record was ignored because the account number was not recognized.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " records with unknown account numbers were ignored.");
                }
            }
            if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() > 0) {
                if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " record was already found in the database and hence ignored.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " records were already found in the database and hence ignored.");
                }
            }
            if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() > 0) {
                if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() == 1) {
                	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate record and was ignored.");
                } else {
                	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate records and were ignored.");
                }
            }
            if (fileUploadResponse.getNoOfChecksInWrongDataFormat() > 0) {
                if (fileUploadResponse.getNoOfChecksInWrongDataFormat() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " record was ignored because the data was not in proper format.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " records were ignored because the data was not in proper format.");
                }
            }
            if (fileUploadResponse.getNoOfChecksWithWrongItemCode() > 0) {
                if (fileUploadResponse.getNoOfChecksWithWrongItemCode() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " record was ignored because the item code was not recognized.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " records were ignored because the item codes were not recognized.");
                }
            }
            if (fileUploadResponse.getNoOfPaidAndStopChecks() > 0) {
            	warningMessages.add(fileUploadResponse.getNoOfPaidAndStopChecks() + " records were ignored because they were either STOP or PAID.");
            }
            model.addAttribute("uploadSuccess", successMessage);
            if(warningMessages.size() > 0) {
            	model.addAttribute("warningMessages", warningMessages);
            }
        } catch (PositivePayDuplicateFileFoundException positivePayDuplicateFileFoundException) {
            model.addAttribute("duplicateUpload", "The file " + fileToProcess.getOriginalFilename() + " was not uploaded because the system found that the file has already been uploaded.");
        } catch (FileFormatNotMatchingWithMappingException fileFormatNotMatchingWithMappingException) {
            model.addAttribute("errorWhileUpload", fileFormatNotMatchingWithMappingException.getMessage());
        } catch (Exception exception) {
            logger.error("Problem encountered while uploading the file.", exception);
            model.addAttribute("errorWhileUpload", "True");
        }
        //Get all the accounts for the user to be shown in the account dropdown
  		List<Account> userAccounts = userService.getUserAccounts();
  		model.addAttribute("userAccounts", userAccounts);
        return "site.file.management.page";
    }
    
    
    @RequestMapping(value = "/user/newfilemanagementIE", method = RequestMethod.POST)
    public String newfilemanagementIE(@ModelAttribute("uploadForm") FileUploadForm fileUploadForm,
                                 BindingResult result, HttpServletRequest request, Model model, MultipartRequest multipartRequest) throws Exception {
        /*String requestMapping = (String) request.getServletPath();
        logger.info("processing url : {}", requestMapping);
        fileUploadValidator.validate(uploadedFile, result);*/
        //Get all the FileMappings for the user
        List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
        model.addAttribute("fileMappings", fileMappings);
        if (result.hasErrors()) {
            model.addAttribute("validationError", "true");
            return "site.file.management.page";
        }
        List<MultipartFile> filesToProcess = fileUploadForm.getFiles();
        int counter_file_processing = 0;
        for(MultipartFile fileToProcess :  filesToProcess) {
        	//process the file now
            try {
                FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(fileToProcess, new Long(2));
                List<String> warningMessages = new ArrayList<String>();
                String successMessage = fileUploadResponse.getNoOfValidChecks() + " of " + fileUploadResponse.getTotalChecksInFile() + " records in the file " + fileToProcess.getOriginalFilename() + " were successfully uploaded.";
                if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() > 0) {
                    if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() == 1) {
                    	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " record was ignored because the account number was not recognized.");
                    } else {
                    	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " records with unknown account numbers were ignored.");
                    }
                }
                if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() > 0) {
                    if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() == 1) {
                    	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " record was already found in the database and hence ignored.");
                    } else {
                    	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " records were already found in the database and hence ignored.");
                    }
                }
                if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() > 0) {
                    if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() == 1) {
                    	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate record and was ignored.");
                    } else {
                    	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate records and were ignored.");
                    }
                }
                if (fileUploadResponse.getNoOfChecksInWrongDataFormat() > 0) {
                    if (fileUploadResponse.getNoOfChecksInWrongDataFormat() == 1) {
                    	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " record was ignored because the data was not in proper format.");
                    } else {
                    	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " records were ignored because the data was not in proper format.");
                    }
                }
                if (fileUploadResponse.getNoOfChecksWithWrongItemCode() > 0) {
                    if (fileUploadResponse.getNoOfChecksWithWrongItemCode() == 1) {
                    	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " record was ignored because the item code was not recognized.");
                    } else {
                    	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " records were ignored because the item codes were not recognized.");
                    }
                }
                if (fileUploadResponse.getNoOfPaidAndStopChecks() > 0) {
                	warningMessages.add(fileUploadResponse.getNoOfPaidAndStopChecks() + " records were ignored because they were either STOP or PAID.");
                }
                model.addAttribute("uploadSuccess", successMessage);
                if(warningMessages.size() > 0) {
                	model.addAttribute("warningMessages", warningMessages);
                }
                counter_file_processing++;
            } catch (PositivePayDuplicateFileFoundException positivePayDuplicateFileFoundException) {
                model.addAttribute("duplicateUpload", "The file " + fileToProcess.getOriginalFilename() + " was not uploaded because the system found that the file has already been uploaded.");
            } catch (FileFormatNotMatchingWithMappingException fileFormatNotMatchingWithMappingException) {
                model.addAttribute("errorWhileUpload", fileFormatNotMatchingWithMappingException.getMessage());
            } catch (Exception exception) {
                logger.error("Problem encountered while uploading the file.", exception);
                model.addAttribute("errorWhileUpload", "True");
            }
        }
        
        //Get all the accounts for the user to be shown in the account dropdown
  		List<Account> userAccounts = userService.getUserAccounts();
  		model.addAttribute("userAccounts", userAccounts);
        return "site.multiple.file.management.ie.page";
    }
    
    
    @RequestMapping(value = "/user/newfilemanagement", method = RequestMethod.POST, headers="content-type=multipart/form-data")
    public @ResponseBody FileUploadResponse newfilemanagement(MultipartHttpServletRequest request, HttpServletResponse response) throws Exception {
        /*String requestMapping = (String) request.getServletPath();
        logger.info("processing url : {}", requestMapping);
        fileUploadValidator.validate(uploadedFile, result);
        //Get all the FileMappings for the user
        List<FileMapping> fileMappings = fileMappingService.findAllForLoggedInUser();
        model.addAttribute("fileMappings", fileMappings);
        if (result.hasErrors()) {
            model.addAttribute("validationError", "true");
            return "site.file.management.page";
        }*/
    	Iterator<String> itr =  request.getFileNames();
    	
    	Long fileMappingId = new Long(request.getParameter("fileMappingId"));
    	logger.info("fileMappingId="+fileMappingId);
        MultipartFile fileToProcess = request.getFile(itr.next());
        logger.info("fileToProcess="+fileToProcess.getOriginalFilename());
        //MultipartFile fileToProcess = uploadedFile;
        String successMessage = "";
        FileUploadResponse fileUploadResponse = new FileUploadResponse();
        List<String> warningMessages = new ArrayList<String>();
        //process the file now
        try {
            fileUploadResponse = fileUploadService.uploadFile(fileToProcess, fileMappingId);
            fileUploadResponse.setStatus(FILE_UPLOAD_STATUS_OK);
            successMessage = fileUploadResponse.getNoOfValidChecks() + " of " + fileUploadResponse.getTotalChecksInFile() + " records in the file " + fileToProcess.getOriginalFilename() + " were successfully uploaded.";
            if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() > 0) {
                if (fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " record was ignored because the account number was not recognized.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfchecksWithNoAccountNumberMatch() + " records with unknown account numbers were ignored.");
                }
            }
            if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() > 0) {
                if (fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " record was already found in the database and hence ignored.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfduplicatesChecksWithinDatabase() + " records were already found in the database and hence ignored.");
                }
            }
            if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() > 0) {
                if (fileUploadResponse.getNoOfduplicatesChecksWithinFile() == 1) {
                	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate record and was ignored.");
                } else {
                	warningMessages.add("File also contained "+fileUploadResponse.getNoOfduplicatesChecksWithinFile() + " duplicate records and were ignored.");
                }
            }
            if (fileUploadResponse.getNoOfChecksInWrongDataFormat() > 0) {
                if (fileUploadResponse.getNoOfChecksInWrongDataFormat() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " record was ignored because the data was not in proper format.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfChecksInWrongDataFormat() + " records were ignored because the data was not in proper format.");
                }
            }
            if (fileUploadResponse.getNoOfChecksWithWrongItemCode() > 0) {
                if (fileUploadResponse.getNoOfChecksWithWrongItemCode() == 1) {
                	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " record was ignored because the item code was not recognized.");
                } else {
                	warningMessages.add(fileUploadResponse.getNoOfChecksWithWrongItemCode() + " records were ignored because the item codes were not recognized.");
                }
            }
            if (fileUploadResponse.getNoOfPaidAndStopChecks() > 0) {
            	warningMessages.add(fileUploadResponse.getNoOfPaidAndStopChecks() + " records were ignored because they were either STOP or PAID.");
            }
            /*model.addAttribute("uploadSuccess", successMessage);
            if(warningMessages.size() > 0) {
            	model.addAttribute("warningMessages", warningMessages);
            }*/
        } catch (PositivePayDuplicateFileFoundException positivePayDuplicateFileFoundException) {
        	fileUploadResponse.setStatus(FILE_UPLOAD_STATUS_FAILED);
        	logger.info("positivePayDuplicateFileFoundException", positivePayDuplicateFileFoundException);
        	successMessage = "The file " + fileToProcess.getOriginalFilename() + " was not uploaded because the system found that the file has already been uploaded.";
           // model.addAttribute("duplicateUpload", "The file " + fileToProcess.getOriginalFilename() + " was not uploaded because the system found that the file has already been uploaded.");
        } catch (FileFormatNotMatchingWithMappingException fileFormatNotMatchingWithMappingException) {
        	//model.addAttribute("errorWhileUpload", fileFormatNotMatchingWithMappingException.getMessage());
        	fileUploadResponse.setStatus(FILE_UPLOAD_STATUS_FAILED);
        	successMessage = fileFormatNotMatchingWithMappingException.getMessage();
        	logger.info("FileFormatNotMatchingWithMappingException", fileFormatNotMatchingWithMappingException);
        } catch (Exception exception) {
        	fileUploadResponse.setStatus(FILE_UPLOAD_STATUS_FAILED);
        	successMessage = "Problem encountered while uploading the file.";
            logger.error("Problem encountered while uploading the file.", exception);
            //model.addAttribute("errorWhileUpload", "True");
        }
        fileUploadResponse.setResponse(successMessage);
        fileUploadResponse.setWarnings(warningMessages);
        //Get all the accounts for the user to be shown in the account dropdown
  		//List<Account> userAccounts = userService.getUserAccounts();
  		//model.addAttribute("userAccounts", userAccounts);
        return fileUploadResponse;
    }
    
    @RequestMapping(value = "/user/allfiles", method = RequestMethod.GET)
    public @ResponseBody List<RecentFileDto> getAllFiles() {
    	List<FileMetaData> fileMetaDataList = fileMetaDataService.findAllFilesUploaded();
    	return getRecentFiles(fileMetaDataList);
    }

    @RequestMapping(value = "/user/filterfiles", method = RequestMethod.GET)
    public @ResponseBody List<RecentFileDto> filterFiles(@RequestParam(value = "uploadDate", required = false) String uploadDateStr,
    		@RequestParam(value = "accountNumber", required = false) String accountNumber,
    		@RequestParam(value = "noOfDaysBefore", required = false) String noOfDaysBefore) {
    	Date uploadDate = null;
    	if(!uploadDateStr.equalsIgnoreCase("")){
    		try {
				uploadDate = DateUtils.getDateFromString(uploadDateStr);
			} catch (ParseException e) {
				logger.warn("Couldn't parse the upload date {}, so would not be used in search", uploadDateStr);
			}
    	}
    	//Get the file metadata ids from the account numbers
    	List<Long>  fileMetaDataIds = fileMetaDataService.findFileMetaDataIdsByAccountNumber(accountNumber);
    	List<FileMetaData> fileMetaDataList = new ArrayList<FileMetaData>();
    	List<RecentFileDto> recentFiles = fileMetaDataService.filterFilesBy(uploadDate, fileMetaDataIds, noOfDaysBefore);
    	for(RecentFileDto recentFile: recentFiles){
			FileMetaData fileMetaData = new FileMetaData();
			fileMetaData.setId(recentFile.getFileMetaDataId());
			fileMetaDataList.add(fileMetaData);
		}
    	List<RecentFileDto> recentFilesToReturn = new ArrayList<RecentFileDto>();
    	if(!fileMetaDataList.isEmpty()) {
    		Map<Long, Integer> processedRecordCountMap = checkService.getProcessedItemsCountOfFile(fileMetaDataList);
	        Map<Long, Integer> unProcessedRecordCountMap = checkService.getUnProcessedItemsCountOfFile(fileMetaDataList);
	        for(RecentFileDto recentFile: recentFiles){
	        	if (recentFile.getFileMetaDataId() == null || processedRecordCountMap.get(recentFile.getFileMetaDataId()) == null) {
	    			recentFile.setItemsLoaded(new Long(0));
                } else {
                	recentFile.setItemsLoaded(processedRecordCountMap.get(recentFile.getFileMetaDataId()).longValue());
                }
                if (recentFile.getFileMetaDataId() == null || unProcessedRecordCountMap.get(recentFile.getFileMetaDataId()) == null) {
                	recentFile.setErrorRecordsLoaded(new Long(0));
                } else {
                	recentFile.setErrorRecordsLoaded(unProcessedRecordCountMap.get(recentFile.getFileMetaDataId()).longValue());
                }
                recentFilesToReturn.add(recentFile);
	        }
    	}
    	return recentFilesToReturn;
    }
    
    @PositivePaySecurity(resource = "DOWNLOAD_FILES", errorMessage = "doesn't have permission to download files.", group = Permission.TYPE.OTHER_PERMISSIONS)
    @RequestMapping(value = "/user/file/download", method = RequestMethod.GET)
    public void downloadFile(HttpServletResponse httpServletResponse,
                             @RequestParam(value = "fileUid", required = true) String uid) throws IOException {
        PositivePayFtpFile positivePayFtpFile = null;
        if (uid.equals(FileMetaData.EXCEPTIONAL_REFERENCE_DATA_FILE_NAME) ||
                uid.equals(FileMetaData.MANUAL_ENTRY_FILE_NAME) ||
                uid.equals(FileMetaData.MIGRATED_FILE_NAME)) {
            positivePayFtpFile = new PositivePayFtpFile();
            positivePayFtpFile.setFileName("No file found");
            positivePayFtpFile.setContents("No file in content as it is a migrated or manual entry record !!".getBytes());
        } else {
            positivePayFtpFile = fileUploadService.downloadFile(uid);
        }
        String mimeType = "application/octet-stream";
        httpServletResponse.setContentType(mimeType);
        httpServletResponse.setContentLength(positivePayFtpFile.getBytes().length);
        // set headers for the response
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                positivePayFtpFile.getOriginalFilename());
        httpServletResponse.setHeader(headerKey, headerValue);
        // get output stream of the response
        OutputStream outStream = httpServletResponse.getOutputStream();

        outStream.write(positivePayFtpFile.getBytes());
        outStream.close();
    }
    
    private List<RecentFileDto> getRecentFiles(List<FileMetaData> fileMetaDataList){
    	List<RecentFileDto> allFiles = new ArrayList<RecentFileDto>();
    	if(!fileMetaDataList.isEmpty()) {
    		Map<Long, Integer> processedRecordCountMap = checkService.getProcessedItemsCountOfFile(fileMetaDataList);
	        Map<Long, Integer> unProcessedRecordCountMap = checkService.getUnProcessedItemsCountOfFile(fileMetaDataList);
	    	for(FileMetaData fileMetaData: fileMetaDataList) {
	    		RecentFileDto recentFile = new RecentFileDto();
	    		recentFile.setFileMetaDataId(fileMetaData.getId());
	    		recentFile.setFileName(fileMetaData.getOriginalFileName());
	    		recentFile.setNoOfRecords(fileMetaData.getItemsReceived());
	    		recentFile.setUploadDate(fileMetaData.getAuditInfo().getDateCreated());
	    		recentFile.setFileUid(fileMetaData.getFileName());
	    		if(fileMetaData.getFileMapping() != null)
	    			recentFile.setCompanyName(fileMetaData.getFileMapping().getCompany().getName());
	    		if (fileMetaData.getId() == null || processedRecordCountMap.get(fileMetaData.getId()) == null) {
	    			recentFile.setItemsLoaded(new Long(0));
                } else {
                	recentFile.setItemsLoaded(processedRecordCountMap.get(fileMetaData.getId()).longValue());
                }
                if (fileMetaData.getId() == null || unProcessedRecordCountMap.get(fileMetaData.getId()) == null) {
                	recentFile.setErrorRecordsLoaded(new Long(0));
                } else {
                	recentFile.setErrorRecordsLoaded(unProcessedRecordCountMap.get(fileMetaData.getId()).longValue());
                }
	    		allFiles.add(recentFile);
	    	}
    	}
    	return allFiles;
    }
}

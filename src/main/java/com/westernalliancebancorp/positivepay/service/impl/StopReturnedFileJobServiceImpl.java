package com.westernalliancebancorp.positivepay.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.exception.NASConnectException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.PositivePayDuplicateFileFoundException;
import com.westernalliancebancorp.positivepay.service.StopReturnedFileJobService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;

/**
 * User: gduggirala
 * Date: 28/5/14
 * Time: 3:28 PM
 */
@Component
public class StopReturnedFileJobServiceImpl implements StopReturnedFileJobService {
    @Value("${stoprtn.file.job.location}")
    public String stopReturnedFileJobLocation;

    @Loggable
    private static Logger logger;

    @Autowired
    FileUploadUtils fileUploadUtils;

    @Autowired
    FileUploadService fileUploadService;
    @Override
    public Map<String, Integer> pullStopReturnedFile() throws NASConnectException {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        int duplicateFileCount = 0;
        File[] filesList = null;
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        List<PositivePayFtpFile> positivePayFtpFileList = new ArrayList<PositivePayFtpFile>();
        try {
	        File stopReturnedFileDirectory = new File(stopReturnedFileJobLocation);
	        filesList = stopReturnedFileDirectory.listFiles();
        } catch (Exception e) {
        	throw new NASConnectException("Exception occured while accessing NAS");
        }
        if (filesList.length == 0) {
        	returnMap.put(Constants.FILE_STATUS_CODE, Integer.parseInt(Constants.NO_FILE));
        	returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, 0);
        	returnMap.put(Constants.ITEMS_IN_ERROR, 0);
        	return returnMap;
        }  
        for (File file : filesList) {
            if (!file.isDirectory()) {
                PositivePayFtpFile positivePayFtpFile = new PositivePayFtpFile();
                //The file name the path altogether will go.
                positivePayFtpFile.setFileName(file.getName());
                positivePayFtpFile.setPath(file.getPath());
                positivePayFtpFileList.add(positivePayFtpFile);
                logger.info(Log.event(Event.STOP_RETURNED_FILE_FILE_READ_SUCCESS, "Successfully read  Stop returned file " + file.getName() + " located at " + file.getPath()));
            }
        }
        logger.info("Successfully read " + positivePayFtpFileList.size() + " file('s)");
        logger.info("Started handing over the files to File upload services to parse and create reference records.");
        for (PositivePayFtpFile positivePayFtpFile : positivePayFtpFileList) {
            try {
                byte[] bytesRead = fileUploadUtils.readBytes(positivePayFtpFile.getPath());
                positivePayFtpFile.setContents(bytesRead);
                positivePayFtpFile.setFileSize(bytesRead.length);
                fileUploadService.processMainframeFile(positivePayFtpFile);
                logger.info(Log.event(Event.STOP_RETURNED_FILE_FILE_PROCESSING_SUCCESS, "Successfully processed Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file."));
                //Let me save some space after processing the file by initializing the content to 0, earlier one will become orphan and will be ready to garbage collected;
                positivePayFtpFile.setContents(new byte[0]);
                positivePayFtpFile.setFileSize(0l);
                itemsProcessedSuccessfuly++;
                deleteFile(positivePayFtpFile);
            } catch (PositivePayDuplicateFileFoundException pd) {
            	duplicateFileCount++;
                pd.printStackTrace();
                logger.error(Log.event(Event.STOP_RETURNED_FILE_DUPLICATE_FILE, "Unable to process Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " as it is marked as duplicate continuing to process another file.", pd), pd);
                deleteFile(positivePayFtpFile);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(Log.event(Event.STOP_RETURNED_FILE_FILE_PROCESSING_UNSUCCESSFUL, "Unable to process Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file.", e), e);
                itemsInError++;
            }
        }
        if(duplicateFileCount > 0){
        	returnMap.put(Constants.FILE_STATUS_CODE, Integer.parseInt(Constants.DUPLICATE_FILE));
        }
        returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
        returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);
        return returnMap;
    }

    public static boolean deleteFile(PositivePayFtpFile positivePayFtpFile) {
        File file =  new File(positivePayFtpFile.getPath());
        if(file.isFile()) {
            return file.delete();
        }else{
            logger.error("I'll not delete directory, sorry "+positivePayFtpFile.getPath());
            return Boolean.FALSE;
        }
    }
}

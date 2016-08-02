package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.westernalliancebancorp.positivepay.exception.SftpConnectException;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.PositivePayDuplicateFileFoundException;
import com.westernalliancebancorp.positivepay.service.SftpPollingService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.utility.Event;
import com.westernalliancebancorp.positivepay.utility.Log;
import com.westernalliancebancorp.positivepay.utility.SFTPUtility;
import com.westernalliancebancorp.positivepay.utility.common.Constants;
import com.westernalliancebancorp.positivepay.utility.common.FileUploadUtils;

/**
 * 
 * @author Gopal Patil
 *
 */
@Service
public class SftpPollingServiceImpl implements SftpPollingService {
	
	@Loggable
    private Logger logger;
    @Value("${sftp.server.name}")
    private String sFtpServerName;
    @Value("${sftp.port}")
    private String sFtpServerport;
    @Value("${sftp.user.name}")
    private String userName;
    @Value("${sftp.password}")
    private String password;

    @Autowired
    FileUploadUtils fileUploadUtils;
    @Autowired
    FileUploadService fileUploadService;
    
    @Value("${crs.paid.file.job.location}")
    public String crsPaidFileJobSftpLocation;
    
    @Value("${dailystop.file.job.location}")
    public String stopFileJobSftpLocation;
    
    @Value("${stoprtn.file.job.location}")
    public String stopRtnFileJobSftpLocation;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Integer> pullFiles(String fileType) throws SftpConnectException {
        Map<String, Integer> returnMap = new HashMap<String, Integer>();
        List<PositivePayFtpFile> positivePayFtpFileList = new ArrayList<PositivePayFtpFile>();
        List<String> deleteFileList = new ArrayList<String>();
        Vector<ChannelSftp.LsEntry> filesList = new Vector<ChannelSftp.LsEntry>();
        int duplicateFileCount = 0;
        int itemsProcessedSuccessfuly = 0;
        int itemsInError = 0;
        int port = Integer.parseInt(sFtpServerport);
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        String sftpWorkingDir = "";
        
    	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
    		sftpWorkingDir = crsPaidFileJobSftpLocation;        		
    	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
    		sftpWorkingDir = stopFileJobSftpLocation;
    	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
    		sftpWorkingDir = stopRtnFileJobSftpLocation;
    	}
        
        try {
	        session = SFTPUtility.connect(sFtpServerName, port, userName, password);	        
	        channel = session.openChannel("sftp");
            channel.connect();
            logger.debug("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            
            channelSftp.cd(sftpWorkingDir);
            filesList = channelSftp.ls(sftpWorkingDir);
            
            if(filesList.isEmpty()) {
            	returnMap.put(Constants.FILE_STATUS_CODE, Integer.parseInt(Constants.NO_FILE));
           	 	returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, 0);
           	 	returnMap.put(Constants.ITEMS_IN_ERROR, 0);
           	 	return returnMap;
           } 
            
            for (ChannelSftp.LsEntry file : filesList) {
                PositivePayFtpFile positivePayFtpFile = new PositivePayFtpFile();
                //The file name the path altogether will go.
                positivePayFtpFile.setFileName(file.getFilename());
                positivePayFtpFile.setPath(channelSftp.pwd());
                
                if(file.getAttrs() != null) {
                	if(file.getAttrs().getSize() > 0) {
                		positivePayFtpFile.setFileSize(file.getAttrs().getSize()); 
                	} else {
                		logger.info("File " + file.getFilename() + " is empty, located at " + channelSftp.pwd());
                		continue;
                	}
                }                
                positivePayFtpFileList.add(positivePayFtpFile);  
                
            	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
            		logger.info(Log.event(Event.CRS_PAID_FILE_READ_SUCCESS, "Successfully read CRS paid file " + file.getFilename() + " located at " + channelSftp.pwd()));      		
            	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
            		logger.info(Log.event(Event.DAILY_STOP_FILE_FILE_READ_SUCCESS, "Successfully read Daily Stop file " + file.getFilename() + " located at " + channelSftp.pwd()));
            	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
            		logger.info(Log.event(Event.STOP_RETURNED_FILE_FILE_READ_SUCCESS, "Successfully read Stop returned file " + file.getFilename() + " located at " + channelSftp.pwd()));
            	}
            }
            
            logger.info("Successfully read " + positivePayFtpFileList.size() + " file('s)");
            logger.info("Started handing over the files to File upload services to parse and create reference records."); 
            
            for (PositivePayFtpFile positivePayFtpFile : positivePayFtpFileList) {
                try {
                    byte[] bytesRead = fileUploadUtils.readBytes(channelSftp.get(positivePayFtpFile.getFileName()), positivePayFtpFile.getFileSize());
                    positivePayFtpFile.setContents(bytesRead);
                    positivePayFtpFile.setFileSize(bytesRead.length);
                    fileUploadService.processMainframeFile(positivePayFtpFile);
                    
                	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
                		logger.info(Log.event(Event.CRS_PAID_FILE_PROCESSING_SUCCESS, "Successfully processed CRS paid file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file."));                               		
                	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
                		logger.info(Log.event(Event.DAILY_STOP_FILE_FILE_PROCESSING_SUCCESS, "Successfully processed Daily Stop file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file."));
                	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
                		logger.info(Log.event(Event.STOP_RETURNED_FILE_FILE_PROCESSING_SUCCESS, "Successfully processed Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file."));
                	}
                    //Let me save some space after processing the file by initializing the content to 0, earlier one will become orphan and will be ready to garbage collected;
                    deleteFileList.add(positivePayFtpFile.getFileName());
                	itemsProcessedSuccessfuly++;
                    positivePayFtpFile.setContents(new byte[0]);
                    positivePayFtpFile.setFileSize(0l);
                } catch (PositivePayDuplicateFileFoundException pd) {
                	duplicateFileCount++;
                    pd.printStackTrace();
                	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
                		logger.error(Log.event(Event.CRS_PAID_DUPLICATE_FILE, "Unable to process CRS Paid file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " as it is marked as duplicate continuing to process another file.",pd), pd);                               		
                	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
                		 logger.error(Log.event(Event.DAILY_STOP_FILE_DUPLICATE_FILE, "Unable to process Daily Stop file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " as it is marked as duplicate continuing to process another file.",pd), pd);
                	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
                        logger.error(Log.event(Event.STOP_RETURNED_FILE_DUPLICATE_FILE, "Unable to process Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " as it is marked as duplicate continuing to process another file.", pd), pd);
                	} 
                	deleteFileList.add(positivePayFtpFile.getFileName());
                } catch (Exception e) {
                    e.printStackTrace();
                	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
                        logger.error(Log.event(Event.CRS_PAID_FILE_PROCESSING_UNSUCCESSFUL, "Unable to process CRS Paid file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file.",e), e);
                	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
               			logger.error(Log.event(Event.DAILY_STOP_FILE_FILE_PROCESSING_UNSUCCESSFUL, "Unable to process Daily Stop file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file.",e), e);
                	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
                		logger.info(Log.event(Event.STOP_RETURNED_FILE_FILE_PROCESSING_UNSUCCESSFUL, "Unable to process Stop returned file " + positivePayFtpFile.getName() + " located at " + positivePayFtpFile.getPath() + " continuing to process another file."));
                	}
                     itemsInError++;
                }
            } 
            if(duplicateFileCount > 0){
            	returnMap.put(Constants.FILE_STATUS_CODE, Integer.parseInt(Constants.DUPLICATE_FILE));
            }
            returnMap.put(Constants.ITEMS_PROCESSED_SUCCESSFULLY, itemsProcessedSuccessfuly);
            returnMap.put(Constants.ITEMS_IN_ERROR, itemsInError);	       
        } catch (JSchException e) {
        	throw new SftpConnectException("Exception occured while connecting to sftp server" , e);
        } catch (SftpException e) {
        	throw new SftpConnectException("Exception occured while making a call to get the list" , e);
		} finally{
			SFTPUtility.disconnect(channelSftp, channelSftp, session);
        }
        if(!deleteFileList.isEmpty()) {
        	this.deleteFile(deleteFileList, fileType);
        }
        return returnMap;
	}
	
    private boolean deleteFile(List<String> deleteFileList, String fileType) {
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        int port = Integer.parseInt(sFtpServerport);
        String sftpWorkingDir = "";
        
    	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
    		sftpWorkingDir = crsPaidFileJobSftpLocation;        		
    	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
    		sftpWorkingDir = stopFileJobSftpLocation;
    	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
    		sftpWorkingDir = stopRtnFileJobSftpLocation;
    	}        
        try {
	        session = SFTPUtility.connect(sFtpServerName, port, userName, password);	        
	        channel = session.openChannel("sftp");
            channel.connect();
            logger.debug("sftp channel opened and connected.");
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(sftpWorkingDir);            
            for (String fileName : deleteFileList) {
                try {
                	channelSftp.rm(fileName);
                } catch (SftpException e) {                	
                	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
                		logger.error(Log.event(Event.CRS_PAID_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);                               		
                	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
                		 logger.error(Log.event(Event.DAILY_STOP_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);
                	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
                        logger.error(Log.event(Event.STOP_RETURNED_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);
                	} 
                	return Boolean.FALSE;
        		} catch (Exception e) {
                	if(fileType.equalsIgnoreCase(Constants.CRS_PAID)) {
                		logger.error(Log.event(Event.CRS_PAID_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);                               		
                	} else if(fileType.equalsIgnoreCase(Constants.DAILY_STOP)) {
                		 logger.error(Log.event(Event.DAILY_STOP_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);
                	} else if(fileType.equalsIgnoreCase(Constants.STOP_PRESENTED)) {
                        logger.error(Log.event(Event.STOP_RETURNED_FILE_DELETING_UNSUCCESSFUL, "Exception occured while deleting file " + fileName + " located at " + sftpWorkingDir ,e), e);
                	} 
        			return Boolean.FALSE;
        		}
            }            
        } catch (JSchException e) {
        	logger.error("Exception occured while connecting to sftp server" , e);
        	return Boolean.FALSE;
        } catch (SftpException e) {
        	logger.error("Exception occured while deleting file" , e);
        	return Boolean.FALSE;
		} finally{
			SFTPUtility.disconnect(channelSftp, channelSftp, session);
        }
        return Boolean.TRUE;
    }   

}

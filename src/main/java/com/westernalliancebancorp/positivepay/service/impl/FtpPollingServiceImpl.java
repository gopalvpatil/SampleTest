package com.westernalliancebancorp.positivepay.service.impl;

import it.sauronsoftware.ftp4j.FTPAbortedException;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPDataTransferException;
import it.sauronsoftware.ftp4j.FTPException;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.FTPIllegalReplyException;
import it.sauronsoftware.ftp4j.FTPListParseException;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.FileMappingDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.dto.FileUploadResponse;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.service.FileUploadService;
import com.westernalliancebancorp.positivepay.service.FtpPollingService;
import com.westernalliancebancorp.positivepay.service.PositivePayDuplicateFileFoundException;
import com.westernalliancebancorp.positivepay.service.PositivePayFtpPollingServiceException;
import com.westernalliancebancorp.positivepay.service.UserService;
import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import com.westernalliancebancorp.positivepay.utility.FTPUtility;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

/**
 * FtpPollingServiceImpl is
 *
 * @author Giridhar Duggirala
 */
@Component("ftpPollingService")
public class FtpPollingServiceImpl implements FtpPollingService {
    @Loggable
    private Logger logger;
    @Value("${ftp.server.name}")
    private String ftpServerName;
    @Value("${ftp.user.name}")
    private String userName;
    @Value("${ftp.password}")
    private String password;
    @Autowired
    private ExecutorService threadPoolExecutor;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private UserDetailDao userDetailDao;
    @Autowired
    private FileMappingDao fileMappingDao;
    @Autowired
    private UserService userService;

    ExecutorCompletionService executorCompletionService = null;

    public List<PositivePayFtpFile> getFiles() throws PositivePayFtpPollingServiceException {
        FTPClient ftpClient = null;
        try {
            ftpClient = FTPUtility.connect(ftpServerName, userName, password);
            List<PositivePayFtpFile> filesList = new ArrayList<PositivePayFtpFile>();
            try {
                //We are expecting we will end up in root directory and we will scroll through
                //the list of user directories.
                return getFilesList(ftpClient.list(), ftpClient, filesList,"");
            } catch (FTPListParseException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            } catch (FTPAbortedException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            } catch (FTPDataTransferException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            } catch (FTPIllegalReplyException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            } catch (FTPException e) {
                e.printStackTrace();
                throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
            }
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect(true);
                } catch (Exception e) {
                    throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
                }
            }
        }
    }

    private List<PositivePayFtpFile> getFilesList(FTPFile[] ftpFiles, FTPClient ftpClient, List<PositivePayFtpFile> filesList, String userName) throws PositivePayFtpPollingServiceException {
        try {
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getType() == FTPFile.TYPE_FILE) {
                    PositivePayFtpFile positivePayFtpFile = new PositivePayFtpFile();
                    positivePayFtpFile.setModifiedDate(ftpFile.getModifiedDate());
                    positivePayFtpFile.setPath(ftpClient.currentDirectory());
                    positivePayFtpFile.setFileName(ftpFile.getName());
                    positivePayFtpFile.setFileSize(ftpFile.getSize());
                    positivePayFtpFile.setUserName(userName);
                    filesList.add(positivePayFtpFile);
                } else if (ftpFile.getType() == FTPFile.TYPE_DIRECTORY) {
                    String dirName = ftpFile.getName();
                    ftpClient.changeDirectory(dirName);
                    getFilesList(ftpClient.list(), ftpClient, filesList, dirName);
                    ftpClient.changeDirectoryUp();
                }
            }
            return filesList;
        } catch (FTPListParseException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        } catch (FTPException e) {
            e.printStackTrace();
            throw new PositivePayFtpPollingServiceException("Exception while making a call to get the list", e);
        }
    }

    public void retrieveAndStoreFiles(List<PositivePayFtpFile> positivePayFtpFiles) throws PositivePayFtpPollingServiceException, Exception {
        executorCompletionService = new ExecutorCompletionService(threadPoolExecutor);
        for (final PositivePayFtpFile positivePayFtpFile : positivePayFtpFiles) {
            //http://java.dzone.com/articles/executorcompletionservice
            executorCompletionService.submit(new Callable() {
                @Override
                public PositivePayFtpFile call() throws Exception {
                    return getFile(positivePayFtpFile);
                }
            });
        }
        for (int i = 0; i < positivePayFtpFiles.size(); i++) {
            try {
                Future<PositivePayFtpFile> positivePayFtpFileFuture = executorCompletionService.take();
                PositivePayFtpFile positivePayFtpFile = null;
                try {
                    positivePayFtpFile = positivePayFtpFileFuture.get();
                    isFileAccepted(positivePayFtpFileFuture.get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Exception while storing the file").append((positivePayFtpFile == null ? "null file" : positivePayFtpFile.getPath() + positivePayFtpFile.getFileName()));
                    logger.error(stringBuffer.toString(), e);
                } catch (NoSuchAlgorithmException nsa) {
                    nsa.printStackTrace();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Exception while storing the file").append((positivePayFtpFile == null ? "null file" : positivePayFtpFile.getPath() + positivePayFtpFile.getFileName()));
                    logger.error(stringBuffer.toString(), nsa);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Exception while storing the file").append((positivePayFtpFile == null ? "null file" : positivePayFtpFile.getPath() + positivePayFtpFile.getFileName()));
                    logger.error(stringBuffer.toString(), ioe);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Exception while taking the future object into consideration", e);
            }
        }

    }

    private boolean isFileAccepted(PositivePayFtpFile positivePayFtpFile) throws Exception {
        SecurityUtility.setPrincipal(positivePayFtpFile.getUserName());
        //TODO: fileMappingId is hard coded for now. Please change later.
        if (positivePayFtpFile != null) {
            logger.debug("PositivepayFile: " + positivePayFtpFile);
            try {
                List<FileMapping> fileMappingList = fileMappingDao.findAllByCompanyId(userService.getUserCompany(positivePayFtpFile.getUserName()).getId());
                if(fileMappingList != null && !fileMappingList.isEmpty() && fileMappingList.size() == 1) {
                    FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(positivePayFtpFile, fileMappingList.get(0).getId());
                    postProcessFile(fileUploadResponse, positivePayFtpFile);
                }else{
                    throw new RuntimeException("More than one file mapping found for the user "+positivePayFtpFile.getUserName()+" or no FileMapping found the user ");
                }
            } catch (PositivePayDuplicateFileFoundException pdf) {
                pdf.printStackTrace();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Exception while storing the file as this file is duplicate file ").append((positivePayFtpFile == null ? "null file" : positivePayFtpFile.getPath() + positivePayFtpFile.getFileName()));
                logger.error(stringBuffer.toString(), pdf);
                processDuplicateFile(positivePayFtpFile);
            } catch (RuntimeException re) {
                if(re.getMessage().startsWith("More than")) {
                    re.printStackTrace();
                    logger.error("Exception while processing PositivePayFtpFile "+positivePayFtpFile,re);
                }else{
                    re.printStackTrace();
                    logger.error("Exception while processing PositivePayFtpFile "+positivePayFtpFile,re);
                }
            }
        }
        return Boolean.TRUE;
    }

    private void postProcessFile(FileUploadResponse fileUploadResponse, PositivePayFtpFile positivePayFtpFile) throws PositivePayFtpPollingServiceException {
        //TODO: Send an email if the file is processed successfully
        deleteFile(positivePayFtpFile);
    }

    private void processDuplicateFile(PositivePayFtpFile positivePayFtpFile) {
        //TODO: Send an email incase file is a dupliate file.
    }

    private boolean deleteFile(PositivePayFtpFile positivePayFtpFile) throws PositivePayFtpPollingServiceException {
        FTPClient ftpClient = null;
        ftpClient = FTPUtility.connect(ftpServerName, userName, password);
        try {
            ftpClient.changeDirectory(positivePayFtpFile.getPath());
            ftpClient.deleteFile(positivePayFtpFile.getFileName());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (FTPException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect(true);
                } catch (Exception e) {
                    logger.error("Exception while disconnecting from FTP site " + positivePayFtpFile + " and the reason being " + e);
                }
            }
        }
        return Boolean.TRUE;
    }

    private PositivePayFtpFile getFile(PositivePayFtpFile positivePayFtpFile) throws PositivePayFtpPollingServiceException {
        FTPClient ftpClient = null;
        ftpClient = FTPUtility.connect(ftpServerName, userName, password);
        PositivePayFtpFile duplicatePositivePayFtpFile = null;
        try {
            ftpClient.changeDirectory(positivePayFtpFile.getPath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ftpClient.download(positivePayFtpFile.getFileName(), bufferedOutputStream, 0, new FTPDataTransferListenerImpl());
            bufferedOutputStream.flush();
            //Just create a new instance...
            duplicatePositivePayFtpFile = new PositivePayFtpFile();
            BeanUtils.copyProperties(positivePayFtpFile, duplicatePositivePayFtpFile);
            duplicatePositivePayFtpFile.setContents(byteArrayOutputStream.toByteArray());
            return duplicatePositivePayFtpFile;
        } catch (FTPDataTransferException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (FTPAbortedException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (FTPIllegalReplyException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } catch (FTPException e) {
            e.printStackTrace();
            logger.error("Exception while downloading the file " + positivePayFtpFile + " and the reason being " + e);
        } finally {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect(true);
                } catch (Exception e) {
                    logger.error("Exception while disconnecting from FTP site " + positivePayFtpFile + " and the reason being " + e);
                }
            }
        }
        return duplicatePositivePayFtpFile;
    }
}
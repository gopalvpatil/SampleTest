package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;

import java.util.List;

/**
 * FtpPollingService is
 *
 * @author Giridhar Duggirala
 */

public interface FtpPollingService {
    List<PositivePayFtpFile> getFiles() throws PositivePayFtpPollingServiceException;
    void retrieveAndStoreFiles(List<PositivePayFtpFile> positivePayFtpFiles) throws PositivePayFtpPollingServiceException, Exception;
}

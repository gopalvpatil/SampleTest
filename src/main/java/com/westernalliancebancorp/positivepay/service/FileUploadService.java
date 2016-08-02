package com.westernalliancebancorp.positivepay.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.westernalliancebancorp.positivepay.service.model.PositivePayFtpFile;
import org.springframework.web.multipart.MultipartFile;

import com.westernalliancebancorp.positivepay.dto.FileUploadResponse;
/**
 * Interface providing service methods to uploading of files into database.
 * @author Anand Kumar
 */
public interface FileUploadService {
	FileUploadResponse uploadFile(MultipartFile fileToProcess, Long fileMappingId) throws Exception;
	boolean isDuplicateFile(MultipartFile fileToProcess) throws IOException, NoSuchAlgorithmException;
	void processMainframeFile(MultipartFile mainFrameFile) throws Exception;
    PositivePayFtpFile downloadFile(String uid) throws IOException;
}

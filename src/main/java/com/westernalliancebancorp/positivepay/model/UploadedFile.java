/**
 * 
 */
package com.westernalliancebancorp.positivepay.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * The Class representing uploaded file.
 * 
 * @author Anand Kumar
 */

public class UploadedFile {
	
	private MultipartFile file;
	private Long fileMappingId;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Long getFileMappingId() {
		return fileMappingId;
	}

	public void setFileMappingId(Long fileMappingId) {
		this.fileMappingId = fileMappingId;
	}
}

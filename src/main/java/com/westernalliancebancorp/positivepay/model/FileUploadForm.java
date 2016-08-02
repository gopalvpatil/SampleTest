package com.westernalliancebancorp.positivepay.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadForm {
	
	private List<MultipartFile> files;
	
	/**
	 * @return the files
	 */
	public List<MultipartFile> getFiles() {
		return files;
	}
	/**
	 * @param files the files to set
	 */
	public void setFiles(List<MultipartFile> files) {
		this.files = files;
	}

}

/**
 * 
 */
package com.westernalliancebancorp.positivepay.web.validator;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.UploadedFile;

/**
 * Validator class for file upload.
 * @author Anand Kumar
 *
 */
@Component
public class FileUploadValidator implements Validator {
	
	@Loggable
	private Logger logger;
	
	@Value("${file.extensions.allowed}")
	private String fileExtensionsAllowed;
	
	@Value("${maximum.file.size.allowed}")
	private String maximumFileSizeAllowed;
	
	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> arg0) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(Object uploadedFile, Errors errors) {
		UploadedFile file = (UploadedFile) uploadedFile;
		if (file.getFile().getSize() == 0) {
			errors.rejectValue("file", "file.upload.notselected");  
		}
		if (file.getFile().getSize() > Integer.parseInt(maximumFileSizeAllowed)) {  
			errors.rejectValue("file", "file.upload.size.overlimit");  
		} 
		if (!(fileExtensionsAllowed.toLowerCase()).contains(FilenameUtils.getExtension(file.getFile().getOriginalFilename()).toLowerCase())) {
			errors.rejectValue("file", "file.upload.xtns.allowed");
		}
		if (file.getFileMappingId()==null) {
			errors.rejectValue("file", "file.upload.template.notselected");
		}
	}
}

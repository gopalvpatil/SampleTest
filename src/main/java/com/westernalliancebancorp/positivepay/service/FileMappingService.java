package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.FileMapping;
/**
 * Interface providing service methods to work with the File Mapping Model
 * @author Anand Kumar
 */
public interface FileMappingService {
	FileMapping saveOrUpdate(FileMapping fileMapping);
	void delete(FileMapping fileMapping);
	FileMapping findById(Long id);
	List<FileMapping> findAll();
	List<FileMapping> saveAll(List<FileMapping> fileMappings);
	FileMapping findByCompanyIdAndFileMappingId(Long companyId, Long fileMappingId);
	List<FileMapping> findAllForLoggedInUser();
}

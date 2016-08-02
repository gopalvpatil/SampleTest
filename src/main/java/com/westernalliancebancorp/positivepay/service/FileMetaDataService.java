package com.westernalliancebancorp.positivepay.service;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
/**
 * Interface providing service methods to work with the FileMetaData
 * @author Anand Kumar
 */
public interface FileMetaDataService {
	FileMetaData update(FileMetaData fileMetaData);
	FileMetaData save(FileMetaData fileMetaData);
	void delete(FileMetaData fileMetaData);
	FileMetaData findById(Long id);
	List<FileMetaData> findAll();
	boolean isDuplicate(FileMetaData fileMetaData);
	List<FileMetaData> findAllFilesUploaded();
	List<FileMetaData> findRecentFilesUploaded();
	List<FileMetaData> findDashboardFileMetaData(String companyName, FileMetaData.STATUS status, Date dateRange, boolean isForDay);
	List<String> getAccountNumbersAssociatedWithUploadedFile(Long fileMetaDataId);
	List<RecentFileDto> filterFilesBy(Date uploadDate, List<Long> fileMetaDataIds, String noOfDaysBefore);
	List<Long> findFileMetaDataIdsByAccountNumber(String accountNumber);
}

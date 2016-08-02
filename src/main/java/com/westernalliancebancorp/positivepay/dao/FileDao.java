/**
 * 
 */
package com.westernalliancebancorp.positivepay.dao;

import java.util.Date;
import java.util.List;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.FileMetaData;

/**
 * Data access object interface to work with Sample Model database operations
 * @author Anand Kumar
 *
 */
public interface FileDao extends GenericDao<FileMetaData, Long> {
	List<FileMetaData> findByOriginalFileNameAndChecksum(String originalFileName, String checksum);
	List<FileMetaData> findByChecksum(String checksum);
	List<FileMetaData> findAllFilesUploadedForUserCompany(Company userCompany);
	List<FileMetaData> findRecentFilesUploadedForUserCompany(int maxResults, Company userCompany);
    FileMetaData findByFileName(String mappedFileName);
	List<FileMetaData> findDashboardFileMetaData(String companyName, FileMetaData.STATUS status, Date dateCreated, boolean isForDay);
}

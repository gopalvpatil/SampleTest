package com.westernalliancebancorp.positivepay.service.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.CheckDao;
import com.westernalliancebancorp.positivepay.dao.FileDao;
import com.westernalliancebancorp.positivepay.dto.RecentFileDto;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.Company;
import com.westernalliancebancorp.positivepay.model.FileMetaData;
import com.westernalliancebancorp.positivepay.service.FileMetaDataService;
import com.westernalliancebancorp.positivepay.service.UserService;

/**
 * providing implementation for service methods to work with the FileMetaData.
 * @author Anand Kumar
 */
@Service
public class FileMetaDataServiceImpl implements FileMetaDataService {

    /**
     * The logger object
     */
    @Loggable
    private Logger logger;

    /**
     * The SampleDao dependency
     */
    @Autowired
    private FileDao fileDao;
	@Autowired
	CheckDao checkDao;
	@Autowired
	UserService userService;
	@Value("${maxResults.customer.recentUploads}")
	private int maxResults;
	@Autowired
	BatchDao batchDao;
	@Autowired
	AccountDao accountDao;

    /**
     * Updates FileMetaData to database by calling appropriate dao method.
     *
     * @param fileMetaData
     * @return SampleModel object that was saved
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileMetaData update(FileMetaData fileMetaData) {
        return fileDao.update(fileMetaData);
    }

    /**
     * Saves a FileMetaData to database by calling appropriate dao method.
     *
     * @param fileMetaData
     * @return FileMetaData object that was saved
     * @see com.westernalliancebancorp.positivepay.service.FileMetaDataService#(com.westernalliancebancorp.positivepay.model.FileMetaData)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileMetaData save(FileMetaData fileMetaData) {
        return fileDao.save(fileMetaData);
    }

    /**
     * Deletes the FileMetaData from the database by calling appropriate dao method.
     *
     * @param fileMetaData
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(FileMetaData fileMetaData) {
        fileDao.delete(fileMetaData);
    }

    /**
     * Finds the FileMetaData by given id by calling appropriate dao method.
     *
     * @param id to find the FileMetaData
     * @return FileMetaData object that was saved
     * @see com.westernalliancebancorp.positivepay.service.FileMetaDataService#findById(java.lang.Long)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileMetaData findById(Long id) {
        return fileDao.findById(id);
    }

    /**
     * finds all the FileMetaDatas from the database by calling appropriate dao method.
     *
     * @return List of FileMetaData objects
     * @see com.westernalliancebancorp.positivepay.service.FileMetaDataService#findAll()
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<FileMetaData> findAll() {
        return fileDao.findAll();
    }

    /**
     * This method checks if the file being uploaded is a duplicate file
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean isDuplicate(FileMetaData fileMetaData) {
        List<FileMetaData> fileMetaDataList = fileDao.findByOriginalFileNameAndChecksum(fileMetaData.getOriginalFileName(), fileMetaData.getChecksum());
        if (fileMetaDataList.size() > 0)
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<FileMetaData> findAllFilesUploaded() {
    	Company userCompany = userService.getLoggedInUserCompany();
        return fileDao.findAllFilesUploadedForUserCompany(userCompany);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<FileMetaData> findRecentFilesUploaded() {
    	Company userCompany = userService.getLoggedInUserCompany();
    	return fileDao.findRecentFilesUploadedForUserCompany(maxResults, userCompany);
    }
    
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public List<FileMetaData> findDashboardFileMetaData(String companyName, FileMetaData.STATUS status, Date dateCreated, boolean isForDay) {		
		return fileDao.findDashboardFileMetaData(companyName, status, dateCreated, isForDay);
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
	public List<String> getAccountNumbersAssociatedWithUploadedFile(Long fileMetaDataId) {
		List<Check> checksMatchingFileMetaDataId = checkDao.findAllByFileMetaDataId(fileMetaDataId);
		Set<String> accountNumberSet = new HashSet<String>();
		for(Check check: checksMatchingFileMetaDataId) {
			accountNumberSet.add(check.getAccount().getNumber());
		}
		return new ArrayList<String>(accountNumberSet);
	}
	
	@Override
	public List<RecentFileDto> filterFilesBy(Date uploadDate, List<Long> fileMetaDataIds, String noOfDaysBefore){
		Company userCompany = userService.getLoggedInUserCompany();
		return batchDao.filterFilesBy(userCompany, uploadDate, fileMetaDataIds, noOfDaysBefore);
	}
	
	@Override
	public List<Long> findFileMetaDataIdsByAccountNumber(String accountNumber){
		List<Long> fileMetaDataIds = new ArrayList<Long>();
		if(accountNumber != null && !accountNumber.equalsIgnoreCase("")){
			Account account = accountDao.findByAccountNumber(accountNumber);
			fileMetaDataIds = batchDao.findFileMetaDataIdsByAccountId(account.getId());
		}
		return fileMetaDataIds;
	}
}

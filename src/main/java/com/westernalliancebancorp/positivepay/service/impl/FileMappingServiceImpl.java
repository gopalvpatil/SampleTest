package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.annotation.RollbackForEmulatedUser;
import com.westernalliancebancorp.positivepay.dao.FileMappingDao;
import com.westernalliancebancorp.positivepay.dao.UserDetailDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.FileMapping;
import com.westernalliancebancorp.positivepay.service.FileMappingService;
import com.westernalliancebancorp.positivepay.service.UserService;

/**
 * providing implementation for service methods to work with the FileMapping Model.
 * @author Anand Kumar
 */
@Service
public class FileMappingServiceImpl implements FileMappingService {

	/** The logger object */
	@Loggable
	private Logger logger;
	
	/** The FileMappingDao dependency */
	@Autowired
	private FileMappingDao fileMappingDao;
	
	@Autowired
	private UserDetailDao userDao;

    @Autowired
    UserService userService;



	@Override
	@RollbackForEmulatedUser
	@Transactional(propagation = Propagation.REQUIRED)
	public FileMapping saveOrUpdate(FileMapping fileMapping) {
		if(fileMapping.getDelimiter()== null) {
			fileMapping.setDelimiter(null);
		}
		fileMapping.setCompany(userService.getLoggedInUserCompany());
		if(fileMapping.getId() != null) {
			//Get the current record
			FileMapping fileMappingFromDB = fileMappingDao.findById(fileMapping.getId());
			AuditInfo auditInfo = new AuditInfo();
			auditInfo.setCreatedBy(fileMappingFromDB.getAuditInfo().getCreatedBy());
			auditInfo.setDateCreated(fileMappingFromDB.getAuditInfo().getDateCreated());
			//Set the audit Info in the file mapping.
			fileMapping.setAuditInfo(auditInfo);
			logger.info("File Mapping with id = {} and filename = {} getting updated", fileMapping.getId(), fileMapping.getFileMappingName());
			return fileMappingDao.update(fileMapping);
		}
		else{
			logger.info("File mapping with filename = {} getting created", fileMapping.getFileMappingName());
			return fileMappingDao.save(fileMapping);
		}
	}

	@Override
	@RollbackForEmulatedUser
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(FileMapping fileMapping) {
		if(fileMapping.getDelimiter()== null) {
			fileMapping.setDelimiter(null);
		}
		fileMapping.setCompany(userService.getLoggedInUserCompany());
		fileMappingDao.delete(fileMapping);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public FileMapping findById(Long id) {
		return fileMappingDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FileMapping> findAll() {
		return fileMappingDao.findAll();
	}
	
	@Override
	@RollbackForEmulatedUser
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FileMapping> saveAll(List<FileMapping> fileMappings) {
		return fileMappingDao.saveAll(fileMappings);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public FileMapping findByCompanyIdAndFileMappingId(Long companyId,
			Long fileMappingId) {
		return fileMappingDao.findByCompanyIdAndFileMappingId(companyId, fileMappingId);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<FileMapping> findAllForLoggedInUser() {
		return fileMappingDao.findAllByCompanyId(userService.getLoggedInUserCompany().getId());
	}

}

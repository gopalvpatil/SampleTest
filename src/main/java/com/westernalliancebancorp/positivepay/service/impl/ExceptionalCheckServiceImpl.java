package com.westernalliancebancorp.positivepay.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.dao.ExceptionalCheckDao;
import com.westernalliancebancorp.positivepay.log.Loggable;
import com.westernalliancebancorp.positivepay.model.AuditInfo;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;
import com.westernalliancebancorp.positivepay.service.ExceptionalCheckService;
import com.westernalliancebancorp.positivepay.utility.SecurityUtility;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/12/14
 * Time: 9:43 PM
 */
@Service
public class ExceptionalCheckServiceImpl implements ExceptionalCheckService {
	/** The logger object */
	@Loggable
	private Logger logger;
	@Autowired
	private ExceptionalCheckDao exceptionalCheckDao;
	@Autowired
	private BatchDao batchDao;

    @Override
    public Check moveToCheckTable(ExceptionalCheck exceptionalCheck) {
        //Check if the user can set this account number;
        //Check if the check is already existing in the check's table.
        //
        return null;
    }

    @Override
    public List<Check> moveToCheckTable(List<ExceptionalCheck> exceptionalChecks) {
        return null;
    }    

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalCheck update(ExceptionalCheck check) {
		return exceptionalCheckDao.update(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalCheck save(ExceptionalCheck check) {
		return exceptionalCheckDao.save(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(ExceptionalCheck check) {
		exceptionalCheckDao.delete(check);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ExceptionalCheck findById(Long id) {
		return exceptionalCheckDao.findById(id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalCheck> findAll() {
		return exceptionalCheckDao.findAll();
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalCheck> saveAll(List<ExceptionalCheck> exceptionalCheckList) {
		//Using JDBC Template batch Updates
		List<ExceptionalCheck> exceptionalCheckListToBeSaved = new ArrayList<ExceptionalCheck>();
        String name = SecurityUtility.getPrincipal();
        for (ExceptionalCheck exceptionalCheck : exceptionalCheckList) {
            AuditInfo auditInfo = new AuditInfo();
            auditInfo.setCreatedBy(name);
            auditInfo.setDateCreated(new Date());
            auditInfo.setDateModified(new Date());
            auditInfo.setModifiedBy(name);
            exceptionalCheck.setAuditInfo(auditInfo);
            exceptionalCheckListToBeSaved.add(exceptionalCheck);
        }
        return batchDao.insertAllExceptionalChecks(exceptionalCheckListToBeSaved);
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<ExceptionalCheck> findAllByUserName() {
		 return exceptionalCheckDao.findAllByUserName(SecurityUtility.getPrincipal());
	}

	@Override
	public List<ExceptionalCheck> findErrorRecordsUploadedBy(Long fileMetaDataId) {
		return exceptionalCheckDao.findErrorRecordsUploadedBy(fileMetaDataId);
	}
}

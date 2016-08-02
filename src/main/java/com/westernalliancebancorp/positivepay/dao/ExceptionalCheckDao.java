package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;

/**
 * 
 * @author akumar1
 *
 */
@Repository
public interface ExceptionalCheckDao  extends GenericDao<ExceptionalCheck, Long> {	
	List<ExceptionalCheck> findAllByUserName(String userName);
	List<ExceptionalCheck> findAllExceptionalChecks();
	List<ExceptionalCheck> findErrorRecordsUploadedBy(Long fileMetaDataId);
}

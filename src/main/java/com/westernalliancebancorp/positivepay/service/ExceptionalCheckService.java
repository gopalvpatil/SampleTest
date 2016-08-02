package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionalCheck;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/12/14
 * Time: 9:43 PM
 */
public interface ExceptionalCheckService {
	ExceptionalCheck update(ExceptionalCheck exceptionalCheck);
	ExceptionalCheck save(ExceptionalCheck exceptionalCheck);
	void delete(ExceptionalCheck exceptionalCheck);
	ExceptionalCheck findById(Long id);
	List<ExceptionalCheck> findAll();
    Check moveToCheckTable(ExceptionalCheck exceptionalCheck);
    List<Check> moveToCheckTable(List<ExceptionalCheck> exceptionalChecks);
    List<ExceptionalCheck> saveAll(List<ExceptionalCheck> exceptionalCheckList);
    List<ExceptionalCheck> findAllByUserName();
	List<ExceptionalCheck> findErrorRecordsUploadedBy(Long fileMetaDataId);
}

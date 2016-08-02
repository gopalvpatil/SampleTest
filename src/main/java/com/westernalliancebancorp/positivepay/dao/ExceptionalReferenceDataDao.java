package com.westernalliancebancorp.positivepay.dao;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.ExceptionType;
import org.springframework.stereotype.Repository;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;

/**
 * 
 * @author akumar1
 *
 */
@Repository
public interface ExceptionalReferenceDataDao  extends GenericDao<ExceptionalReferenceData, Long> {
	List<ExceptionalReferenceData> findAllExceptionalReferenceDataByExceptionTypeId(Long exceptionTypeId);
    ExceptionalReferenceData findBy(String traceNumber, String amount, String accountNumber);
    List<ExceptionalReferenceData> findByCheckNumberAndAccountNumber(String checkNumber, String accountNumber);
}

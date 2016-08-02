package com.westernalliancebancorp.positivepay.service;

import java.text.ParseException;
import java.util.List;

import com.westernalliancebancorp.positivepay.exception.WorkFlowServiceException;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.workflow.CallbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;

/**
 * Interface providing service methods to working with exceptional reference data model.
 *
 * @author Anand Kumar
 */
public interface ExceptionalReferenceDataService {
    ExceptionalReferenceData update(ExceptionalReferenceData exceptionalReferenceData);

    ExceptionalReferenceData save(ExceptionalReferenceData exceptionalReferenceData);

    void delete(ExceptionalReferenceData exceptionalReferenceData);

    ExceptionalReferenceData findById(Long id);

    List<ExceptionalReferenceData> findAll();

    List<ExceptionalReferenceData> saveAll(List<ExceptionalReferenceData> exceptionalReferenceDataList);

    Boolean deleteExceptionalReferenceDataRecord(Long exceptionReferenceId, String userComment);

    ExceptionalReferenceData findBy(String traceNumber, String amount, String accountNumber);

    void changeZeroedCheckNumber(Long id, String changedCheckNumber, String userComment) throws ParseException, AccountNotFoundException, CallbackException, WorkFlowServiceException;

    void processDuplicatesInExceptionsReferneceDataWith(String oldCheckNumber, Long oldAccountId) throws ParseException, AccountNotFoundException, CallbackException, WorkFlowServiceException;
}

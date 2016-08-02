package com.westernalliancebancorp.positivepay.service;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;
import com.westernalliancebancorp.positivepay.model.ExceptionalReferenceData;
import com.westernalliancebancorp.positivepay.model.ReferenceData;

import javax.security.auth.login.AccountNotFoundException;
import java.text.ParseException;

/**
 * Interface providing service methods to create Reference Data 
 * @author Moumita Ghosh
 */

public interface ReferenceDataCreationService {
    ReferenceData createNewReferenceDataForCheck(Check check);
    ReferenceData createReferenceData(ExceptionalReferenceData exceptionalReferenceData, Account account) throws ParseException;
    ReferenceData createReferenceData(ExceptionalReferenceData exceptionalReferenceData) throws ParseException, AccountNotFoundException;

    ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData, Account account) throws ParseException;
    ExceptionalReferenceData createExceptionalReferenceData(ReferenceData referenceData) throws ParseException;
}

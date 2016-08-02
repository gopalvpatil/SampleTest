package com.westernalliancebancorp.positivepay.service;

import java.util.List;

import com.westernalliancebancorp.positivepay.model.ExceptionType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ExceptionTypeService {
	List<ExceptionType> getAllExceptionTypes();

    ExceptionType createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE exception_type);
}

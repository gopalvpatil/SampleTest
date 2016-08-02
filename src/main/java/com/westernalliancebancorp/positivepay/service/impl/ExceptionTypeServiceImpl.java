package com.westernalliancebancorp.positivepay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.westernalliancebancorp.positivepay.dao.ExceptionTypeDao;
import com.westernalliancebancorp.positivepay.model.ExceptionType;
import com.westernalliancebancorp.positivepay.service.ExceptionTypeService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExceptionTypeServiceImpl implements ExceptionTypeService{
	
	 @Autowired
	 ExceptionTypeDao exceptionTypeDao;
	 
	@Override
	public List<ExceptionType> getAllExceptionTypes() {
		return exceptionTypeDao.findAll();
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ExceptionType createOrRetrieveExceptionType(ExceptionType.EXCEPTION_TYPE exception_type) {
        ExceptionType exceptionType = exceptionTypeDao.findByName(exception_type);
        if (exceptionType == null) {
            exceptionType = new ExceptionType();
            exceptionType.setActive(Boolean.TRUE);
            exceptionType.setDescription(exception_type.toString());
            exceptionType.setLabel(exception_type.toString());
            exceptionType.setExceptionType(exception_type);
            exceptionTypeDao.save(exceptionType);
        } else {
            if (exceptionType.getLabel() == null || !exceptionType.getLabel().equals(exception_type.toString())) {
                exceptionType.setLabel(exception_type.toString());
                exceptionTypeDao.update(exceptionType);
            }
        }
        return exceptionType;
    }
}

package com.westernalliancebancorp.positivepay.dao;

import com.westernalliancebancorp.positivepay.dao.common.GenericDao;
import com.westernalliancebancorp.positivepay.model.ExceptionType;

/**
 * User: moumita
 * Date: 13/5/14
 * Time: 3:59 PM
 */
public interface ExceptionTypeDao extends GenericDao<ExceptionType, Long> {
    ExceptionType findByName(ExceptionType.EXCEPTION_TYPE name);
}

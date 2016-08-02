package com.westernalliancebancorp.positivepay.dao.impl.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.westernalliancebancorp.positivepay.dto.ExceptionCheckDto;

/**
 * Created with IntelliJ IDEA.
 * User: moumita
 * Date: 25/4/14
 * Time: 8:05 PM
 */
public class ExceptionCheckDtoRowMapper implements RowMapper<ExceptionCheckDto> {
    @Override
    public ExceptionCheckDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    	ExceptionCheckDto expCheckDto = new ExceptionCheckDto();
        expCheckDto.setId(resultSet.getLong("ID"));
        expCheckDto.setExceptionCheckId((Long)(resultSet.getLong("EXC_CHECK_ID")));
        return expCheckDto;
    }
}

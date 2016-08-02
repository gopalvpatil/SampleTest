package com.westernalliancebancorp.positivepay.dao.impl.jdbc;


import com.westernalliancebancorp.positivepay.dto.CheckDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 25/3/14
 * Time: 8:05 PM
 */
public class CheckDtoRowMapper implements RowMapper<CheckDto> {
    @Override
    public CheckDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        CheckDto checkDto = new CheckDto();
        checkDto.setId(resultSet.getLong("ID"));
        checkDto.setReferenceDataId((Long)(resultSet.getLong("REF_DATA_ID")));
        return checkDto;
    }
}

package com.westernalliancebancorp.positivepay.dao.impl.jdbc;

import com.westernalliancebancorp.positivepay.dto.UserDto;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 5/5/14
 * Time: 4:36 PM
 */
public class UserDtoRowMapper implements RowMapper<UserDto> {
    @Override
    public UserDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserDto userDto = new UserDto();
        userDto.setUserId(resultSet.getLong("id"));
        userDto.setUserName(resultSet.getString("userName"));
        userDto.setFirstName(resultSet.getString("firstName"));
        userDto.setLastName(resultSet.getString("lastName"));
        userDto.setEmail(resultSet.getString("emailAddress"));
        return userDto;
    }
}

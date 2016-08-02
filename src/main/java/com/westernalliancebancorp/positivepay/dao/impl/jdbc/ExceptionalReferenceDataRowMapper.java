package com.westernalliancebancorp.positivepay.dao.impl.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.westernalliancebancorp.positivepay.dao.ExceptionalReferenceDataDao;
import com.westernalliancebancorp.positivepay.dao.ReferenceDataDao;
import com.westernalliancebancorp.positivepay.dto.ExceptionalReferenceDataDto;

/**
 * Class that maps resultset to ExceptionalReferenceData 
 * @author Moumita Ghosh
 *
 */
@Component
public class ExceptionalReferenceDataRowMapper implements RowMapper<ExceptionalReferenceDataDto> {
	
    @Autowired
    ReferenceDataDao referenceDataDao;
    
    @Autowired
    ExceptionalReferenceDataDao expReferenceDataDao;
	
    @Override
    public ExceptionalReferenceDataDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    	
    	ExceptionalReferenceDataDto exceptionalReferenceDataDto = new ExceptionalReferenceDataDto();
    	Long referenceDataId = resultSet.getLong("referenceDataId");
    	exceptionalReferenceDataDto.setReferenceDataId(referenceDataId);
    	
    	Long expReferenceDataId = resultSet.getLong("expReferenceDataId");
    	exceptionalReferenceDataDto.setExpReferenceDataId(expReferenceDataId);
    	
        return exceptionalReferenceDataDto;
    }
}

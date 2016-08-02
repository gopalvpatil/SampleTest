package com.westernalliancebancorp.positivepay.dao.impl.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import com.westernalliancebancorp.positivepay.model.FileMetaData;
import org.springframework.jdbc.core.RowMapper;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.ReferenceData;

/**
 * Class that maps resultset to ReferenceData wherever account information also needs to be populated.
 * @author akumar1
 *
 */
public class ReferenceDataRowMapper implements RowMapper<ReferenceData> {
    @Override
    public ReferenceData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    	ReferenceData referenceData = new ReferenceData();
    	Account account = new Account();
    	account.setId(resultSet.getLong("ACCOUNT_ID"));
    	account.setName(resultSet.getString("ACCOUNT_NAME"));
    	account.setNumber(resultSet.getString("ACCOUNT_NUMBER"));
        referenceData.setAccount(account);

        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setId(resultSet.getLong("FILE_META_DATA_ID"));
        referenceData.setFileMetaData(fileMetaData);
        //make all the reference data items
        referenceData.setAmount(resultSet.getBigDecimal("AMOUNT"));
        referenceData.setCheckNumber(resultSet.getString("CHECK_NUMBER"));
        referenceData.setDigest(resultSet.getString("DUPLICATE_IDENTIFIER"));
        referenceData.setId(resultSet.getLong("ID"));
        
        if (resultSet.getString("ITEM_TYPE").equalsIgnoreCase(ReferenceData.ITEM_TYPE.PAID.toString())) {
        	referenceData.setItemType(ReferenceData.ITEM_TYPE.PAID);
        } else { 
        	referenceData.setItemType(ReferenceData.ITEM_TYPE.STOP);
        }
        referenceData.setLineNumber(resultSet.getString("FILE_IMPORT_LINE_NUMBER"));
        referenceData.setPaidDate(resultSet.getDate("PAID_DATE"));
        referenceData.setAssignedBankNumber(resultSet.getShort("ASSIGNED_BANK_NUMBER"));
        
        if (resultSet.getString("STATUS").equalsIgnoreCase(ReferenceData.STATUS.NOT_PROCESSED.toString())) {
        	referenceData.setStatus(ReferenceData.STATUS.NOT_PROCESSED);
        } else if (resultSet.getString("STATUS").equalsIgnoreCase(ReferenceData.STATUS.PROCESSED.toString())) {
        	referenceData.setStatus(ReferenceData.STATUS.PROCESSED);
        } else if (resultSet.getString("STATUS").equalsIgnoreCase(ReferenceData.STATUS.DELETED.toString())) {
        	referenceData.setStatus(ReferenceData.STATUS.DELETED);
        } else if (resultSet.getString("STATUS").equalsIgnoreCase(ReferenceData.STATUS.DUPLICATE_EXCEPTION.toString())) {
        	referenceData.setStatus(ReferenceData.STATUS.DUPLICATE_EXCEPTION);
        }    
        
        referenceData.setStopDate(resultSet.getDate("STOP_DATE"));
        referenceData.setTraceNumber(resultSet.getString("TRACE_NUMBER"));
        return referenceData;
    }
}

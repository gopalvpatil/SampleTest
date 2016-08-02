package com.westernalliancebancorp.positivepay.dao.impl.jdbc;


import java.sql.ResultSet;
import java.sql.SQLException;

import com.westernalliancebancorp.positivepay.model.ItemType;
import org.springframework.jdbc.core.RowMapper;

import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.Check;

/**
 * Class that maps resultset to Check wherever account information also needs to be populated.
 * @author akumar1
 *
 */
public class CheckRowMapper implements RowMapper<Check> {
    @Override
    public Check mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    	Check check = new Check();
    	Account account = new Account();
    	account.setId(resultSet.getLong("ACCOUNT_ID"));
    	account.setName(resultSet.getString("ACCOUNT_NAME"));
    	account.setNumber(resultSet.getString("ACCOUNT_NUMBER"));
        check.setAccount(account);

        ItemType itemType = new ItemType();
        itemType.setId(resultSet.getLong("ITEM_TYPE_ID"));
        itemType.setItemCode(resultSet.getString("ITEM_CODE"));
        itemType.setName(resultSet.getString("ITEM_NAME"));
        itemType.setDescription(resultSet.getString("ITEM_DESCRIPTION"));
        //make all the reference data items
        check.setCheckNumber(resultSet.getString("CHECK_NUMBER"));
        check.setDigest(resultSet.getString("DUPLICATE_IDENTIFIER"));
        check.setId(resultSet.getLong("ID"));
        check.setItemType(itemType);
        check.setIssuedAmount(resultSet.getBigDecimal("ISSUED_AMOUNT"));
        check.setIssueDate(resultSet.getDate("ISSUE_DATE"));
        //check.setLineItem(resultSet.getString("LINE_ITEM"));
        check.setLineNumber(resultSet.getString("FILE_IMPORT_LINE_NUMBER"));
        check.setPayee(resultSet.getString("PAYEE"));
        check.setRoutingNumber(resultSet.getString("ROUTING_NUMBER"));
        check.setVoidDate(resultSet.getDate("VOID_DATE"));
        return check;
    }
}

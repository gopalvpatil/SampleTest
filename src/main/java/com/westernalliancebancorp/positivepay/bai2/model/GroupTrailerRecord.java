package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The group trailer record provides group level control totals. There must be one 98 record for each 02 record. All fields
 * are required.
 * Field Name                             Field description
 * Record Code                            98
 * Group Control                          Total Algebraic sum of account control totals in this group. This field
 * includes the sign ("+" or "-") for the total. If no sign precedes the
 * total, the default is positive.
 *
 * Number of Accounts                      Integer. The number of 03 records in this group.
 *
 * Number of Records                       Integer. The total number of all records in this group. Include the
 *                                         02, all 03, 16, 49, and 88 records, and this 98 record.
 *
 * Delimiters: Comma "," follows "Record Code," "Group Control Total," and "Number of
 * Accounts." Slash "/" follows "Number of Records" and indicates the end of
 * the logical record.
 *
 * Sample 98 record:
 * 98,11800000,2,6/
 *
 * This group trailer record contains the group control total (11800000) which is the algebraic sum of all account
 * control totals in this group. The number of accounts is two (2), reflecting the two account records (record type
 * 03) in this group. The number of records in this group (6) includes the 02 record, all 03, 16, 88, and 49 records
 * and this 98 record.
 *
 * @author Giridhar Duggirala
 */

public class GroupTrailerRecord {
    public static final String recordCode = "98";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(4);

    public GroupTrailerRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }
    public String getRecordCode() {
        return recordValuesMap.get(0);
    }
    public String getGroupControlTotal() {
        return recordValuesMap.get(1);
    }
    public String getNumberOfAccounts() {
        return recordValuesMap.get(2);
    }
    public String getNumberOfRecords() {
        return recordValuesMap.get(3);
    }
}

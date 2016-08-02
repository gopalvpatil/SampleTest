package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The account trailer record provides account level control totals. There must be one 49 record for each 03 record. All 16
 * and 88 records between the 03 record and the 49 record refer to the account identified in the 03 record. All fields are
 * required.
 * Field Name                                                                 Field description
 * Record Code                                                                49
 * Account Control                                                            Total Algebraic sum of all "Amount" fields in the preceding type 03 record
 *                                                                             and all type 16 and 88 records associated with that account. The
 *                                                                             total does not include amounts reported in "Funds Type" or "Item
 *                                                                             Count" fields. This field includes the sign "+" or "-" for the total. If
 *                                                                             no sign precedes the total, the default is positive.
 * Number of Records                                                          Integer. The total number of records in the account, including the 03
 *                                                                             record and all 16 and 88 records, and including this account trailer
 *                                                                             49 record.
 * Delimiters: Comma "," follows "Record Code" and "Account Control Total." Slash "/"
 * follows "Number of Records" and indicates the end of the logical record.
 * Sample 49 record:
 * 49,18650000,3/
 * The account trailer record contains the account control total (18650000) which is the algebraic sum of "Amounts"
 * in all records back to and including the preceding 03 record. The account control total does not include amounts
 * in "funds type" fields. The number of records (3) includes the 03 record, a detail (16) or continuation (88) record,
 * and this account trailer (49) record.
 *
 * @author Giridhar Duggirala
 */

public class AccountTrailerRecord {
    public static final String recordCode = "49";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(8);

    public AccountTrailerRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }

    public String getRecordCode() {
        return recordValuesMap.get(0);
    }

    public String getAccountControlTotal() {
        return recordValuesMap.get(0);
    }

    public String getNumberOfRecords() {
        return recordValuesMap.get(0);
    }

}

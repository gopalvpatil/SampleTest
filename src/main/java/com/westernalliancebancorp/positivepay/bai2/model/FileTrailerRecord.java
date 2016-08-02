package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * The file trailer record provides file control totals. There must be one 99 record for each file. The 99 record indicates the
 * end of the logical file. All fields are required.
 * Field Name                                 Field description
 * Record Code                                99
 * FIle Control                               Total Algebraic sum of all group control totals in this file. This field
 * includes the sign ("+" or "-") for the total. If no sign precedes the
 * total, the default is positive.
 * Number of Groups                           Number of 02 records in this file.
 * Number of Records                          Total number of records of all codes in the file, including continuation records, headers and trailers (and including this 99
 * record); exclude any device-oriented records, JCL, tape marks, and so on.
 *
 * Delimiters: Comma "," follows "Record Code," "File Control Total," and "Number of Groups."
 *
 * Slash "/" follows "Number of Records" and indicates the end of the logical
 * record.
 *
 * Sample 99 record:
 * 99,1215450000,4,36/
 *
 * The file trailer record contains the file control total (1215450000) which is the algebraic sum of all group control
 * totals for this file. The number of groups (4) is the number of 02 records in this file. The number of records (36)
 * is the total number of records in this file, including this 99 record.
 *
 * @author Giridhar Duggirala
 */

public class FileTrailerRecord {
    public static final String recordCode = "99";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(4);
    public FileTrailerRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }
    public String getRecordCode() {
        return recordValuesMap.get(0);
    }
    public String getFileControlTotal() {
        return recordValuesMap.get(1);
    }
    public String getNumberOfGroups() {
        return recordValuesMap.get(2);
    }
    public String getNumberOfRecords() {
        return recordValuesMap.get(3);
    }
}

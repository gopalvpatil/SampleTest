package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * <p/>
 * 02 - GROUP HEADER
 * The group header identifies a group of accounts, all of which are from the same originator and include the same as-ofdate.
 * All fields are required except those labeled optional.
 * Field Name                                                                             Field description
 * Record Code                                                                            02
 * Ultimate Receiver Identification                                                       Optional. Final receiver of this group of data. Alphanumeric.
 * Originator Identification Originator of the group of data. Alphanumeric.
 * Group Status                                                                            1 = Update
 *                                                                                          2 = Deletion.
 *                                                                                         3 = Correction.
 *                                                                                          4 = Test Only.
 *                                                                                          See "Group Status Codes" for definitions.
 * As-of-date                                                                             YYMMDD; Originator date.
 * As-of-time                                                                             Optional. Military format (2400); originator time zone. For reference only.
 * Currency Code Optional.                                                                Default is currency code "USD". See Appendix B for currency codes.
 * As-of-date                                                                             Modifier Optional.
 *                                                                                          1 = Interim previous-day data.
 *                                                                                          2 = Final previous-day data.
 *                                                                                          3 = Interim same-day data.
 *                                                                                          4 = Final same-day data.
 *                                                                                          As-of-date modifier does not affect processing. For reference only.
 * <p/>
 * Delimiters: Comma "," delimits fields.
 * Slash "/" delimits the end of the logical record. Adjacent delimiters ",," indicate
 * defaulted or unspecified fields.
 * All defaulted or unspecified fields must be identified.
 * <p/>
 * Example #3 - Sample 02 Record:
 * 02,031001234,122099999,1,940620,2359,,2/
 * <p/>
 * A group of data is being sent to a bank (031001234) from Last National Bank (122099999). The data in the
 * file has a group status of update (1) and the data are as-of-June 20, 1994 (940620) at 11:59 p.m. (2359). The
 * optional group currency code field is defaulted as indicated by the adjacent delimiters (,,) and therefore is USD
 * (U.S. dollars). The data are final previous-day data as signified by the as-of-date modifier (2).
 *
 * @author Giridhar Duggirala
 */

public class GroupHeaderRecord {
    public static final String recordCode = "02";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(8);

    public GroupHeaderRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }

    public String getRecordCode() {
        return recordValuesMap.get(0);
    }

    public String getUltimateReceiverIdentification() {
        return recordValuesMap.get(1);
    }

    public String getOriginatorIdentification() {
        return recordValuesMap.get(2);
    }

    public String getGroupStatus() {
        return recordValuesMap.get(3);
    }

    public String getAsOfDate() {
        return recordValuesMap.get(4);
    }

    public String getAsOfTime() {
        return recordValuesMap.get(5);
    }

    public String getCurrencyCode() {
        return recordValuesMap.get(6);
    }

    public String getAsOfDateModifier() {
        return recordValuesMap.get(7);
    }
}

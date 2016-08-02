package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This record reports transaction detail with accompanying text and reference numbers. Only one detail transaction may
 * be reported by each Type 16 record. Record 16 cannot report status or summary amounts. Status and summary are
 * reported in record 03. Transaction detail records report activity in accounts identified by 03 records. All Type 16
 * records following an 03 record refer to the account identified in the "Customer Account Number" field (See Record
 * Type 03). All fields are required except those labeled optional.
 * <p/>
 * Field Name                                                                     Field description
 * Record Code                                                                    16
 * Type Code                                                                      Identifies the type of detail data. See Appendix A for type codes.
 * Amount                                                                         Optional. Always positive (unsigned). Expressed without a decimal.
 *                                                                                Currency Code in a group header record or in an account identifier
 *                                                                                record determines implied decimal. Default indicates that no amount
 *                                                                                is being reported.
 * Funds Type                                                                     Optional. Types are:
 *                                                                                0 = immediate availability (zero).
 *                                                                                1 = one-day availability.
 *                                                                                2 = two-or-more-day availability.
 *                                                                                S = distributed availability.
 *                                                                                V = value dated.
 *                                                                                D = distributed availability.
 *                                                                                Z = unknown (default).
 * If funds type = S, the next three fields are immediate availability amount, one-day availability amount, and more than one-day availability amount.
 * If funds type = V, the next two fields are value date (YYMMDD) and 9 value time in military format (2400). Both are for the originator's business date and time zone. The value date is the date the originator
 * makes funds available to the customer. Value time is optional and may be defaulted by adjacent delimiters.
 * If funds type = D, the next field indicates the number of availability distributions and each following pair of fields indicate the number of days and the amount available, respectively.
 * Format - "X,a,$,a,$"
 * X = number of distributions (integer).
 * a = availability in days (integer).
 * $ = available amount.
 * See section "Funds Type."
 * Amounts in the funds type field have the same currency code and implied decimals as the "amount" following the type code. Amounts in the funds type field are not included in the account, group and file
 * trailer batch control totals.
 * Bank Reference Number                                                            Optional. Alphanumeric field defined by the originator. Must not
 * contain a comma "," or a slash "/".
 * Customer Reference Number                                                        Optional. Alphanumeric field defined by the originator. Must not
 * contain a comma "," or a slash "/".
 * Text Optional alphanumeric field defined by the originator. Must not
 * begin with a slash "/", but may contain a comma "," or a slash "/"
 * after the first character.
 * Delimiters: Comma "," delimits fields.
 * Adjacent delimiters ",," indicate defaulted or unspecified fields.
 * The end of the "text" field is indicated by the beginning of the next record (unless it is a type 88 continuation record). Spaces between the end of the text and the end of the physical record must be filled with blanks if fixed
 * length records are used. If the type 16 record does not include text, the end of the logical record is indicated by the adjacent delimiters ",/" following the Customer Reference Number field.
 * 10
 *
 * Example # - Sample 16 record:
 * 16,165,1500000,1,DD1620,,DEALER PAYMENTS
 *
 * This is a detail record (16). The amount for type code 165 is $15,000.00 (1500000) and has one-day (1)
 * deferred availability (1). The bank reference number is (DD1620). There is no customer reference number (,,).
 * The text is (DEALER PAYMENTS). The remainder of the field is blank filled if fixed length records are used, and
 * the text field is delimited by the fact that the next record is not "88".
 *
 * @author Giridhar Duggirala
 */

public class TransactionDetailRecord {
    public static final String recordCode = "16";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(8);

    public TransactionDetailRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }

    public String getRecordCode() {
        return recordValuesMap.get(0);
    }

    public String getTypeCode() {
        return recordValuesMap.get(1);
    }

    public String getAmount() {
        return recordValuesMap.get(2);
    }

    public String getFundsType() {
        return recordValuesMap.get(3);
    }

    public String getBankReferenceNumber() {
        return recordValuesMap.get(4);
    }

    public String getCustomerReferenceNumber() {
        return recordValuesMap.get(5);
    }

    public String getText() {
        return recordValuesMap.get(6);
    }
}

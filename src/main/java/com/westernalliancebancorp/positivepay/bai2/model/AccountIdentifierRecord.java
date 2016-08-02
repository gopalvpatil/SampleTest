package com.westernalliancebancorp.positivepay.bai2.model;

import java.util.HashMap;
import java.util.Map;

/**
 * This record identifies the account number and reports summary and status information. Summary information may be
 * accompanied by an item count and funds availability distribution. An 03 record must be used to identify each account.
 * 03 records cannot report transaction detail. Detail is reported in record 16. All fields are required except those labeled
 * optional.
 * Field Name                                                                            Field description
 * Record Code                                                                           03
 * Customer Account Number                                                               Customer account number at originator financial institution. The field
 * is alphanumeric and includes significant leading zeroes. Must not
 * contain a comma "," or slash "/".
 * Currency Code                                                                         Optional. Default is group currency code.
 * Type Code * Optional. Identifies the type of summary or status data. See
 * Appendix A for type codes. Default indicates that no status or
 * summary data are being reported.
 * Amount*                                                                               Optional. Expressed without a decimal. The currency code will
 * determine the implied decimal. Status amounts are signed positive
 * "+" or negative "-". Default of sign is positive. Summary amounts
 * may only be positive or unsigned. Default of field indicates that no
 * amount is being reported.
 * Item Count**                                                                           Optional. Integer field. Default is "unknown." For summary type
 * codes only; must be defaulted for status type codes. No implied
 * decimal.
 * Funds Type**                                                                           Optional. Types are:
 * 0 = immediate availability (zero).
 * 1 = one-day availability.
 * 2 = two-or-more days availability.
 * S = distributed availability.
 * V = value dated.
 * D = distributed availability.
 * Z = unknown (default).
 * If funds type = S, the next three fields are immediate availability
 * amount, one-day availability amount, and more than one-day
 * availability amount. See section "Funds Type."
 * If funds type = V, the next two fields are value date (YYMMDD)
 * 7
 * and value time in military format (2400). Both are for the
 * originator's business day and time zone. Value date is the date the
 * originator makes funds available to the customer. Value time is
 * optional and may be defaulted by adjacent delimiters.
 * If funds type = D, the next field indicates the number of availability
 * distributions, and each of the following pair of fields indicate the
 * number of days and the amount of available funds, respectively.
 * Format - "X, a, $, a, $."
 * X = number of distributions (integer)
 * a = availability in days (integer)
 * $ = available amount
 * See Section "Funds Type."
 * Amounts in the funds type field have the same currency code and
 * implied decimals specified in the 03 record. Amounts in the funds
 * type field are not included in the account, group and file trailer
 * batch control totals.
 * <p/>
 * Type 03 records may report several different status and/or summary amounts for the same account. For example, a
 * single 03 record might report ledger balance and available balance as well as the amount, item count and funds type
 * for total credits and total debits. The "Type Code," "Amount," "Item Count" and "Funds Type" fields are repeated to
 * identify each status or summary type. See Appendix A for Type Codes.
 * <p/>
 * *Type 03 records allow the reporting of item counts and funds availability for summary data only. Status availability is
 * reported by individual type codes (e.g., type code 072, one-day float). The "Item Count" and "Funds Type" fields
 * following a status amount should be defaulted by adjacent delimiters.
 * <p/>
 * Delimiters: Comma "," delimits fields.
 * Slash "/" delimits the end of the logical record. Adjacent delimiters ",," or ",/" indicate
 * defaulted or unspecified fields. All defaulted or unspecified fields must be identified.
 * Note: An 03 record must include an account number but might not include status or summary data. For
 * example, an 03 record would not report status or summary data if it is used only to identify the
 * account number for transaction detail records (16) that follow. In this case, the account number
 * would be followed by five commas and a slash ",,,,,/" to delimit the Currency Code, Type Code,
 * Amount, Item Count and Funds Type fields, which are defaulted (e.g. 04,5765432,,,,,/).
 * 8
 * <p/>
 * Example #4 - Sample 03 Record:
 * 03,0975312468,,010,500000,,,190,70000000,4,0/
 * <p/>
 * Data in this record are for the sending bank's account number (0975312468). The leading zero in the account
 * number is significant and must be included in the data. The optional currency code is defaulted to the group
 * currency code. The amount for type code (010) is $5,000.00 (500000). The item count and funds type field are
 * defaulted to "unknown" as indicated by adjacent delimiters (,,,). The amount for type code (190) is $700,000.00
 * (70000000). The item count for this amount is four (4) and the availability is immediate (0).
 *
 * @author Giridhar Duggirala
 */

public class AccountIdentifierRecord {
    public static final String recordCode = "03";
    private Map<Integer, String> recordValuesMap = new HashMap<Integer, String>(8);

    public AccountIdentifierRecord() {
        recordValuesMap.put(0, recordCode);
        for (int i = 1; i < 8; i++) {
            recordValuesMap.put(i, "");
        }
    }

    public String getRecordCode() {
        return recordValuesMap.get(0);
    }

    public String getCustomerAccountNumber() {
        return recordValuesMap.get(1);
    }

    public String getCurrencyCode() {
        return recordValuesMap.get(2);
    }

    public String getTypeCode() {
        return recordValuesMap.get(3);
    }

    public String getAmount() {
        return recordValuesMap.get(4);
    }

    public String getItemCount() {
        return recordValuesMap.get(5);
    }

    public String getFundsType() {
        return recordValuesMap.get(6);
    }

}

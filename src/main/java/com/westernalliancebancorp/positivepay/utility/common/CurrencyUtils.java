package com.westernalliancebancorp.positivepay.utility.common;

import java.text.DecimalFormat;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/20/14
 * Time: 4:48 AM
 */
public class CurrencyUtils {
    public static String getWalFormattedCurrency(float amount) {
        DecimalFormat moneyFormat = new DecimalFormat(".00");
        return moneyFormat.format(amount);
    }
}

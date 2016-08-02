package com.westernalliancebancorp.positivepay.utility.common;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 3/20/14
 * Time: 9:35 PM
 */
public class CurrencyUtilsTest {
    @Test
    public void testGetWalFormattedCurrency() throws Exception {
        System.out.println(CurrencyUtils.getWalFormattedCurrency(123));
    }
}

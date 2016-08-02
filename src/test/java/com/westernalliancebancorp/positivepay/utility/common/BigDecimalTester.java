package com.westernalliancebancorp.positivepay.utility.common;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 24/4/14
 * Time: 3:01 PM
 */
public class BigDecimalTester {
    public static void main(String a[]){
        BigDecimal small = new BigDecimal(100);
        BigDecimal big = new BigDecimal(150);
        System.out.println(big.compareTo(small));
    }
}

package com.westernalliancebancorp.positivepay.utility;

import org.apache.commons.lang.RandomStringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * RandomTest is
 *
 * @author Giridhar Duggirala
 */

public class RandomTest {
    public static void main(String a[]){
        SecurityUtility.setTransactionId();
        System.out.println(Log.event(Event.LOGIN_SUCCESS, "Test message"));
        int randomLimit = 1000000;
        Set<String> storeString = new HashSet<String>(randomLimit);

        for(int i=0;i<randomLimit;i++){
            String randomString = RandomStringUtils.random(6, true, true);
            //System.out.println(i+" Randomized number "+ randomString);
            if(!storeString.add(randomString)){
                System.out.println("Already generated "+randomString);
            }
        }
    }
}

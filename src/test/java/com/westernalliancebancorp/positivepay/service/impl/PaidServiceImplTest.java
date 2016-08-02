package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.PaidService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * User: gduggirala
 * Date: 20/6/14
 * Time: 1:37 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class PaidServiceImplTest {
    @Autowired
    PaidService  paidService;

    @Before
    public void before() {
        PositivePayThreadLocal.set("jenos");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));;
    }

    @Test
    public void testPaidService() throws Exception{
        List<Long> accountIds = new ArrayList<Long>();
        accountIds.add(7l);
        paidService.markChecksPaidByAccountIds(accountIds);
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.CrsPaidJobService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: gduggirala
 * Date: 22/5/14
 * Time: 1:45 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
public class CrsPaidJobServiceImplTest {
    @Autowired

    CrsPaidJobService crsPaidJobService;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    //@Ignore
    public void testCrsPull() throws Exception{
        crsPaidJobService.pullCrsPaidFile();
    }

}

package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.CrsPaidJobService;
import com.westernalliancebancorp.positivepay.service.DailyStopFileJobService;
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
 * Date: 15/6/14
 * Time: 11:20 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
public class DailyStopFileJobServiceImplTest {
    @Autowired

    DailyStopFileJobService dailyStopFileJobService;

    @Before
    public void before() {
        PositivePayThreadLocal.set("gduggira");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    //@Ignore
    public void testStopPull() throws Exception{
        dailyStopFileJobService.pullStopFile();
    }
}

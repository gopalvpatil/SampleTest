package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.service.IssuedAfterVoidService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: gduggirala
 * Date: 30/5/14
 * Time: 4:48 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class IssuedAfterVoidServiceImplTest {
    @Autowired
    IssuedAfterVoidService issuedAfterVoidService;

    @Test
    public void testMarkAllChceksIssuedAfterVoid() throws Exception{
        issuedAfterVoidService.markChecksIssuedAfterVoid();
    }
}

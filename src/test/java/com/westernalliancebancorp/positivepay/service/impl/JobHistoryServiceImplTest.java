package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.service.JobHistoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 7/7/14
 * Time: 12:13 PM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class JobHistoryServiceImplTest {
    @Autowired
    JobHistoryService jobHistoryService;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testFindAllJobs(){
        jobHistoryService.findAllJobs();
    }
}

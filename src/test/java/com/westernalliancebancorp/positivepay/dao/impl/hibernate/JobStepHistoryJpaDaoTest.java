package com.westernalliancebancorp.positivepay.dao.impl.hibernate;

import com.westernalliancebancorp.positivepay.dao.JobStepHistoryDao;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: gduggirala
 * Date: 23/5/14
 * Time: 4:26 PM
 */


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class JobStepHistoryJpaDaoTest {
    @Autowired
    JobStepHistoryDao jobStepHistoryDao;

    @Test
    @Ignore
    public void testFindJobStepHistoryBy(){
        jobStepHistoryDao.findJobStepHistoryByJobId(0l);
    }
}

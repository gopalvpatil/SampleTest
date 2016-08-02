package com.westernalliancebancorp.positivepay.service.impl;

import ch.lambdaj.Lambda;
import com.westernalliancebancorp.positivepay.dao.AccountDao;
import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.model.Account;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.service.StopNotIssuedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * User: gduggirala
 * Date: 20/6/14
 * Time: 5:59 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class StopNotIssuedServiceImplTest {
    @Autowired
    StopNotIssuedService stopNotIssuedService;

    @Autowired
    BatchDao batchDao;

    @Autowired
    AccountDao accountDao;
    @Before
    public void before() {
        PositivePayThreadLocal.set("neverMind");
    }

    @Test
    public void testStopNotIssued() throws Exception {
        List<Long> accountIds = Lambda.extract(accountDao.findAll(), Lambda.on(Account.class).getId());
        stopNotIssuedService.markChecksStopNotIssuedByAccountIds(accountIds);
    }
}

package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.dao.BatchDao;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
import com.westernalliancebancorp.positivepay.service.InvalidAmountService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * User: gduggirala
 * Date: 15/6/14
 * Time: 5:08 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class InvalidAmountServiceImplTest {
    @Autowired
    BatchDao batchDao;

    @Autowired
    InvalidAmountService invalidAmountService;

    @Before
    public void before() {
        PositivePayThreadLocal.set("neverMind");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));;
    }

    @Test
    public void testMarkChecksInvalidAmountByAccountIds() throws Exception{
        List<Long> accountIds = batchDao.getAllAccountsIds();
        invalidAmountService.markChecksInvalidAmountByAccountIds(accountIds);
    }

}

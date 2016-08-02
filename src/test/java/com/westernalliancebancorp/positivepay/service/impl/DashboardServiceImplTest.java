package com.westernalliancebancorp.positivepay.service.impl;

import com.westernalliancebancorp.positivepay.dto.PaymentByDateDto;
import com.westernalliancebancorp.positivepay.service.DashboardService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * User: gduggirala
 * Date: 5/6/14
 * Time: 11:10 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:positivepay-test-context.xml"})
public class DashboardServiceImplTest {
    @Autowired
    DashboardService dashboardService;

    @Test
    @Ignore
    public void testGetAllPaymentsByDate() throws Exception{
        List<PaymentByDateDto> paymentByDateDtoList = dashboardService.getAllPaymentsByDate(null,null, null);
        Assert.notNull(paymentByDateDtoList, "This can be empty but not null");
    }
}

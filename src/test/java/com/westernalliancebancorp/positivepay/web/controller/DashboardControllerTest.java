package com.westernalliancebancorp.positivepay.web.controller;

import com.westernalliancebancorp.positivepay.dto.AccountInfoDto;
import com.westernalliancebancorp.positivepay.dto.AccountInfoForCustomerDashboardDto;
import com.westernalliancebancorp.positivepay.dto.CustomerDashboardDto;
import com.westernalliancebancorp.positivepay.model.interceptor.PositivePayThreadLocal;
import com.westernalliancebancorp.positivepay.model.interceptor.TransactionIdThreadLocal;
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
 * Date: 1/6/14
 * Time: 3:12 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
public class DashboardControllerTest {
    @Autowired
    DashboardController dashboardController;

    @Before
    public void before() {
        PositivePayThreadLocal.set("amorales");
        TransactionIdThreadLocal.set(RandomStringUtils.randomAlphabetic(6));
    }

    @Test
    public void testGetAllAccountsPaymentsData() throws Exception{
        List<CustomerDashboardDto.PaymentData> paymentDataList = dashboardController.getAllAccountsPaymentsData();
        System.out.println(paymentDataList);
    }

    @Test
    public void testCustomerDashboard() throws Exception{
        dashboardController.customerDashboard(null, null);
    }

    @Test
    public void testGetAccountInfoForCustomerDashboard() throws Exception{
        AccountInfoForCustomerDashboardDto accountInfoForCustomerDashboardDto = dashboardController.getAccountInfoForCustomerDashboard();
        System.out.println("AccountInfoDtos: "+accountInfoForCustomerDashboardDto);
    }
}

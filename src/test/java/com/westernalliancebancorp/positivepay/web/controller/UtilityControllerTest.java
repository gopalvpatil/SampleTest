package com.westernalliancebancorp.positivepay.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: gduggirala
 * Date: 6/27/14
 * Time: 12:54 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml"})
public class UtilityControllerTest {
    @Autowired
    UtilityController utilityController;
    HttpServletResponse httpServletResponse;

    @Before
    public void setUp(){
      httpServletResponse = Mockito.mock(HttpServletResponse.class);
    }

    @Test
    public void testSample() throws IOException {
        utilityController.getSample(httpServletResponse);
    }
}

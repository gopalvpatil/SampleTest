package com.westernalliancebancorp.positivepay.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link com.westernalliancebancorp.positivepay.web.controller.PositivePayController}
 * @author <a href="mailto:akumar1@intraedge.com">Anand Kumar</a>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:positivepay-test-context.xml", "file:**/positivepay-servlet.xml"})
@WebAppConfiguration
public class PositivePayControllerTest {
	
	private MockMvc mockMvc;
	

	@Autowired
	private WebApplicationContext webApplicationContext;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//We have to reset our mock between tests because the mock objects
        //are managed by the Spring container. If we would not reset them,
        //stubbing and verified behavior would "leak" from one test to another.
		//Mockito.reset(sampleService);
		//Get the MockMvc set up
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //Build the sampleModel

	}

	/**
	 * hits the URL = /home
	 * @throws Exception
	 */
	@Test
    @Ignore
	public void testHome() throws Exception {
		//Mockito.when(sampleService.findById(1L)).thenReturn(sampleModel);
		mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("site.default.home.page"));
	}

}
